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
package org.openspcoop2.pdd.monitor;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.pdd.monitor package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.pdd.monitor
     * 
     */
    public ObjectFactory() {
        // Create a new ObjectFactory
    }

    /**
     * Create an instance of {@link ServizioApplicativoConsegna }
     */
    public ServizioApplicativoConsegna createServizioApplicativoConsegna() {
        return new ServizioApplicativoConsegna();
    }

    /**
     * Create an instance of {@link Openspcoop2 }
     */
    public Openspcoop2 createOpenspcoop2() {
        return new Openspcoop2();
    }

    /**
     * Create an instance of {@link Filtro }
     */
    public Filtro createFiltro() {
        return new Filtro();
    }

    /**
     * Create an instance of {@link Proprieta }
     */
    public Proprieta createProprieta() {
        return new Proprieta();
    }

    /**
     * Create an instance of {@link StatoPdd }
     */
    public StatoPdd createStatoPdd() {
        return new StatoPdd();
    }

    /**
     * Create an instance of {@link Messaggio }
     */
    public Messaggio createMessaggio() {
        return new Messaggio();
    }

    /**
     * Create an instance of {@link BustaServizio }
     */
    public BustaServizio createBustaServizio() {
        return new BustaServizio();
    }

    /**
     * Create an instance of {@link BustaSoggetto }
     */
    public BustaSoggetto createBustaSoggetto() {
        return new BustaSoggetto();
    }

    /**
     * Create an instance of {@link Dettaglio }
     */
    public Dettaglio createDettaglio() {
        return new Dettaglio();
    }

    /**
     * Create an instance of {@link Busta }
     */
    public Busta createBusta() {
        return new Busta();
    }


 }
