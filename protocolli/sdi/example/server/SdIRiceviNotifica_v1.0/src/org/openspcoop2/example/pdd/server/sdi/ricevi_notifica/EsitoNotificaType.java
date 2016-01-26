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

package org.openspcoop2.example.pdd.server.sdi.ricevi_notifica;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for esitoNotifica_Type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="esitoNotifica_Type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ES00"/>
 *     &lt;enumeration value="ES01"/>
 *     &lt;enumeration value="ES02"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlType(name = "esitoNotifica_Type")
@XmlEnum
public enum EsitoNotificaType {


    /**
     * 
     * 						ES00 = NOTIFICA NON ACCETTATA
     * 					
     * 
     */
    @XmlEnumValue("ES00")
    ES_00("ES00"),

    /**
     * 
     * 						ES01 = NOTIFICA ACCETTATA
     * 					
     * 
     */
    @XmlEnumValue("ES01")
    ES_01("ES01"),

    /**
     * 
     * 						ES02 = SERVIZIO NON DISPONIBILE
     * 					
     * 
     */
    @XmlEnumValue("ES02")
    ES_02("ES02");
    private final String value;

    EsitoNotificaType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static EsitoNotificaType fromValue(String v) {
        for (EsitoNotificaType c: EsitoNotificaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
