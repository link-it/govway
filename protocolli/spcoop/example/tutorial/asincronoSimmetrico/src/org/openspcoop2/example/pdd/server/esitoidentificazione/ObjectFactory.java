/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.example.pdd.server.esitoidentificazione;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.example.pdd.server.stampadocumento package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Risultato_QNAME = new QName("http://openspcoop2.org/example/pdd/server/IdentificaSoggetto", "risultato");
    private final static QName _RisultatoResponse_QNAME = new QName("http://openspcoop2.org/example/pdd/server/IdentificaSoggetto", "risultatoResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.example.pdd.server.stampadocumento
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
    @XmlElementDecl(namespace = "http://openspcoop2.org/example/pdd/server/IdentificaSoggetto", name = "risultato")
    public JAXBElement<PersonaType> createRisultato(PersonaType value) {
        return new JAXBElement<PersonaType>(ObjectFactory._Risultato_QNAME, PersonaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://openspcoop2.org/example/pdd/server/IdentificaSoggetto", name = "risultatoResponse")
    public JAXBElement<Object> createRisultatoResponse(Object value) {
        return new JAXBElement<Object>(ObjectFactory._RisultatoResponse_QNAME, Object.class, null, value);
    }

}
