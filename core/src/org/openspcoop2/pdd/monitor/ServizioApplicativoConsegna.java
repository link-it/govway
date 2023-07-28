/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for servizio-applicativo-consegna complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servizio-applicativo-consegna"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="autorizzazione-integration-manager" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="errore-processamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="sbustamento-soap" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="sbustamento-informazioni-protocollo" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="true"/&gt;
 * 			&lt;element name="tipo-consegna" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="data-rispedizione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="coda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="priorita" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="attesa-esito" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
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
@XmlType(name = "servizio-applicativo-consegna", 
  propOrder = {
  	"autorizzazioneIntegrationManager",
  	"erroreProcessamento",
  	"nome",
  	"sbustamentoSoap",
  	"sbustamentoInformazioniProtocollo",
  	"tipoConsegna",
  	"dataRispedizione",
  	"nomePorta",
  	"coda",
  	"priorita",
  	"attesaEsito"
  }
)

@XmlRootElement(name = "servizio-applicativo-consegna")

public class ServizioApplicativoConsegna extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ServizioApplicativoConsegna() {
    super();
  }

  public boolean isAutorizzazioneIntegrationManager() {
    return this.autorizzazioneIntegrationManager;
  }

  public boolean getAutorizzazioneIntegrationManager() {
    return this.autorizzazioneIntegrationManager;
  }

  public void setAutorizzazioneIntegrationManager(boolean autorizzazioneIntegrationManager) {
    this.autorizzazioneIntegrationManager = autorizzazioneIntegrationManager;
  }

  public java.lang.String getErroreProcessamento() {
    return this.erroreProcessamento;
  }

  public void setErroreProcessamento(java.lang.String erroreProcessamento) {
    this.erroreProcessamento = erroreProcessamento;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public boolean isSbustamentoSoap() {
    return this.sbustamentoSoap;
  }

  public boolean getSbustamentoSoap() {
    return this.sbustamentoSoap;
  }

  public void setSbustamentoSoap(boolean sbustamentoSoap) {
    this.sbustamentoSoap = sbustamentoSoap;
  }

  public boolean isSbustamentoInformazioniProtocollo() {
    return this.sbustamentoInformazioniProtocollo;
  }

  public boolean getSbustamentoInformazioniProtocollo() {
    return this.sbustamentoInformazioniProtocollo;
  }

  public void setSbustamentoInformazioniProtocollo(boolean sbustamentoInformazioniProtocollo) {
    this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
  }

  public java.lang.String getTipoConsegna() {
    return this.tipoConsegna;
  }

  public void setTipoConsegna(java.lang.String tipoConsegna) {
    this.tipoConsegna = tipoConsegna;
  }

  public java.util.Date getDataRispedizione() {
    return this.dataRispedizione;
  }

  public void setDataRispedizione(java.util.Date dataRispedizione) {
    this.dataRispedizione = dataRispedizione;
  }

  public java.lang.String getNomePorta() {
    return this.nomePorta;
  }

  public void setNomePorta(java.lang.String nomePorta) {
    this.nomePorta = nomePorta;
  }

  public java.lang.String getCoda() {
    return this.coda;
  }

  public void setCoda(java.lang.String coda) {
    this.coda = coda;
  }

  public java.lang.String getPriorita() {
    return this.priorita;
  }

  public void setPriorita(java.lang.String priorita) {
    this.priorita = priorita;
  }

  public boolean isAttesaEsito() {
    return this.attesaEsito;
  }

  public boolean getAttesaEsito() {
    return this.attesaEsito;
  }

  public void setAttesaEsito(boolean attesaEsito) {
    this.attesaEsito = attesaEsito;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="autorizzazione-integration-manager",required=true,nillable=false,defaultValue="false")
  protected boolean autorizzazioneIntegrationManager = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="errore-processamento",required=false,nillable=false)
  protected java.lang.String erroreProcessamento;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="sbustamento-soap",required=true,nillable=false,defaultValue="false")
  protected boolean sbustamentoSoap = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="sbustamento-informazioni-protocollo",required=true,nillable=false,defaultValue="true")
  protected boolean sbustamentoInformazioniProtocollo = true;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-consegna",required=true,nillable=false)
  protected java.lang.String tipoConsegna;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-rispedizione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataRispedizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-porta",required=true,nillable=false)
  protected java.lang.String nomePorta;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="coda",required=true,nillable=false)
  protected java.lang.String coda;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="priorita",required=true,nillable=false)
  protected java.lang.String priorita;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="attesa-esito",required=true,nillable=false,defaultValue="false")
  protected boolean attesaEsito = false;

}
