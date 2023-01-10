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

package org.openspcoop2.core.config.ws.client.servizioapplicativo.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;


/**
 * <p>Java class for invocazione-porta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-porta"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gestione-errore" type="{http://www.openspcoop2.org/core/config/management}invocazione-porta-gestione-errore" minOccurs="0"/&gt;
 *         &lt;element name="invio-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="sbustamento-informazioni-protocollo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invocazione-porta", propOrder = {
    "gestioneErrore",
    "invioPerRiferimento",
    "sbustamentoInformazioniProtocollo"
})
public class InvocazionePorta {

    @XmlElement(name = "gestione-errore")
    protected InvocazionePortaGestioneErrore gestioneErrore;
    @XmlElement(name = "invio-per-riferimento")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita invioPerRiferimento;
    @XmlElement(name = "sbustamento-informazioni-protocollo")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita sbustamentoInformazioniProtocollo;

    /**
     * Gets the value of the gestioneErrore property.
     * 
     * @return
     *     possible object is
     *     {@link InvocazionePortaGestioneErrore }
     *     
     */
    public InvocazionePortaGestioneErrore getGestioneErrore() {
        return this.gestioneErrore;
    }

    /**
     * Sets the value of the gestioneErrore property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvocazionePortaGestioneErrore }
     *     
     */
    public void setGestioneErrore(InvocazionePortaGestioneErrore value) {
        this.gestioneErrore = value;
    }

    /**
     * Gets the value of the invioPerRiferimento property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getInvioPerRiferimento() {
        return this.invioPerRiferimento;
    }

    /**
     * Sets the value of the invioPerRiferimento property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setInvioPerRiferimento(StatoFunzionalita value) {
        this.invioPerRiferimento = value;
    }

    /**
     * Gets the value of the sbustamentoInformazioniProtocollo property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getSbustamentoInformazioniProtocollo() {
        return this.sbustamentoInformazioniProtocollo;
    }

    /**
     * Sets the value of the sbustamentoInformazioniProtocollo property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setSbustamentoInformazioniProtocollo(StatoFunzionalita value) {
        this.sbustamentoInformazioniProtocollo = value;
    }

}
