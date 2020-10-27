/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.ws.server.filter;

/**
 * <p>Java class for SearchFilterPortaDelegata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-porta-delegata"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="soggetto-erogatore" type="{http://www.openspcoop2.org/core/config/management}porta-delegata-soggetto-erogatore" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/core/config/management}porta-delegata-servizio" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="azione" type="{http://www.openspcoop2.org/core/config/management}porta-delegata-azione" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="xacml-policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="ruoli" type="{http://www.openspcoop2.org/core/config/management}autorizzazione-ruoli" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="scope" type="{http://www.openspcoop2.org/core/config/management}autorizzazione-scope" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="gestione-token" type="{http://www.openspcoop2.org/core/config/management}gestione-token" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="local-forward" type="{http://www.openspcoop2.org/core/config/management}porta-delegata-local-forward" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="mtom-processor" type="{http://www.openspcoop2.org/core/config/management}mtom-processor" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="validazione-contenuti-applicativi" type="{http://www.openspcoop2.org/core/config/management}validazione-contenuti-applicativi" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="tipo-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="nome-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="stato-message-security" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="autenticazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="autenticazione-opzionale" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="autorizzazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="autorizzazione-contenuto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="ricevuta-asincrona-simmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="ricevuta-asincrona-asimmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="integrazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="allega-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="scarta-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="gestione-manifest" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="stateless" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="ricerca-porta-azione-delegata" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="canale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="orCondition" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="Boolean.valueOf("false")" /&gt;
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.ws.server.filter.beans.MtomProcessor;
import org.openspcoop2.core.config.ws.server.filter.beans.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.ws.server.filter.beans.PortaDelegataAzione;
import org.openspcoop2.core.config.ws.server.filter.beans.ValidazioneContenutiApplicativi;
import java.util.Date;
import org.openspcoop2.core.config.ws.server.filter.beans.PortaDelegataServizio;
import org.openspcoop2.core.config.ws.server.filter.beans.GestioneToken;
import org.openspcoop2.core.config.ws.server.filter.beans.AutorizzazioneRuoli;
import org.openspcoop2.core.config.ws.server.filter.beans.AutorizzazioneScope;
import org.openspcoop2.core.config.ws.server.filter.beans.PortaDelegataLocalForward;
import org.openspcoop2.core.config.constants.StatoFunzionalita;

/**     
 * SearchFilterPortaDelegata
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "search-filter-porta-delegata", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "soggettoErogatore",
    "servizio",
    "azione",
    "xacmlPolicy",
    "ruoli",
    "scope",
    "gestioneToken",
    "localForward",
    "mtomProcessor",
    "validazioneContenutiApplicativi",
    "tipoSoggettoProprietario",
    "nomeSoggettoProprietario",
    "statoMessageSecurity",
    "nome",
    "descrizione",
    "autenticazione",
    "autenticazioneOpzionale",
    "autorizzazione",
    "autorizzazioneContenuto",
    "ricevutaAsincronaSimmetrica",
    "ricevutaAsincronaAsimmetrica",
    "integrazione",
    "allegaBody",
    "scartaBody",
    "gestioneManifest",
    "stateless",
    "ricercaPortaAzioneDelegata",
    "stato",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "canale",
    "orCondition",
    "limit",
    "offset"
})
@javax.xml.bind.annotation.XmlRootElement(name = "search-filter-porta-delegata")
public class SearchFilterPortaDelegata extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="soggetto-erogatore",required=false,nillable=false)
	private PortaDelegataSoggettoErogatore soggettoErogatore;
	
	public void setSoggettoErogatore(PortaDelegataSoggettoErogatore soggettoErogatore){
		this.soggettoErogatore = soggettoErogatore;
	}
	
	public PortaDelegataSoggettoErogatore getSoggettoErogatore(){
		return this.soggettoErogatore;
	}
	
	
	@XmlElement(name="servizio",required=false,nillable=false)
	private PortaDelegataServizio servizio;
	
	public void setServizio(PortaDelegataServizio servizio){
		this.servizio = servizio;
	}
	
	public PortaDelegataServizio getServizio(){
		return this.servizio;
	}
	
	
	@XmlElement(name="azione",required=false,nillable=false)
	private PortaDelegataAzione azione;
	
	public void setAzione(PortaDelegataAzione azione){
		this.azione = azione;
	}
	
	public PortaDelegataAzione getAzione(){
		return this.azione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="xacml-policy",required=false,nillable=false)
	private String xacmlPolicy;
	
	public void setXacmlPolicy(String xacmlPolicy){
		this.xacmlPolicy = xacmlPolicy;
	}
	
	public String getXacmlPolicy(){
		return this.xacmlPolicy;
	}
	
	
	@XmlElement(name="ruoli",required=false,nillable=false)
	private AutorizzazioneRuoli ruoli;
	
	public void setRuoli(AutorizzazioneRuoli ruoli){
		this.ruoli = ruoli;
	}
	
	public AutorizzazioneRuoli getRuoli(){
		return this.ruoli;
	}
	
	
	@XmlElement(name="scope",required=false,nillable=false)
	private AutorizzazioneScope scope;
	
	public void setScope(AutorizzazioneScope scope){
		this.scope = scope;
	}
	
	public AutorizzazioneScope getScope(){
		return this.scope;
	}
	
	
	@XmlElement(name="gestione-token",required=false,nillable=false)
	private GestioneToken gestioneToken;
	
	public void setGestioneToken(GestioneToken gestioneToken){
		this.gestioneToken = gestioneToken;
	}
	
	public GestioneToken getGestioneToken(){
		return this.gestioneToken;
	}
	
	
	@XmlElement(name="local-forward",required=false,nillable=false)
	private PortaDelegataLocalForward localForward;
	
	public void setLocalForward(PortaDelegataLocalForward localForward){
		this.localForward = localForward;
	}
	
	public PortaDelegataLocalForward getLocalForward(){
		return this.localForward;
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
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="autenticazione",required=false,nillable=false)
	private String autenticazione;
	
	public void setAutenticazione(String autenticazione){
		this.autenticazione = autenticazione;
	}
	
	public String getAutenticazione(){
		return this.autenticazione;
	}
	
	
	@XmlElement(name="autenticazione-opzionale",required=false,nillable=false)
	private StatoFunzionalita autenticazioneOpzionale;
	
	public void setAutenticazioneOpzionale(StatoFunzionalita autenticazioneOpzionale){
		this.autenticazioneOpzionale = autenticazioneOpzionale;
	}
	
	public StatoFunzionalita getAutenticazioneOpzionale(){
		return this.autenticazioneOpzionale;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="autorizzazione",required=false,nillable=false)
	private String autorizzazione;
	
	public void setAutorizzazione(String autorizzazione){
		this.autorizzazione = autorizzazione;
	}
	
	public String getAutorizzazione(){
		return this.autorizzazione;
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
	
	
	@XmlElement(name="ricevuta-asincrona-simmetrica",required=false,nillable=false)
	private StatoFunzionalita ricevutaAsincronaSimmetrica;
	
	public void setRicevutaAsincronaSimmetrica(StatoFunzionalita ricevutaAsincronaSimmetrica){
		this.ricevutaAsincronaSimmetrica = ricevutaAsincronaSimmetrica;
	}
	
	public StatoFunzionalita getRicevutaAsincronaSimmetrica(){
		return this.ricevutaAsincronaSimmetrica;
	}
	
	
	@XmlElement(name="ricevuta-asincrona-asimmetrica",required=false,nillable=false)
	private StatoFunzionalita ricevutaAsincronaAsimmetrica;
	
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
	
	
	@XmlElement(name="allega-body",required=false,nillable=false)
	private StatoFunzionalita allegaBody;
	
	public void setAllegaBody(StatoFunzionalita allegaBody){
		this.allegaBody = allegaBody;
	}
	
	public StatoFunzionalita getAllegaBody(){
		return this.allegaBody;
	}
	
	
	@XmlElement(name="scarta-body",required=false,nillable=false)
	private StatoFunzionalita scartaBody;
	
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
	
	
	@XmlElement(name="ricerca-porta-azione-delegata",required=false,nillable=false)
	private StatoFunzionalita ricercaPortaAzioneDelegata;
	
	public void setRicercaPortaAzioneDelegata(StatoFunzionalita ricercaPortaAzioneDelegata){
		this.ricercaPortaAzioneDelegata = ricercaPortaAzioneDelegata;
	}
	
	public StatoFunzionalita getRicercaPortaAzioneDelegata(){
		return this.ricercaPortaAzioneDelegata;
	}
	
	
	@XmlElement(name="stato",required=false,nillable=false)
	private StatoFunzionalita stato;
	
	public void setStato(StatoFunzionalita stato){
		this.stato = stato;
	}
	
	public StatoFunzionalita getStato(){
		return this.stato;
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
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="canale",required=false,nillable=false)
	private String canale;
	
	public void setCanale(String canale){
		this.canale = canale;
	}
	
	public String getCanale(){
		return this.canale;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="orCondition",required=false,nillable=false,defaultValue="false")
	private Boolean orCondition = Boolean.valueOf("false");
	
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