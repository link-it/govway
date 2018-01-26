/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

package org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for identified.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="identified"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AccordoCooperazione"/&gt;
 *     &lt;enumeration value="AccordoServizioParteComune"/&gt;
 *     &lt;enumeration value="PortaDominio"/&gt;
 *     &lt;enumeration value="Soggetto"/&gt;
 *     &lt;enumeration value="AccordoServizioParteSpecifica"/&gt;
 *     &lt;enumeration value="Ruolo"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "identified")
@XmlEnum
public enum Identified {

    @XmlEnumValue("AccordoCooperazione")
    ACCORDO_COOPERAZIONE("AccordoCooperazione"),
    @XmlEnumValue("AccordoServizioParteComune")
    ACCORDO_SERVIZIO_PARTE_COMUNE("AccordoServizioParteComune"),
    @XmlEnumValue("PortaDominio")
    PORTA_DOMINIO("PortaDominio"),
    @XmlEnumValue("Soggetto")
    SOGGETTO("Soggetto"),
    @XmlEnumValue("AccordoServizioParteSpecifica")
    ACCORDO_SERVIZIO_PARTE_SPECIFICA("AccordoServizioParteSpecifica"),
    @XmlEnumValue("Ruolo")
    RUOLO("Ruolo");
    private final String value;

    Identified(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static Identified fromValue(String v) {
        for (Identified c: Identified.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
