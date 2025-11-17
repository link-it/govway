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
package org.openspcoop2.core.statistiche;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.statistiche.constants.PdndMethods;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiPdnd;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiRichieste;
import java.io.Serializable;


/** <p>Java class for statistiche-pdnd-tracing complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistiche-pdnd-tracing"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="data-tracciamento" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="data-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="data-pubblicazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="pdd-codice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="csv" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="method" type="{http://www.openspcoop2.org/core/statistiche}pdnd-methods" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="stato-pdnd" type="{http://www.openspcoop2.org/core/statistiche}possibili-stati-pdnd" minOccurs="1" maxOccurs="1" default="WAITING"/&gt;
 * 			&lt;element name="tentativi-pubblicazione" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1" default="0"/&gt;
 * 			&lt;element name="force-publish" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="stato" type="{http://www.openspcoop2.org/core/statistiche}possibili-stati-richieste" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tracing-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="error-details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="history" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "statistiche-pdnd-tracing", 
  propOrder = {
  	"dataTracciamento",
  	"dataRegistrazione",
  	"dataPubblicazione",
  	"pddCodice",
  	"csv",
  	"method",
  	"statoPdnd",
  	"tentativiPubblicazione",
  	"forcePublish",
  	"stato",
  	"tracingId",
  	"errorDetails",
  	"history"
  }
)

@XmlRootElement(name = "statistiche-pdnd-tracing")

public class StatistichePdndTracing extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public StatistichePdndTracing() {
    super();
  }

  public java.util.Date getDataTracciamento() {
    return this.dataTracciamento;
  }

  public void setDataTracciamento(java.util.Date dataTracciamento) {
    this.dataTracciamento = dataTracciamento;
  }

  public java.util.Date getDataRegistrazione() {
    return this.dataRegistrazione;
  }

  public void setDataRegistrazione(java.util.Date dataRegistrazione) {
    this.dataRegistrazione = dataRegistrazione;
  }

  public java.util.Date getDataPubblicazione() {
    return this.dataPubblicazione;
  }

  public void setDataPubblicazione(java.util.Date dataPubblicazione) {
    this.dataPubblicazione = dataPubblicazione;
  }

  public java.lang.String getPddCodice() {
    return this.pddCodice;
  }

  public void setPddCodice(java.lang.String pddCodice) {
    this.pddCodice = pddCodice;
  }

  public byte[] getCsv() {
    return this.csv;
  }

  public void setCsv(byte[] csv) {
    this.csv = csv;
  }

  public void setMethodRawEnumValue(String value) {
    this.method = (PdndMethods) PdndMethods.toEnumConstantFromString(value);
  }

  public String getMethodRawEnumValue() {
    if(this.method == null){
    	return null;
    }else{
    	return this.method.toString();
    }
  }

  public org.openspcoop2.core.statistiche.constants.PdndMethods getMethod() {
    return this.method;
  }

  public void setMethod(org.openspcoop2.core.statistiche.constants.PdndMethods method) {
    this.method = method;
  }

  public void setStatoPdndRawEnumValue(String value) {
    this.statoPdnd = (PossibiliStatiPdnd) PossibiliStatiPdnd.toEnumConstantFromString(value);
  }

  public String getStatoPdndRawEnumValue() {
    if(this.statoPdnd == null){
    	return null;
    }else{
    	return this.statoPdnd.toString();
    }
  }

  public org.openspcoop2.core.statistiche.constants.PossibiliStatiPdnd getStatoPdnd() {
    return this.statoPdnd;
  }

  public void setStatoPdnd(org.openspcoop2.core.statistiche.constants.PossibiliStatiPdnd statoPdnd) {
    this.statoPdnd = statoPdnd;
  }

  public java.lang.Integer getTentativiPubblicazione() {
    return this.tentativiPubblicazione;
  }

  public void setTentativiPubblicazione(java.lang.Integer tentativiPubblicazione) {
    this.tentativiPubblicazione = tentativiPubblicazione;
  }

  public boolean isForcePublish() {
    return this.forcePublish;
  }

  public boolean getForcePublish() {
    return this.forcePublish;
  }

  public void setForcePublish(boolean forcePublish) {
    this.forcePublish = forcePublish;
  }

  public void setStatoRawEnumValue(String value) {
    this.stato = (PossibiliStatiRichieste) PossibiliStatiRichieste.toEnumConstantFromString(value);
  }

  public String getStatoRawEnumValue() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.statistiche.constants.PossibiliStatiRichieste getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.statistiche.constants.PossibiliStatiRichieste stato) {
    this.stato = stato;
  }

  public java.lang.String getTracingId() {
    return this.tracingId;
  }

  public void setTracingId(java.lang.String tracingId) {
    this.tracingId = tracingId;
  }

  public java.lang.String getErrorDetails() {
    return this.errorDetails;
  }

  public void setErrorDetails(java.lang.String errorDetails) {
    this.errorDetails = errorDetails;
  }

  public int getHistory() {
    return this.history;
  }

  public void setHistory(int history) {
    this.history = history;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.statistiche.model.StatistichePdndTracingModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.statistiche.StatistichePdndTracing.modelStaticInstance==null){
  			org.openspcoop2.core.statistiche.StatistichePdndTracing.modelStaticInstance = new org.openspcoop2.core.statistiche.model.StatistichePdndTracingModel();
	  }
  }
  public static org.openspcoop2.core.statistiche.model.StatistichePdndTracingModel model(){
	  if(org.openspcoop2.core.statistiche.StatistichePdndTracing.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.statistiche.StatistichePdndTracing.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-tracciamento",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataTracciamento;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-registrazione",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataRegistrazione;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-pubblicazione",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataPubblicazione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="pdd-codice",required=true,nillable=false)
  protected java.lang.String pddCodice;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.HexBinaryAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="hexBinary")
  @XmlElement(type=String.class, name="csv",required=false,nillable=false)
  protected byte[] csv;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String methodRawEnumValue;

  @XmlElement(name="method",required=false,nillable=false)
  protected PdndMethods method;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String statoPdndRawEnumValue;

  @XmlElement(name="stato-pdnd",required=true,nillable=false,defaultValue="WAITING")
  protected PossibiliStatiPdnd statoPdnd = (PossibiliStatiPdnd) PossibiliStatiPdnd.toEnumConstantFromString("WAITING");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="tentativi-pubblicazione",required=true,nillable=false,defaultValue="0")
  protected java.lang.Integer tentativiPubblicazione = java.lang.Integer.valueOf("0");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="force-publish",required=true,nillable=false,defaultValue="false")
  protected volatile boolean forcePublish = false;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlElement(name="stato",required=false,nillable=false)
  protected PossibiliStatiRichieste stato;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tracing-id",required=false,nillable=false)
  protected java.lang.String tracingId;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="error-details",required=false,nillable=false)
  protected java.lang.String errorDetails;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="history",required=true,nillable=false)
  protected volatile int history;

}
