
package org.openspcoop2.core.config.ws.client.servizioapplicativo.all;

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
 *     &lt;enumeration value="Soggetto"/&gt;
 *     &lt;enumeration value="PortaDelegata"/&gt;
 *     &lt;enumeration value="PortaApplicativa"/&gt;
 *     &lt;enumeration value="ServizioApplicativo"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "identified")
@XmlEnum
public enum Identified {

    @XmlEnumValue("Soggetto")
    SOGGETTO("Soggetto"),
    @XmlEnumValue("PortaDelegata")
    PORTA_DELEGATA("PortaDelegata"),
    @XmlEnumValue("PortaApplicativa")
    PORTA_APPLICATIVA("PortaApplicativa"),
    @XmlEnumValue("ServizioApplicativo")
    SERVIZIO_APPLICATIVO("ServizioApplicativo");
    private final String value;

    Identified(String v) {
        value = v;
    }

    public String value() {
        return value;
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
