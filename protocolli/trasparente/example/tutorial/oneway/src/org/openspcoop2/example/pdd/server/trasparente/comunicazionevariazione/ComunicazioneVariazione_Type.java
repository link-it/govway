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

package org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione.PersonaType;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione}personaType">
 *       &lt;attribute name="CF" use="required" type="{http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione}codiceFiscaleType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "comunicazioneVariazione")
public class ComunicazioneVariazione_Type
    extends PersonaType
{

    @XmlAttribute(name = "CF", required = true)
    protected String cf;

    /**
     * Recupera il valore della proprietà cf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCF() {
        return this.cf;
    }

    /**
     * Imposta il valore della proprietà cf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCF(String value) {
        this.cf = value;
    }

}
