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

package org.openspcoop2.core.config.ws.client.portaapplicativa.search;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openspcoop2.core.config.constants.StatoFunzionalita;


/**
 * <p>Java class for search-filter-porta-applicativa complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-porta-applicativa"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="soggetto-virtuale" type="{http://www.openspcoop2.org/core/config/management}porta-applicativa-soggetto-virtuale" minOccurs="0"/&gt;
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/core/config/management}porta-applicativa-servizio" minOccurs="0"/&gt;
 *         &lt;element name="azione" type="{http://www.openspcoop2.org/core/config/management}porta-applicativa-azione" minOccurs="0"/&gt;
 *         &lt;element name="ruoli" type="{http://www.openspcoop2.org/core/config/management}autorizzazione-ruoli" minOccurs="0"/&gt;
 *         &lt;element name="mtom-processor" type="{http://www.openspcoop2.org/core/config/management}mtom-processor" minOccurs="0"/&gt;
 *         &lt;element name="validazione-contenuti-applicativi" type="{http://www.openspcoop2.org/core/config/management}validazione-contenuti-applicativi" minOccurs="0"/&gt;
 *         &lt;element name="tipo-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="stato-message-security" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ricevuta-asincrona-simmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="ricevuta-asincrona-asimmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="integrazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="allega-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="scarta-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="gestione-manifest" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="stateless" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="behaviour" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="autenticazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="autenticazione-opzionale" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="autorizzazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="autorizzazione-contenuto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="orCondition" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-filter-porta-applicativa", propOrder = {
    "soggettoVirtuale",
    "servizio",
    "azione",
    "ruoli",
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
    "autenticazione",
    "autenticazioneOpzionale",
    "autorizzazione",
    "autorizzazioneContenuto",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "orCondition",
    "limit",
    "offset"
})
public class SearchFilterPortaApplicativa {

    @XmlElement(name = "soggetto-virtuale")
    protected PortaApplicativaSoggettoVirtuale soggettoVirtuale;
    protected PortaApplicativaServizio servizio;
    protected PortaApplicativaAzione azione;
    protected AutorizzazioneRuoli ruoli;
    @XmlElement(name = "mtom-processor")
    protected MtomProcessor mtomProcessor;
    @XmlElement(name = "validazione-contenuti-applicativi")
    protected ValidazioneContenutiApplicativi validazioneContenutiApplicativi;
    @XmlElement(name = "tipo-soggetto-proprietario")
    protected String tipoSoggettoProprietario;
    @XmlElement(name = "nome-soggetto-proprietario")
    protected String nomeSoggettoProprietario;
    @XmlElement(name = "stato-message-security")
    protected String statoMessageSecurity;
    protected String nome;
    protected String descrizione;
    @XmlElement(name = "ricevuta-asincrona-simmetrica")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita ricevutaAsincronaSimmetrica;
    @XmlElement(name = "ricevuta-asincrona-asimmetrica")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita ricevutaAsincronaAsimmetrica;
    protected String integrazione;
    @XmlElement(name = "allega-body")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita allegaBody;
    @XmlElement(name = "scarta-body")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita scartaBody;
    @XmlElement(name = "gestione-manifest")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita gestioneManifest;
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita stateless;
    protected String behaviour;
    protected String autenticazione;
    @XmlElement(name = "autenticazione-opzionale")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita autenticazioneOpzionale;
    protected String autorizzazione;
    @XmlElement(name = "autorizzazione-contenuto")
    protected String autorizzazioneContenuto;
    @XmlElement(name = "ora-registrazione-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMin;
    @XmlElement(name = "ora-registrazione-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMax;
    protected Boolean orCondition;
    protected BigInteger limit;
    protected BigInteger offset;

    /**
     * Gets the value of the soggettoVirtuale property.
     * 
     * @return
     *     possible object is
     *     {@link PortaApplicativaSoggettoVirtuale }
     *     
     */
    public PortaApplicativaSoggettoVirtuale getSoggettoVirtuale() {
        return this.soggettoVirtuale;
    }

    /**
     * Sets the value of the soggettoVirtuale property.
     * 
     * @param value
     *     allowed object is
     *     {@link PortaApplicativaSoggettoVirtuale }
     *     
     */
    public void setSoggettoVirtuale(PortaApplicativaSoggettoVirtuale value) {
        this.soggettoVirtuale = value;
    }

    /**
     * Gets the value of the servizio property.
     * 
     * @return
     *     possible object is
     *     {@link PortaApplicativaServizio }
     *     
     */
    public PortaApplicativaServizio getServizio() {
        return this.servizio;
    }

    /**
     * Sets the value of the servizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link PortaApplicativaServizio }
     *     
     */
    public void setServizio(PortaApplicativaServizio value) {
        this.servizio = value;
    }

    /**
     * Gets the value of the azione property.
     * 
     * @return
     *     possible object is
     *     {@link PortaApplicativaAzione }
     *     
     */
    public PortaApplicativaAzione getAzione() {
        return this.azione;
    }

    /**
     * Sets the value of the azione property.
     * 
     * @param value
     *     allowed object is
     *     {@link PortaApplicativaAzione }
     *     
     */
    public void setAzione(PortaApplicativaAzione value) {
        this.azione = value;
    }

    /**
     * Gets the value of the ruoli property.
     * 
     * @return
     *     possible object is
     *     {@link AutorizzazioneRuoli }
     *     
     */
    public AutorizzazioneRuoli getRuoli() {
        return this.ruoli;
    }

    /**
     * Sets the value of the ruoli property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutorizzazioneRuoli }
     *     
     */
    public void setRuoli(AutorizzazioneRuoli value) {
        this.ruoli = value;
    }

    /**
     * Gets the value of the mtomProcessor property.
     * 
     * @return
     *     possible object is
     *     {@link MtomProcessor }
     *     
     */
    public MtomProcessor getMtomProcessor() {
        return this.mtomProcessor;
    }

    /**
     * Sets the value of the mtomProcessor property.
     * 
     * @param value
     *     allowed object is
     *     {@link MtomProcessor }
     *     
     */
    public void setMtomProcessor(MtomProcessor value) {
        this.mtomProcessor = value;
    }

    /**
     * Gets the value of the validazioneContenutiApplicativi property.
     * 
     * @return
     *     possible object is
     *     {@link ValidazioneContenutiApplicativi }
     *     
     */
    public ValidazioneContenutiApplicativi getValidazioneContenutiApplicativi() {
        return this.validazioneContenutiApplicativi;
    }

    /**
     * Sets the value of the validazioneContenutiApplicativi property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidazioneContenutiApplicativi }
     *     
     */
    public void setValidazioneContenutiApplicativi(ValidazioneContenutiApplicativi value) {
        this.validazioneContenutiApplicativi = value;
    }

    /**
     * Gets the value of the tipoSoggettoProprietario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoSoggettoProprietario() {
        return this.tipoSoggettoProprietario;
    }

    /**
     * Sets the value of the tipoSoggettoProprietario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoSoggettoProprietario(String value) {
        this.tipoSoggettoProprietario = value;
    }

    /**
     * Gets the value of the nomeSoggettoProprietario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeSoggettoProprietario() {
        return this.nomeSoggettoProprietario;
    }

    /**
     * Sets the value of the nomeSoggettoProprietario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeSoggettoProprietario(String value) {
        this.nomeSoggettoProprietario = value;
    }

    /**
     * Gets the value of the statoMessageSecurity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatoMessageSecurity() {
        return this.statoMessageSecurity;
    }

    /**
     * Sets the value of the statoMessageSecurity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatoMessageSecurity(String value) {
        this.statoMessageSecurity = value;
    }

    /**
     * Gets the value of the nome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Sets the value of the nome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNome(String value) {
        this.nome = value;
    }

    /**
     * Gets the value of the descrizione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescrizione() {
        return this.descrizione;
    }

    /**
     * Sets the value of the descrizione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescrizione(String value) {
        this.descrizione = value;
    }

    /**
     * Gets the value of the ricevutaAsincronaSimmetrica property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getRicevutaAsincronaSimmetrica() {
        return this.ricevutaAsincronaSimmetrica;
    }

    /**
     * Sets the value of the ricevutaAsincronaSimmetrica property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setRicevutaAsincronaSimmetrica(StatoFunzionalita value) {
        this.ricevutaAsincronaSimmetrica = value;
    }

    /**
     * Gets the value of the ricevutaAsincronaAsimmetrica property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getRicevutaAsincronaAsimmetrica() {
        return this.ricevutaAsincronaAsimmetrica;
    }

    /**
     * Sets the value of the ricevutaAsincronaAsimmetrica property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setRicevutaAsincronaAsimmetrica(StatoFunzionalita value) {
        this.ricevutaAsincronaAsimmetrica = value;
    }

    /**
     * Gets the value of the integrazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntegrazione() {
        return this.integrazione;
    }

    /**
     * Sets the value of the integrazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntegrazione(String value) {
        this.integrazione = value;
    }

    /**
     * Gets the value of the allegaBody property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getAllegaBody() {
        return this.allegaBody;
    }

    /**
     * Sets the value of the allegaBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setAllegaBody(StatoFunzionalita value) {
        this.allegaBody = value;
    }

    /**
     * Gets the value of the scartaBody property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getScartaBody() {
        return this.scartaBody;
    }

    /**
     * Sets the value of the scartaBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setScartaBody(StatoFunzionalita value) {
        this.scartaBody = value;
    }

    /**
     * Gets the value of the gestioneManifest property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getGestioneManifest() {
        return this.gestioneManifest;
    }

    /**
     * Sets the value of the gestioneManifest property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setGestioneManifest(StatoFunzionalita value) {
        this.gestioneManifest = value;
    }

    /**
     * Gets the value of the stateless property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getStateless() {
        return this.stateless;
    }

    /**
     * Sets the value of the stateless property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setStateless(StatoFunzionalita value) {
        this.stateless = value;
    }

    /**
     * Gets the value of the behaviour property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBehaviour() {
        return this.behaviour;
    }

    /**
     * Sets the value of the behaviour property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBehaviour(String value) {
        this.behaviour = value;
    }

    /**
     * Gets the value of the autenticazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutenticazione() {
        return this.autenticazione;
    }

    /**
     * Sets the value of the autenticazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutenticazione(String value) {
        this.autenticazione = value;
    }

    /**
     * Gets the value of the autenticazioneOpzionale property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getAutenticazioneOpzionale() {
        return this.autenticazioneOpzionale;
    }

    /**
     * Sets the value of the autenticazioneOpzionale property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setAutenticazioneOpzionale(StatoFunzionalita value) {
        this.autenticazioneOpzionale = value;
    }

    /**
     * Gets the value of the autorizzazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutorizzazione() {
        return this.autorizzazione;
    }

    /**
     * Sets the value of the autorizzazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutorizzazione(String value) {
        this.autorizzazione = value;
    }

    /**
     * Gets the value of the autorizzazioneContenuto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutorizzazioneContenuto() {
        return this.autorizzazioneContenuto;
    }

    /**
     * Sets the value of the autorizzazioneContenuto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutorizzazioneContenuto(String value) {
        this.autorizzazioneContenuto = value;
    }

    /**
     * Gets the value of the oraRegistrazioneMin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOraRegistrazioneMin() {
        return this.oraRegistrazioneMin;
    }

    /**
     * Sets the value of the oraRegistrazioneMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOraRegistrazioneMin(XMLGregorianCalendar value) {
        this.oraRegistrazioneMin = value;
    }

    /**
     * Gets the value of the oraRegistrazioneMax property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOraRegistrazioneMax() {
        return this.oraRegistrazioneMax;
    }

    /**
     * Sets the value of the oraRegistrazioneMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOraRegistrazioneMax(XMLGregorianCalendar value) {
        this.oraRegistrazioneMax = value;
    }

    /**
     * Gets the value of the orCondition property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOrCondition() {
        return this.orCondition;
    }

    /**
     * Sets the value of the orCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOrCondition(Boolean value) {
        this.orCondition = value;
    }

    /**
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLimit() {
        return this.limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLimit(BigInteger value) {
        this.limit = value;
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOffset() {
        return this.offset;
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOffset(BigInteger value) {
        this.offset = value;
    }

}
