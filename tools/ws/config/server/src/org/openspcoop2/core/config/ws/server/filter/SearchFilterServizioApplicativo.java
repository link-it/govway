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
package org.openspcoop2.core.config.ws.server.filter;

/**
 * <p>Java class for SearchFilterServizioApplicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-servizio-applicativo">
 *     &lt;sequence>
 *         &lt;element name="invocazione-porta" type="{http://www.openspcoop2.org/core/config/management}invocazione-porta" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="invocazione-servizio" type="{http://www.openspcoop2.org/core/config/management}invocazione-servizio" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="risposta-asincrona" type="{http://www.openspcoop2.org/core/config/management}risposta-asincrona" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipo-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="nome-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipologia-fruizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipologia-erogazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="orCondition" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.ws.server.filter.beans.InvocazionePorta;
import org.openspcoop2.core.config.ws.server.filter.beans.RispostaAsincrona;
import org.openspcoop2.core.config.ws.server.filter.beans.InvocazioneServizio;
import java.util.Date;

/**     
 * SearchFilterServizioApplicativo
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "search-filter-servizio-applicativo", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "invocazionePorta",
    "invocazioneServizio",
    "rispostaAsincrona",
    "tipoSoggettoProprietario",
    "nomeSoggettoProprietario",
    "tipologiaFruizione",
    "tipologiaErogazione",
    "nome",
    "descrizione",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "orCondition",
    "limit",
    "offset"
})
@javax.xml.bind.annotation.XmlRootElement(name = "search-filter-servizio-applicativo")
public class SearchFilterServizioApplicativo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="invocazione-porta",required=false,nillable=false)
	private InvocazionePorta invocazionePorta;
	
	public void setInvocazionePorta(InvocazionePorta invocazionePorta){
		this.invocazionePorta = invocazionePorta;
	}
	
	public InvocazionePorta getInvocazionePorta(){
		return this.invocazionePorta;
	}
	
	
	@XmlElement(name="invocazione-servizio",required=false,nillable=false)
	private InvocazioneServizio invocazioneServizio;
	
	public void setInvocazioneServizio(InvocazioneServizio invocazioneServizio){
		this.invocazioneServizio = invocazioneServizio;
	}
	
	public InvocazioneServizio getInvocazioneServizio(){
		return this.invocazioneServizio;
	}
	
	
	@XmlElement(name="risposta-asincrona",required=false,nillable=false)
	private RispostaAsincrona rispostaAsincrona;
	
	public void setRispostaAsincrona(RispostaAsincrona rispostaAsincrona){
		this.rispostaAsincrona = rispostaAsincrona;
	}
	
	public RispostaAsincrona getRispostaAsincrona(){
		return this.rispostaAsincrona;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-soggetto-proprietario",required=false,nillable=false)
	private String tipoSoggettoProprietario;
	
	public void setTipoSoggettoProprietario(String tipoSoggettoProprietario){
		this.tipoSoggettoProprietario = tipoSoggettoProprietario;
	}
	
	public String getTipoSoggettoProprietario(){
		return this.tipoSoggettoProprietario;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-soggetto-proprietario",required=false,nillable=false)
	private String nomeSoggettoProprietario;
	
	public void setNomeSoggettoProprietario(String nomeSoggettoProprietario){
		this.nomeSoggettoProprietario = nomeSoggettoProprietario;
	}
	
	public String getNomeSoggettoProprietario(){
		return this.nomeSoggettoProprietario;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipologia-fruizione",required=false,nillable=false)
	private String tipologiaFruizione;
	
	public void setTipologiaFruizione(String tipologiaFruizione){
		this.tipologiaFruizione = tipologiaFruizione;
	}
	
	public String getTipologiaFruizione(){
		return this.tipologiaFruizione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipologia-erogazione",required=false,nillable=false)
	private String tipologiaErogazione;
	
	public void setTipologiaErogazione(String tipologiaErogazione){
		this.tipologiaErogazione = tipologiaErogazione;
	}
	
	public String getTipologiaErogazione(){
		return this.tipologiaErogazione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=false,nillable=false)
	private String nome;
	
	public void setNome(String nome){
		this.nome = nome;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=false,nillable=false)
	private String descrizione;
	
	public void setDescrizione(String descrizione){
		this.descrizione = descrizione;
	}
	
	public String getDescrizione(){
		return this.descrizione;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione-min",required=false,nillable=false)
	private Date oraRegistrazioneMin;
	
	public void setOraRegistrazioneMin(Date oraRegistrazioneMin){
		this.oraRegistrazioneMin = oraRegistrazioneMin;
	}
	
	public Date getOraRegistrazioneMin(){
		return this.oraRegistrazioneMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione-max",required=false,nillable=false)
	private Date oraRegistrazioneMax;
	
	public void setOraRegistrazioneMax(Date oraRegistrazioneMax){
		this.oraRegistrazioneMax = oraRegistrazioneMax;
	}
	
	public Date getOraRegistrazioneMax(){
		return this.oraRegistrazioneMax;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="orCondition",required=false,nillable=false,defaultValue="false")
	private Boolean orCondition = new Boolean("false");
	
	public void setOrCondition(Boolean orCondition){
		this.orCondition = orCondition;
	}
	
	public Boolean getOrCondition(){
		return this.orCondition;
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
	
	
	
	
}