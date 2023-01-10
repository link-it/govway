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
package org.openspcoop2.core.transazioni.ws.server.filter;

/**
 * <p>Java class for SearchFilterTransazioneApplicativoServer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-transazione-applicativo-server"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="connettore-nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="protocollo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="consegna-terminata" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="Boolean.valueOf("false")" /&gt;
 *         &lt;element name="data-messaggio-scaduto-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-messaggio-scaduto-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="dettaglio-esito" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="consegna-trasparente" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="Boolean.valueOf("false")" /&gt;
 *         &lt;element name="consegna-integration-manager" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="Boolean.valueOf("false")" /&gt;
 *         &lt;element name="identificativo-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-accettazione-richiesta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-accettazione-richiesta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-uscita-richiesta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-uscita-richiesta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-accettazione-risposta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-accettazione-risposta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-ingresso-risposta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-ingresso-risposta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="richiesta-uscita-bytes-min" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="richiesta-uscita-bytes-max" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="risposta-ingresso-bytes-min" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="risposta-ingresso-bytes-max" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="location-connettore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="codice-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="formato-fault" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-primo-tentativo-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-primo-tentativo-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="cluster-id-presa-in-carico" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="cluster-id-consegna" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-ultimo-errore-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-ultimo-errore-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="dettaglio-esito-ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="codice-risposta-ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="location-ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="cluster-id-ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="formato-fault-ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-primo-prelievo-im-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-primo-prelievo-im-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-prelievo-im-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-prelievo-im-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-eliminazione-im-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="data-eliminazione-im-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="cluster-id-prelievo-im" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="cluster-id-eliminazione-im" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="descOrder" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="Boolean.valueOf("false")" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

/**     
 * SearchFilterTransazioneApplicativoServer
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "search-filter-transazione-applicativo-server", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "idTransazione",
    "servizioApplicativoErogatore",
    "connettoreNome",
    "dataRegistrazioneMin",
    "dataRegistrazioneMax",
    "protocollo",
    "consegnaTerminata",
    "dataMessaggioScadutoMin",
    "dataMessaggioScadutoMax",
    "dettaglioEsito",
    "consegnaTrasparente",
    "consegnaIntegrationManager",
    "identificativoMessaggio",
    "dataAccettazioneRichiestaMin",
    "dataAccettazioneRichiestaMax",
    "dataUscitaRichiestaMin",
    "dataUscitaRichiestaMax",
    "dataAccettazioneRispostaMin",
    "dataAccettazioneRispostaMax",
    "dataIngressoRispostaMin",
    "dataIngressoRispostaMax",
    "richiestaUscitaBytesMin",
    "richiestaUscitaBytesMax",
    "rispostaIngressoBytesMin",
    "rispostaIngressoBytesMax",
    "locationConnettore",
    "codiceRisposta",
    "formatoFault",
    "dataPrimoTentativoMin",
    "dataPrimoTentativoMax",
    "clusterIdPresaInCarico",
    "clusterIdConsegna",
    "dataUltimoErroreMin",
    "dataUltimoErroreMax",
    "dettaglioEsitoUltimoErrore",
    "codiceRispostaUltimoErrore",
    "ultimoErrore",
    "locationUltimoErrore",
    "clusterIdUltimoErrore",
    "formatoFaultUltimoErrore",
    "dataPrimoPrelievoImMin",
    "dataPrimoPrelievoImMax",
    "dataPrelievoImMin",
    "dataPrelievoImMax",
    "dataEliminazioneImMin",
    "dataEliminazioneImMax",
    "clusterIdPrelievoIm",
    "clusterIdEliminazioneIm",
    "limit",
    "offset",
    "descOrder"
})
@javax.xml.bind.annotation.XmlRootElement(name = "search-filter-transazione-applicativo-server")
public class SearchFilterTransazioneApplicativoServer extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-transazione",required=false,nillable=false)
	private String idTransazione;
	
	public void setIdTransazione(String idTransazione){
		this.idTransazione = idTransazione;
	}
	
	public String getIdTransazione(){
		return this.idTransazione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-erogatore",required=false,nillable=false)
	private String servizioApplicativoErogatore;
	
	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore){
		this.servizioApplicativoErogatore = servizioApplicativoErogatore;
	}
	
	public String getServizioApplicativoErogatore(){
		return this.servizioApplicativoErogatore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="connettore-nome",required=false,nillable=false)
	private String connettoreNome;
	
	public void setConnettoreNome(String connettoreNome){
		this.connettoreNome = connettoreNome;
	}
	
	public String getConnettoreNome(){
		return this.connettoreNome;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-registrazione-min",required=false,nillable=false)
	private Date dataRegistrazioneMin;
	
	public void setDataRegistrazioneMin(Date dataRegistrazioneMin){
		this.dataRegistrazioneMin = dataRegistrazioneMin;
	}
	
	public Date getDataRegistrazioneMin(){
		return this.dataRegistrazioneMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-registrazione-max",required=false,nillable=false)
	private Date dataRegistrazioneMax;
	
	public void setDataRegistrazioneMax(Date dataRegistrazioneMax){
		this.dataRegistrazioneMax = dataRegistrazioneMax;
	}
	
	public Date getDataRegistrazioneMax(){
		return this.dataRegistrazioneMax;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="protocollo",required=false,nillable=false)
	private String protocollo;
	
	public void setProtocollo(String protocollo){
		this.protocollo = protocollo;
	}
	
	public String getProtocollo(){
		return this.protocollo;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="consegna-terminata",required=false,nillable=false,defaultValue="false")
	private Boolean consegnaTerminata = Boolean.valueOf("false");
	
	public void setConsegnaTerminata(Boolean consegnaTerminata){
		this.consegnaTerminata = consegnaTerminata;
	}
	
	public Boolean getConsegnaTerminata(){
		return this.consegnaTerminata;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-messaggio-scaduto-min",required=false,nillable=false)
	private Date dataMessaggioScadutoMin;
	
	public void setDataMessaggioScadutoMin(Date dataMessaggioScadutoMin){
		this.dataMessaggioScadutoMin = dataMessaggioScadutoMin;
	}
	
	public Date getDataMessaggioScadutoMin(){
		return this.dataMessaggioScadutoMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-messaggio-scaduto-max",required=false,nillable=false)
	private Date dataMessaggioScadutoMax;
	
	public void setDataMessaggioScadutoMax(Date dataMessaggioScadutoMax){
		this.dataMessaggioScadutoMax = dataMessaggioScadutoMax;
	}
	
	public Date getDataMessaggioScadutoMax(){
		return this.dataMessaggioScadutoMax;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="dettaglio-esito",required=false,nillable=false)
	private Integer dettaglioEsito;
	
	public void setDettaglioEsito(Integer dettaglioEsito){
		this.dettaglioEsito = dettaglioEsito;
	}
	
	public Integer getDettaglioEsito(){
		return this.dettaglioEsito;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="consegna-trasparente",required=false,nillable=false,defaultValue="false")
	private Boolean consegnaTrasparente = Boolean.valueOf("false");
	
	public void setConsegnaTrasparente(Boolean consegnaTrasparente){
		this.consegnaTrasparente = consegnaTrasparente;
	}
	
	public Boolean getConsegnaTrasparente(){
		return this.consegnaTrasparente;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="consegna-integration-manager",required=false,nillable=false,defaultValue="false")
	private Boolean consegnaIntegrationManager = Boolean.valueOf("false");
	
	public void setConsegnaIntegrationManager(Boolean consegnaIntegrationManager){
		this.consegnaIntegrationManager = consegnaIntegrationManager;
	}
	
	public Boolean getConsegnaIntegrationManager(){
		return this.consegnaIntegrationManager;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-messaggio",required=false,nillable=false)
	private String identificativoMessaggio;
	
	public void setIdentificativoMessaggio(String identificativoMessaggio){
		this.identificativoMessaggio = identificativoMessaggio;
	}
	
	public String getIdentificativoMessaggio(){
		return this.identificativoMessaggio;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-accettazione-richiesta-min",required=false,nillable=false)
	private Date dataAccettazioneRichiestaMin;
	
	public void setDataAccettazioneRichiestaMin(Date dataAccettazioneRichiestaMin){
		this.dataAccettazioneRichiestaMin = dataAccettazioneRichiestaMin;
	}
	
	public Date getDataAccettazioneRichiestaMin(){
		return this.dataAccettazioneRichiestaMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-accettazione-richiesta-max",required=false,nillable=false)
	private Date dataAccettazioneRichiestaMax;
	
	public void setDataAccettazioneRichiestaMax(Date dataAccettazioneRichiestaMax){
		this.dataAccettazioneRichiestaMax = dataAccettazioneRichiestaMax;
	}
	
	public Date getDataAccettazioneRichiestaMax(){
		return this.dataAccettazioneRichiestaMax;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-uscita-richiesta-min",required=false,nillable=false)
	private Date dataUscitaRichiestaMin;
	
	public void setDataUscitaRichiestaMin(Date dataUscitaRichiestaMin){
		this.dataUscitaRichiestaMin = dataUscitaRichiestaMin;
	}
	
	public Date getDataUscitaRichiestaMin(){
		return this.dataUscitaRichiestaMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-uscita-richiesta-max",required=false,nillable=false)
	private Date dataUscitaRichiestaMax;
	
	public void setDataUscitaRichiestaMax(Date dataUscitaRichiestaMax){
		this.dataUscitaRichiestaMax = dataUscitaRichiestaMax;
	}
	
	public Date getDataUscitaRichiestaMax(){
		return this.dataUscitaRichiestaMax;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-accettazione-risposta-min",required=false,nillable=false)
	private Date dataAccettazioneRispostaMin;
	
	public void setDataAccettazioneRispostaMin(Date dataAccettazioneRispostaMin){
		this.dataAccettazioneRispostaMin = dataAccettazioneRispostaMin;
	}
	
	public Date getDataAccettazioneRispostaMin(){
		return this.dataAccettazioneRispostaMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-accettazione-risposta-max",required=false,nillable=false)
	private Date dataAccettazioneRispostaMax;
	
	public void setDataAccettazioneRispostaMax(Date dataAccettazioneRispostaMax){
		this.dataAccettazioneRispostaMax = dataAccettazioneRispostaMax;
	}
	
	public Date getDataAccettazioneRispostaMax(){
		return this.dataAccettazioneRispostaMax;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-ingresso-risposta-min",required=false,nillable=false)
	private Date dataIngressoRispostaMin;
	
	public void setDataIngressoRispostaMin(Date dataIngressoRispostaMin){
		this.dataIngressoRispostaMin = dataIngressoRispostaMin;
	}
	
	public Date getDataIngressoRispostaMin(){
		return this.dataIngressoRispostaMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-ingresso-risposta-max",required=false,nillable=false)
	private Date dataIngressoRispostaMax;
	
	public void setDataIngressoRispostaMax(Date dataIngressoRispostaMax){
		this.dataIngressoRispostaMax = dataIngressoRispostaMax;
	}
	
	public Date getDataIngressoRispostaMax(){
		return this.dataIngressoRispostaMax;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="richiesta-uscita-bytes-min",required=false,nillable=false)
	private Long richiestaUscitaBytesMin;
	
	public void setRichiestaUscitaBytesMin(Long richiestaUscitaBytesMin){
		this.richiestaUscitaBytesMin = richiestaUscitaBytesMin;
	}
	
	public Long getRichiestaUscitaBytesMin(){
		return this.richiestaUscitaBytesMin;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="richiesta-uscita-bytes-max",required=false,nillable=false)
	private Long richiestaUscitaBytesMax;
	
	public void setRichiestaUscitaBytesMax(Long richiestaUscitaBytesMax){
		this.richiestaUscitaBytesMax = richiestaUscitaBytesMax;
	}
	
	public Long getRichiestaUscitaBytesMax(){
		return this.richiestaUscitaBytesMax;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="risposta-ingresso-bytes-min",required=false,nillable=false)
	private Long rispostaIngressoBytesMin;
	
	public void setRispostaIngressoBytesMin(Long rispostaIngressoBytesMin){
		this.rispostaIngressoBytesMin = rispostaIngressoBytesMin;
	}
	
	public Long getRispostaIngressoBytesMin(){
		return this.rispostaIngressoBytesMin;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="risposta-ingresso-bytes-max",required=false,nillable=false)
	private Long rispostaIngressoBytesMax;
	
	public void setRispostaIngressoBytesMax(Long rispostaIngressoBytesMax){
		this.rispostaIngressoBytesMax = rispostaIngressoBytesMax;
	}
	
	public Long getRispostaIngressoBytesMax(){
		return this.rispostaIngressoBytesMax;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="location-connettore",required=false,nillable=false)
	private String locationConnettore;
	
	public void setLocationConnettore(String locationConnettore){
		this.locationConnettore = locationConnettore;
	}
	
	public String getLocationConnettore(){
		return this.locationConnettore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice-risposta",required=false,nillable=false)
	private String codiceRisposta;
	
	public void setCodiceRisposta(String codiceRisposta){
		this.codiceRisposta = codiceRisposta;
	}
	
	public String getCodiceRisposta(){
		return this.codiceRisposta;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="formato-fault",required=false,nillable=false)
	private String formatoFault;
	
	public void setFormatoFault(String formatoFault){
		this.formatoFault = formatoFault;
	}
	
	public String getFormatoFault(){
		return this.formatoFault;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-primo-tentativo-min",required=false,nillable=false)
	private Date dataPrimoTentativoMin;
	
	public void setDataPrimoTentativoMin(Date dataPrimoTentativoMin){
		this.dataPrimoTentativoMin = dataPrimoTentativoMin;
	}
	
	public Date getDataPrimoTentativoMin(){
		return this.dataPrimoTentativoMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-primo-tentativo-max",required=false,nillable=false)
	private Date dataPrimoTentativoMax;
	
	public void setDataPrimoTentativoMax(Date dataPrimoTentativoMax){
		this.dataPrimoTentativoMax = dataPrimoTentativoMax;
	}
	
	public Date getDataPrimoTentativoMax(){
		return this.dataPrimoTentativoMax;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="cluster-id-presa-in-carico",required=false,nillable=false)
	private String clusterIdPresaInCarico;
	
	public void setClusterIdPresaInCarico(String clusterIdPresaInCarico){
		this.clusterIdPresaInCarico = clusterIdPresaInCarico;
	}
	
	public String getClusterIdPresaInCarico(){
		return this.clusterIdPresaInCarico;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="cluster-id-consegna",required=false,nillable=false)
	private String clusterIdConsegna;
	
	public void setClusterIdConsegna(String clusterIdConsegna){
		this.clusterIdConsegna = clusterIdConsegna;
	}
	
	public String getClusterIdConsegna(){
		return this.clusterIdConsegna;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-ultimo-errore-min",required=false,nillable=false)
	private Date dataUltimoErroreMin;
	
	public void setDataUltimoErroreMin(Date dataUltimoErroreMin){
		this.dataUltimoErroreMin = dataUltimoErroreMin;
	}
	
	public Date getDataUltimoErroreMin(){
		return this.dataUltimoErroreMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-ultimo-errore-max",required=false,nillable=false)
	private Date dataUltimoErroreMax;
	
	public void setDataUltimoErroreMax(Date dataUltimoErroreMax){
		this.dataUltimoErroreMax = dataUltimoErroreMax;
	}
	
	public Date getDataUltimoErroreMax(){
		return this.dataUltimoErroreMax;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="dettaglio-esito-ultimo-errore",required=false,nillable=false)
	private Integer dettaglioEsitoUltimoErrore;
	
	public void setDettaglioEsitoUltimoErrore(Integer dettaglioEsitoUltimoErrore){
		this.dettaglioEsitoUltimoErrore = dettaglioEsitoUltimoErrore;
	}
	
	public Integer getDettaglioEsitoUltimoErrore(){
		return this.dettaglioEsitoUltimoErrore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice-risposta-ultimo-errore",required=false,nillable=false)
	private String codiceRispostaUltimoErrore;
	
	public void setCodiceRispostaUltimoErrore(String codiceRispostaUltimoErrore){
		this.codiceRispostaUltimoErrore = codiceRispostaUltimoErrore;
	}
	
	public String getCodiceRispostaUltimoErrore(){
		return this.codiceRispostaUltimoErrore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="ultimo-errore",required=false,nillable=false)
	private String ultimoErrore;
	
	public void setUltimoErrore(String ultimoErrore){
		this.ultimoErrore = ultimoErrore;
	}
	
	public String getUltimoErrore(){
		return this.ultimoErrore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="location-ultimo-errore",required=false,nillable=false)
	private String locationUltimoErrore;
	
	public void setLocationUltimoErrore(String locationUltimoErrore){
		this.locationUltimoErrore = locationUltimoErrore;
	}
	
	public String getLocationUltimoErrore(){
		return this.locationUltimoErrore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="cluster-id-ultimo-errore",required=false,nillable=false)
	private String clusterIdUltimoErrore;
	
	public void setClusterIdUltimoErrore(String clusterIdUltimoErrore){
		this.clusterIdUltimoErrore = clusterIdUltimoErrore;
	}
	
	public String getClusterIdUltimoErrore(){
		return this.clusterIdUltimoErrore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="formato-fault-ultimo-errore",required=false,nillable=false)
	private String formatoFaultUltimoErrore;
	
	public void setFormatoFaultUltimoErrore(String formatoFaultUltimoErrore){
		this.formatoFaultUltimoErrore = formatoFaultUltimoErrore;
	}
	
	public String getFormatoFaultUltimoErrore(){
		return this.formatoFaultUltimoErrore;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-primo-prelievo-im-min",required=false,nillable=false)
	private Date dataPrimoPrelievoImMin;
	
	public void setDataPrimoPrelievoImMin(Date dataPrimoPrelievoImMin){
		this.dataPrimoPrelievoImMin = dataPrimoPrelievoImMin;
	}
	
	public Date getDataPrimoPrelievoImMin(){
		return this.dataPrimoPrelievoImMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-primo-prelievo-im-max",required=false,nillable=false)
	private Date dataPrimoPrelievoImMax;
	
	public void setDataPrimoPrelievoImMax(Date dataPrimoPrelievoImMax){
		this.dataPrimoPrelievoImMax = dataPrimoPrelievoImMax;
	}
	
	public Date getDataPrimoPrelievoImMax(){
		return this.dataPrimoPrelievoImMax;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-prelievo-im-min",required=false,nillable=false)
	private Date dataPrelievoImMin;
	
	public void setDataPrelievoImMin(Date dataPrelievoImMin){
		this.dataPrelievoImMin = dataPrelievoImMin;
	}
	
	public Date getDataPrelievoImMin(){
		return this.dataPrelievoImMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-prelievo-im-max",required=false,nillable=false)
	private Date dataPrelievoImMax;
	
	public void setDataPrelievoImMax(Date dataPrelievoImMax){
		this.dataPrelievoImMax = dataPrelievoImMax;
	}
	
	public Date getDataPrelievoImMax(){
		return this.dataPrelievoImMax;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-eliminazione-im-min",required=false,nillable=false)
	private Date dataEliminazioneImMin;
	
	public void setDataEliminazioneImMin(Date dataEliminazioneImMin){
		this.dataEliminazioneImMin = dataEliminazioneImMin;
	}
	
	public Date getDataEliminazioneImMin(){
		return this.dataEliminazioneImMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-eliminazione-im-max",required=false,nillable=false)
	private Date dataEliminazioneImMax;
	
	public void setDataEliminazioneImMax(Date dataEliminazioneImMax){
		this.dataEliminazioneImMax = dataEliminazioneImMax;
	}
	
	public Date getDataEliminazioneImMax(){
		return this.dataEliminazioneImMax;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="cluster-id-prelievo-im",required=false,nillable=false)
	private String clusterIdPrelievoIm;
	
	public void setClusterIdPrelievoIm(String clusterIdPrelievoIm){
		this.clusterIdPrelievoIm = clusterIdPrelievoIm;
	}
	
	public String getClusterIdPrelievoIm(){
		return this.clusterIdPrelievoIm;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="cluster-id-eliminazione-im",required=false,nillable=false)
	private String clusterIdEliminazioneIm;
	
	public void setClusterIdEliminazioneIm(String clusterIdEliminazioneIm){
		this.clusterIdEliminazioneIm = clusterIdEliminazioneIm;
	}
	
	public String getClusterIdEliminazioneIm(){
		return this.clusterIdEliminazioneIm;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="limit",required=false,nillable=false)
	private Integer limit;
	
	public void setLimit(Integer limit){
		this.limit = limit;
	}
	
	public Integer getLimit(){
		return this.limit;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="offset",required=false,nillable=false)
	private Integer offset;
	
	public void setOffset(Integer offset){
		this.offset = offset;
	}
	
	public Integer getOffset(){
		return this.offset;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="descOrder",required=false,nillable=false,defaultValue="false")
	private Boolean descOrder = Boolean.valueOf("false");
	
	public void setDescOrder(Boolean descOrder){
		this.descOrder = descOrder;
	}
	
	public Boolean getDescOrder(){
		return this.descOrder;
	}
	
	
	
	
}