/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
 * <p>Java class for SearchFilterPortaApplicativa complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-porta-applicativa">
 *     &lt;sequence>
 *         &lt;element name="soggetto-virtuale" type="{http://www.openspcoop2.org/core/config/management}porta-applicativa-soggetto-virtuale" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/core/config/management}porta-applicativa-servizio" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="azione" type="{http://www.openspcoop2.org/core/config/management}porta-applicativa-azione" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="mtom-processor" type="{http://www.openspcoop2.org/core/config/management}mtom-processor" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="validazione-contenuti-applicativi" type="{http://www.openspcoop2.org/core/config/management}validazione-contenuti-applicativi" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipo-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="nome-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="stato-message-security" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ricevuta-asincrona-simmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" default="(StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato")" />
 *         &lt;element name="ricevuta-asincrona-asimmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" default="(StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato")" />
 *         &lt;element name="integrazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="allega-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" default="(StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato")" />
 *         &lt;element name="scarta-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" default="(StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato")" />
 *         &lt;element name="gestione-manifest" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="stateless" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="behaviour" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="autorizzazione-contenuto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
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
import org.openspcoop2.core.config.ws.server.filter.beans.MtomProcessor;
import org.openspcoop2.core.config.ws.server.filter.beans.PortaApplicativaAzione;
import org.openspcoop2.core.config.ws.server.filter.beans.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.util.Date;
import org.openspcoop2.core.config.ws.server.filter.beans.PortaApplicativaServizio;
import org.openspcoop2.core.config.ws.server.filter.beans.ValidazioneContenutiApplicativi;

/**     
 * SearchFilterPortaApplicativa
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "search-filter-porta-applicativa", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "soggettoVirtuale",
    "servizio",
    "azione",
    "mtomProcessor",
    "validazioneContenutiApplicativi",
    "tipoSoggettoProprietario",
    "nomeSoggettoProprietario",
    "statoMessageSecurity",
    "nome",
    "descrizione",
    "ricevutaAsincronaSimmetrica",
    "ricevutaAsincronaAsimmetrica",
    "integrazione",
    "allegaBody",
    "scartaBody",
    "gestioneManifest",
    "stateless",
    "behaviour",
    "autorizzazioneContenuto",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "orCondition",
    "limit",
    "offset"
})
@javax.xml.bind.annotation.XmlRootElement(name = "search-filter-porta-applicativa")
public class SearchFilterPortaApplicativa extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="soggetto-virtuale",required=false,nillable=false)
	private PortaApplicativaSoggettoVirtuale soggettoVirtuale;
	
	public void setSoggettoVirtuale(PortaApplicativaSoggettoVirtuale soggettoVirtuale){
		this.soggettoVirtuale = soggettoVirtuale;
	}
	
	public PortaApplicativaSoggettoVirtuale getSoggettoVirtuale(){
		return this.soggettoVirtuale;
	}
	
	
	@XmlElement(name="servizio",required=false,nillable=false)
	private PortaApplicativaServizio servizio;
	
	public void setServizio(PortaApplicativaServizio servizio){
		this.servizio = servizio;
	}
	
	public PortaApplicativaServizio getServizio(){
		return this.servizio;
	}
	
	
	@XmlElement(name="azione",required=false,nillable=false)
	private PortaApplicativaAzione azione;
	
	public void setAzione(PortaApplicativaAzione azione){
		this.azione = azione;
	}
	
	public PortaApplicativaAzione getAzione(){
		return this.azione;
	}
	
	
	@XmlElement(name="mtom-processor",required=false,nillable=false)
	private MtomProcessor mtomProcessor;
	
	public void setMtomProcessor(MtomProcessor mtomProcessor){
		this.mtomProcessor = mtomProcessor;
	}
	
	public MtomProcessor getMtomProcessor(){
		return this.mtomProcessor;
	}
	
	
	@XmlElement(name="validazione-contenuti-applicativi",required=false,nillable=false)
	private ValidazioneContenutiApplicativi validazioneContenutiApplicativi;
	
	public void setValidazioneContenutiApplicativi(ValidazioneContenutiApplicativi validazioneContenutiApplicativi){
		this.validazioneContenutiApplicativi = validazioneContenutiApplicativi;
	}
	
	public ValidazioneContenutiApplicativi getValidazioneContenutiApplicativi(){
		return this.validazioneContenutiApplicativi;
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
  @XmlElement(name="stato-message-security",required=false,nillable=false)
	private String statoMessageSecurity;
	
	public void setStatoMessageSecurity(String statoMessageSecurity){
		this.statoMessageSecurity = statoMessageSecurity;
	}
	
	public String getStatoMessageSecurity(){
		return this.statoMessageSecurity;
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
	
	
	@XmlElement(name="ricevuta-asincrona-simmetrica",required=false,nillable=false,defaultValue="abilitato")
	private StatoFunzionalita ricevutaAsincronaSimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");
	
	public void setRicevutaAsincronaSimmetrica(StatoFunzionalita ricevutaAsincronaSimmetrica){
		this.ricevutaAsincronaSimmetrica = ricevutaAsincronaSimmetrica;
	}
	
	public StatoFunzionalita getRicevutaAsincronaSimmetrica(){
		return this.ricevutaAsincronaSimmetrica;
	}
	
	
	@XmlElement(name="ricevuta-asincrona-asimmetrica",required=false,nillable=false,defaultValue="abilitato")
	private StatoFunzionalita ricevutaAsincronaAsimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");
	
	public void setRicevutaAsincronaAsimmetrica(StatoFunzionalita ricevutaAsincronaAsimmetrica){
		this.ricevutaAsincronaAsimmetrica = ricevutaAsincronaAsimmetrica;
	}
	
	public StatoFunzionalita getRicevutaAsincronaAsimmetrica(){
		return this.ricevutaAsincronaAsimmetrica;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="integrazione",required=false,nillable=false)
	private String integrazione;
	
	public void setIntegrazione(String integrazione){
		this.integrazione = integrazione;
	}
	
	public String getIntegrazione(){
		return this.integrazione;
	}
	
	
	@XmlElement(name="allega-body",required=false,nillable=false,defaultValue="disabilitato")
	private StatoFunzionalita allegaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");
	
	public void setAllegaBody(StatoFunzionalita allegaBody){
		this.allegaBody = allegaBody;
	}
	
	public StatoFunzionalita getAllegaBody(){
		return this.allegaBody;
	}
	
	
	@XmlElement(name="scarta-body",required=false,nillable=false,defaultValue="disabilitato")
	private StatoFunzionalita scartaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");
	
	public void setScartaBody(StatoFunzionalita scartaBody){
		this.scartaBody = scartaBody;
	}
	
	public StatoFunzionalita getScartaBody(){
		return this.scartaBody;
	}
	
	
	@XmlElement(name="gestione-manifest",required=false,nillable=false)
	private StatoFunzionalita gestioneManifest;
	
	public void setGestioneManifest(StatoFunzionalita gestioneManifest){
		this.gestioneManifest = gestioneManifest;
	}
	
	public StatoFunzionalita getGestioneManifest(){
		return this.gestioneManifest;
	}
	
	
	@XmlElement(name="stateless",required=false,nillable=false)
	private StatoFunzionalita stateless;
	
	public void setStateless(StatoFunzionalita stateless){
		this.stateless = stateless;
	}
	
	public StatoFunzionalita getStateless(){
		return this.stateless;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="behaviour",required=false,nillable=false)
	private String behaviour;
	
	public void setBehaviour(String behaviour){
		this.behaviour = behaviour;
	}
	
	public String getBehaviour(){
		return this.behaviour;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="autorizzazione-contenuto",required=false,nillable=false)
	private String autorizzazioneContenuto;
	
	public void setAutorizzazioneContenuto(String autorizzazioneContenuto){
		this.autorizzazioneContenuto = autorizzazioneContenuto;
	}
	
	public String getAutorizzazioneContenuto(){
		return this.autorizzazioneContenuto;
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