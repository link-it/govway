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

package org.openspcoop2.example.pdd.server.sdi.ricevi_notifica;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rispostaRiceviFatture_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rispostaRiceviFatture_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Esito" type="{http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types}esitoRicezione_Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rispostaRiceviFatture_Type", propOrder = {
    "esito"
})
public class RispostaRiceviFattureType {

    @XmlElement(name = "Esito", required = true)
    protected EsitoRicezioneType esito;

    /**
     * Gets the value of the esito property.
     * 
     * @return
     *     possible object is
     *     {@link EsitoRicezioneType }
     *     
     */
    public EsitoRicezioneType getEsito() {
        return this.esito;
    }

    /**
     * Sets the value of the esito property.
     * 
     * @param value
     *     allowed object is
     *     {@link EsitoRicezioneType }
     *     
     */
    public void setEsito(EsitoRicezioneType value) {
        this.esito = value;
    }

}
