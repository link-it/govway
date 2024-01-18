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

package org.openspcoop2.example.pdd.server.richiestastatofamiglia;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * ObjectFactory
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _StatoFamiglia_QNAME = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "statoFamiglia");
    private static final QName _RichiestaStatoFamiglia_QNAME = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "richiestaStatoFamiglia");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.example.pdd.server.richiestastatofamiglia
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PersonaType }
     * 
     */
    public PersonaType createPersonaType() {
        return new PersonaType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PersonaType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", name = "statoFamiglia")
    public JAXBElement<PersonaType> createStatoFamiglia(PersonaType value) {
        return new JAXBElement<PersonaType>(_StatoFamiglia_QNAME, PersonaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", name = "richiestaStatoFamiglia")
    public JAXBElement<String> createRichiestaStatoFamiglia(String value) {
        return new JAXBElement<String>(_RichiestaStatoFamiglia_QNAME, String.class, null, value);
    }

}
