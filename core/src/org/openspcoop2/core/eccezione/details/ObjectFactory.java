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
package org.openspcoop2.core.eccezione.details;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.eccezione.details package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.eccezione.details
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Eccezione }
     */
    public Eccezione createEccezione() {
        return new Eccezione();
    }

    /**
     * Create an instance of {@link DominioSoggetto }
     */
    public DominioSoggetto createDominioSoggetto() {
        return new DominioSoggetto();
    }

    /**
     * Create an instance of {@link Dettagli }
     */
    public Dettagli createDettagli() {
        return new Dettagli();
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

    /**
     * Create an instance of {@link Eccezioni }
     */
    public Eccezioni createEccezioni() {
        return new Eccezioni();
    }

    /**
     * Create an instance of {@link DettaglioEccezione }
     */
    public DettaglioEccezione createDettaglioEccezione() {
        return new DettaglioEccezione();
    }

    private final static QName _FaultDetails = new QName("http://govway.org/integration/fault/details", "fault-details");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DettaglioEccezione }{@code >}}
     */
    @XmlElementDecl(namespace = "http://govway.org/integration/fault/details", name="fault-details")
    public JAXBElement<DettaglioEccezione> createFaultDetails() {
        return new JAXBElement<DettaglioEccezione>(_FaultDetails, DettaglioEccezione.class, null, this.createDettaglioEccezione());
    }
    public JAXBElement<DettaglioEccezione> createFaultDetails(DettaglioEccezione faultDetails) {
        return new JAXBElement<DettaglioEccezione>(_FaultDetails, DettaglioEccezione.class, null, faultDetails);
    }


 }
