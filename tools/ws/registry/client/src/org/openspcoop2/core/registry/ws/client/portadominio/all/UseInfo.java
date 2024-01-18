/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.core.registry.ws.client.portadominio.all;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for use-info complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="use-info"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="inUseCondition" type="{http://www.openspcoop2.org/core/registry/management}in-use-condition" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="used" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "use-info", propOrder = {
    "inUseCondition",
    "used"
})
public class UseInfo {

    protected List<InUseCondition> inUseCondition;
    protected boolean used;

    /**
     * Gets the value of the inUseCondition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inUseCondition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInUseCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InUseCondition }
     * 
     * 
     */
    public List<InUseCondition> getInUseCondition() {
        if (this.inUseCondition == null) {
            this.inUseCondition = new ArrayList<InUseCondition>();
        }
        return this.inUseCondition;
    }

    /**
     * Gets the value of the used property.
     * 
     */
    public boolean isUsed() {
        return this.used;
    }

    /**
     * Sets the value of the used property.
     * 
     */
    public void setUsed(boolean value) {
        this.used = value;
    }

}
