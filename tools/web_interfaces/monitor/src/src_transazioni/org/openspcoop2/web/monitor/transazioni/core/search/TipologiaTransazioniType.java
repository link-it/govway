/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package org.openspcoop2.web.monitor.transazioni.core.search;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tipologia-transazioni_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>{@code
 * <simpleType name="tipologia-transazioni_type">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="ProxyTrasparente/IntegrationManager"/>
 *     <enumeration value="ProxyTrasparente"/>
 *     <enumeration value="IntegrationManager"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlType(name = "tipologia-transazioni_type", namespace = "http://www.openspcoop2.org/web/monitor/transazioni/core/search")
@XmlEnum
public enum TipologiaTransazioniType {

    @XmlEnumValue("ProxyTrasparente/IntegrationManager")
    PROXY_TRASPARENTE_INTEGRATION_MANAGER("ProxyTrasparente/IntegrationManager"),
    @XmlEnumValue("ProxyTrasparente")
    PROXY_TRASPARENTE("ProxyTrasparente"),
    @XmlEnumValue("IntegrationManager")
    INTEGRATION_MANAGER("IntegrationManager");
    private final String value;

    TipologiaTransazioniType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static TipologiaTransazioniType fromValue(String v) {
        for (TipologiaTransazioniType c: TipologiaTransazioniType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
