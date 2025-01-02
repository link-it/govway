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

package org.openspcoop2.example.pdd.server.stampadocumento;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://openspcoop2.org/example/pdd/server/StampaDocumento}richiestaDocumento">
 *       &lt;attribute name="CF" use="required" type="{http://openspcoop2.org/example/pdd/server/StampaDocumento}codiceFiscaleType" />
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
@XmlRootElement(name = "stampaDocumento")
public class StampaDocumento_Type
    extends RichiestaDocumento
{

    @XmlAttribute(name = "CF", required = true)
    protected String cf;

    /**
     * Gets the value of the cf property.
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
     * Sets the value of the cf property.
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
