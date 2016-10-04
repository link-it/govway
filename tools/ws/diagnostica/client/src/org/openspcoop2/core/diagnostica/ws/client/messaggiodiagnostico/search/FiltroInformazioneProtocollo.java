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

package org.openspcoop2.core.diagnostica.ws.client.messaggiodiagnostico.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.diagnostica.constants.TipoPdD;


/**
 * <p>Java class for filtro-informazione-protocollo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filtro-informazione-protocollo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tipo-porta" type="{http://www.openspcoop2.org/core/diagnostica}TipoPdD" minOccurs="0"/>
 *         &lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ricerca-solo-messaggi-correlati-informazioni-protocollo" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="fruitore" type="{http://www.openspcoop2.org/core/diagnostica/management}soggetto" minOccurs="0"/>
 *         &lt;element name="erogatore" type="{http://www.openspcoop2.org/core/diagnostica/management}soggetto" minOccurs="0"/>
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/core/diagnostica/management}servizio" minOccurs="0"/>
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificativo-correlazione-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificativo-correlazione-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="correlazione-applicativa-and-match" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="servizio-applicativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filtro-informazione-protocollo", propOrder = {
    "tipoPorta",
    "nomePorta",
    "ricercaSoloMessaggiCorrelatiInformazioniProtocollo",
    "fruitore",
    "erogatore",
    "servizio",
    "azione",
    "identificativoCorrelazioneRichiesta",
    "identificativoCorrelazioneRisposta",
    "correlazioneApplicativaAndMatch",
    "servizioApplicativo"
})
public class FiltroInformazioneProtocollo {

    @XmlElement(name = "tipo-porta")
    protected TipoPdD tipoPorta;
    @XmlElement(name = "nome-porta")
    protected String nomePorta;
    @XmlElement(name = "ricerca-solo-messaggi-correlati-informazioni-protocollo")
    protected Boolean ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
    protected Soggetto fruitore;
    protected Soggetto erogatore;
    protected Servizio servizio;
    protected String azione;
    @XmlElement(name = "identificativo-correlazione-richiesta")
    protected String identificativoCorrelazioneRichiesta;
    @XmlElement(name = "identificativo-correlazione-risposta")
    protected String identificativoCorrelazioneRisposta;
    @XmlElement(name = "correlazione-applicativa-and-match")
    protected Boolean correlazioneApplicativaAndMatch;
    @XmlElement(name = "servizio-applicativo")
    protected String servizioApplicativo;

    /**
     * Gets the value of the tipoPorta property.
     * 
     * @return
     *     possible object is
     *     {@link TipoPdD }
     *     
     */
    public TipoPdD getTipoPorta() {
        return this.tipoPorta;
    }

    /**
     * Sets the value of the tipoPorta property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPdD }
     *     
     */
    public void setTipoPorta(TipoPdD value) {
        this.tipoPorta = value;
    }

    /**
     * Gets the value of the nomePorta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomePorta() {
        return this.nomePorta;
    }

    /**
     * Sets the value of the nomePorta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomePorta(String value) {
        this.nomePorta = value;
    }

    /**
     * Gets the value of the ricercaSoloMessaggiCorrelatiInformazioniProtocollo property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRicercaSoloMessaggiCorrelatiInformazioniProtocollo() {
        return this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
    }

    /**
     * Sets the value of the ricercaSoloMessaggiCorrelatiInformazioniProtocollo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRicercaSoloMessaggiCorrelatiInformazioniProtocollo(Boolean value) {
        this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo = value;
    }

    /**
     * Gets the value of the fruitore property.
     * 
     * @return
     *     possible object is
     *     {@link Soggetto }
     *     
     */
    public Soggetto getFruitore() {
        return this.fruitore;
    }

    /**
     * Sets the value of the fruitore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Soggetto }
     *     
     */
    public void setFruitore(Soggetto value) {
        this.fruitore = value;
    }

    /**
     * Gets the value of the erogatore property.
     * 
     * @return
     *     possible object is
     *     {@link Soggetto }
     *     
     */
    public Soggetto getErogatore() {
        return this.erogatore;
    }

    /**
     * Sets the value of the erogatore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Soggetto }
     *     
     */
    public void setErogatore(Soggetto value) {
        this.erogatore = value;
    }

    /**
     * Gets the value of the servizio property.
     * 
     * @return
     *     possible object is
     *     {@link Servizio }
     *     
     */
    public Servizio getServizio() {
        return this.servizio;
    }

    /**
     * Sets the value of the servizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Servizio }
     *     
     */
    public void setServizio(Servizio value) {
        this.servizio = value;
    }

    /**
     * Gets the value of the azione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAzione() {
        return this.azione;
    }

    /**
     * Sets the value of the azione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAzione(String value) {
        this.azione = value;
    }

    /**
     * Gets the value of the identificativoCorrelazioneRichiesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoCorrelazioneRichiesta() {
        return this.identificativoCorrelazioneRichiesta;
    }

    /**
     * Sets the value of the identificativoCorrelazioneRichiesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoCorrelazioneRichiesta(String value) {
        this.identificativoCorrelazioneRichiesta = value;
    }

    /**
     * Gets the value of the identificativoCorrelazioneRisposta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoCorrelazioneRisposta() {
        return this.identificativoCorrelazioneRisposta;
    }

    /**
     * Sets the value of the identificativoCorrelazioneRisposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoCorrelazioneRisposta(String value) {
        this.identificativoCorrelazioneRisposta = value;
    }

    /**
     * Gets the value of the correlazioneApplicativaAndMatch property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCorrelazioneApplicativaAndMatch() {
        return this.correlazioneApplicativaAndMatch;
    }

    /**
     * Sets the value of the correlazioneApplicativaAndMatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCorrelazioneApplicativaAndMatch(Boolean value) {
        this.correlazioneApplicativaAndMatch = value;
    }

    /**
     * Gets the value of the servizioApplicativo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServizioApplicativo() {
        return this.servizioApplicativo;
    }

    /**
     * Sets the value of the servizioApplicativo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServizioApplicativo(String value) {
        this.servizioApplicativo = value;
    }

}
