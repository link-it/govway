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

package org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;


/**
 * <p>Java class for findAllIdsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAllIdsResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idAccordoServizioParteSpecifica" type="{http://www.openspcoop2.org/core/registry}id-accordo-servizio-parte-specifica" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findAllIdsResponse", propOrder = {
    "idAccordoServizioParteSpecifica"
})
public class FindAllIdsResponse {

    protected List<IdAccordoServizioParteSpecifica> idAccordoServizioParteSpecifica;

    /**
     * Gets the value of the idAccordoServizioParteSpecifica property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idAccordoServizioParteSpecifica property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdAccordoServizioParteSpecifica().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdAccordoServizioParteSpecifica }
     * 
     * 
     */
    public List<IdAccordoServizioParteSpecifica> getIdAccordoServizioParteSpecifica() {
        if (this.idAccordoServizioParteSpecifica == null) {
            this.idAccordoServizioParteSpecifica = new ArrayList<IdAccordoServizioParteSpecifica>();
        }
        return this.idAccordoServizioParteSpecifica;
    }

}
