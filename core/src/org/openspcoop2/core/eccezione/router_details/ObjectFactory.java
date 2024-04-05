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
package org.openspcoop2.core.eccezione.router_details;

import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.eccezione.router_details package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/

@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.eccezione.router_details
     * 
     */
    public ObjectFactory() {
        // Create a new ObjectFactory
    }

    /**
     * Create an instance of {@link DominioSoggetto }
     */
    public DominioSoggetto createDominioSoggetto() {
        return new DominioSoggetto();
    }

    /**
     * Create an instance of {@link DettaglioRouting }
     */
    public DettaglioRouting createDettaglioRouting() {
        return new DettaglioRouting();
    }

    /**
     * Create an instance of {@link Dettaglio }
     */
    public Dettaglio createDettaglio() {
        return new Dettaglio();
    }

    /**
     * Create an instance of {@link Dominio }
     */
    public Dominio createDominio() {
        return new Dominio();
    }

    private static final QName _RouterDetails = new QName("http://govway.org/integration/fault/router_details", "router-details");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DettaglioRouting }{@code >}}
     */
    @XmlElementDecl(namespace = "http://govway.org/integration/fault/router_details", name="router-details")
    public JAXBElement<DettaglioRouting> createRouterDetails() {
        return new JAXBElement<DettaglioRouting>(_RouterDetails, DettaglioRouting.class, null, this.createDettaglioRouting());
    }
    public JAXBElement<DettaglioRouting> createRouterDetails(DettaglioRouting routerDetails) {
        return new JAXBElement<DettaglioRouting>(_RouterDetails, DettaglioRouting.class, null, routerDetails);
    }


 }
