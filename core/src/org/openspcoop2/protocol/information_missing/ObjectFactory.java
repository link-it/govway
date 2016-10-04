/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.protocol.information_missing package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.protocol.information_missing
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AccordoServizioParteComune }
     */
    public AccordoServizioParteComune createAccordoServizioParteComune() {
        return new AccordoServizioParteComune();
    }

    /**
     * Create an instance of {@link Wizard }
     */
    public Wizard createWizard() {
        return new Wizard();
    }

    /**
     * Create an instance of {@link Requisiti }
     */
    public Requisiti createRequisiti() {
        return new Requisiti();
    }

    /**
     * Create an instance of {@link ReplaceMatchType }
     */
    public ReplaceMatchType createReplaceMatchType() {
        return new ReplaceMatchType();
    }

    /**
     * Create an instance of {@link AccordoCooperazione }
     */
    public AccordoCooperazione createAccordoCooperazione() {
        return new AccordoCooperazione();
    }

    /**
     * Create an instance of {@link Soggetto }
     */
    public Soggetto createSoggetto() {
        return new Soggetto();
    }

    /**
     * Create an instance of {@link AccordoServizioParteSpecifica }
     */
    public AccordoServizioParteSpecifica createAccordoServizioParteSpecifica() {
        return new AccordoServizioParteSpecifica();
    }

    /**
     * Create an instance of {@link Openspcoop2 }
     */
    public Openspcoop2 createOpenspcoop2() {
        return new Openspcoop2();
    }

    /**
     * Create an instance of {@link Proprieta }
     */
    public Proprieta createProprieta() {
        return new Proprieta();
    }

    /**
     * Create an instance of {@link Fruitore }
     */
    public Fruitore createFruitore() {
        return new Fruitore();
    }

    /**
     * Create an instance of {@link ServizioApplicativo }
     */
    public ServizioApplicativo createServizioApplicativo() {
        return new ServizioApplicativo();
    }

    /**
     * Create an instance of {@link RequisitoProtocollo }
     */
    public RequisitoProtocollo createRequisitoProtocollo() {
        return new RequisitoProtocollo();
    }

    /**
     * Create an instance of {@link ReplaceMatchFieldType }
     */
    public ReplaceMatchFieldType createReplaceMatchFieldType() {
        return new ReplaceMatchFieldType();
    }

    /**
     * Create an instance of {@link Input }
     */
    public Input createInput() {
        return new Input();
    }

    private final static QName _AccordoServizioComposto = new QName("http://www.openspcoop2.org/protocol/information_missing", "accordo-servizio-composto");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AccordoServizioParteComune }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/protocol/information_missing", name="accordo-servizio-composto")
    public JAXBElement<AccordoServizioParteComune> createAccordoServizioComposto() {
        return new JAXBElement<AccordoServizioParteComune>(_AccordoServizioComposto, AccordoServizioParteComune.class, null, this.createAccordoServizioParteComune());
    }
    public JAXBElement<AccordoServizioParteComune> createAccordoServizioComposto(AccordoServizioParteComune accordoServizioComposto) {
        return new JAXBElement<AccordoServizioParteComune>(_AccordoServizioComposto, AccordoServizioParteComune.class, null, accordoServizioComposto);
    }


 }
