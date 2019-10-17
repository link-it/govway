/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
 * &lt;complexType name="search-filter-transazione-applicativo-server">
 *     &lt;sequence>
 *         &lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="data-uscita-richiesta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="data-uscita-richiesta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="data-accettazione-risposta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="data-accettazione-risposta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="data-ingresso-risposta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="data-ingresso-risposta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="richiesta-uscita-bytes-min" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="richiesta-uscita-bytes-max" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="risposta-ingresso-bytes-min" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="risposta-ingresso-bytes-max" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="codice-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="data-primo-tentativo-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="data-primo-tentativo-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="data-ultimo-errore-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="data-ultimo-errore-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="codice-risposta-ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="descOrder" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="Boolean.valueOf("false")" />
 *     &lt;/sequence>
 * &lt;/complexType>
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
    "codiceRisposta",
    "dataPrimoTentativoMin",
    "dataPrimoTentativoMax",
    "dataUltimoErroreMin",
    "dataUltimoErroreMax",
    "codiceRispostaUltimoErrore",
    "ultimoErrore",
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
  @XmlElement(name="codice-risposta",required=false,nillable=false)
	private String codiceRisposta;
	
	public void setCodiceRisposta(String codiceRisposta){
		this.codiceRisposta = codiceRisposta;
	}
	
	public String getCodiceRisposta(){
		return this.codiceRisposta;
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