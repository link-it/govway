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

package org.openspcoop2.example.server.rpc.literal.skeleton;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.example.server.rpc.literal.skeleton package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _Nominativo_QNAME = new QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "nominativo");
    private static final QName _Indirizzo_QNAME = new QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "indirizzo");
    private static final QName _OraRegistrazione_QNAME = new QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "ora-registrazione");
    private static final QName _Esito_QNAME = new QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esito");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.example.server.rpc.literal.skeleton
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NominativoType }
     * 
     * @return
     *     the new instance of {@link NominativoType }
     */
    public NominativoType createNominativoType() {
        return new NominativoType();
    }

    /**
     * Create an instance of {@link EsitoType }
     * 
     * @return
     *     the new instance of {@link EsitoType }
     */
    public EsitoType createEsitoType() {
        return new EsitoType();
    }

    /**
     * Create an instance of {@link MessaggioType }
     * 
     * @return
     *     the new instance of {@link MessaggioType }
     */
    public MessaggioType createMessaggioType() {
        return new MessaggioType();
    }

    /**
     * Create an instance of {@link FixedMessaggioType }
     * 
     * @return
     *     the new instance of {@link FixedMessaggioType }
     */
    public FixedMessaggioType createFixedMessaggioType() {
        return new FixedMessaggioType();
    }

    /**
     * Create an instance of {@link Fixed2MessaggioType }
     * 
     * @return
     *     the new instance of {@link Fixed2MessaggioType }
     */
    public Fixed2MessaggioType createFixed2MessaggioType() {
        return new Fixed2MessaggioType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NominativoType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NominativoType }{@code >}
     */
    @XmlElementDecl(namespace = "http://openspcoop2.org/ValidazioneContenutiWS/Service/types", name = "nominativo")
    public JAXBElement<NominativoType> createNominativo(NominativoType value) {
        return new JAXBElement<>(ObjectFactory._Nominativo_QNAME, NominativoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://openspcoop2.org/ValidazioneContenutiWS/Service/types", name = "indirizzo")
    public JAXBElement<String> createIndirizzo(String value) {
        return new JAXBElement<>(ObjectFactory._Indirizzo_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "http://openspcoop2.org/ValidazioneContenutiWS/Service/types", name = "ora-registrazione")
    public JAXBElement<XMLGregorianCalendar> createOraRegistrazione(XMLGregorianCalendar value) {
        return new JAXBElement<>(ObjectFactory._OraRegistrazione_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EsitoType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EsitoType }{@code >}
     */
    @XmlElementDecl(namespace = "http://openspcoop2.org/ValidazioneContenutiWS/Service/types", name = "esito")
    public JAXBElement<EsitoType> createEsito(EsitoType value) {
        return new JAXBElement<>(ObjectFactory._Esito_QNAME, EsitoType.class, null, value);
    }

}
