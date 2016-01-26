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

package org.openspcoop2.example.pdd.server.sdi.ricevi_file;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for erroreInvio_Type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="erroreInvio_Type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EI01"/>
 *     &lt;enumeration value="EI02"/>
 *     &lt;enumeration value="EI03"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlType(name = "erroreInvio_Type")
@XmlEnum
public enum ErroreInvioType {


    /**
     * 
     * 						EI01 = FILE VUOTO
     * 					
     * 
     */
    @XmlEnumValue("EI01")
    EI_01("EI01"),

    /**
     * 
     * 						EI02 = SERVIZIO NON DISPONIBILE
     * 					
     * 
     */
    @XmlEnumValue("EI02")
    EI_02("EI02"),

    /**
     * 
     * 						EI03 = UTENTE NON ABILITATO
     * 					
     * 
     */
    @XmlEnumValue("EI03")
    EI_03("EI03");
    private final String value;

    ErroreInvioType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static ErroreInvioType fromValue(String v) {
        for (ErroreInvioType c: ErroreInvioType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
