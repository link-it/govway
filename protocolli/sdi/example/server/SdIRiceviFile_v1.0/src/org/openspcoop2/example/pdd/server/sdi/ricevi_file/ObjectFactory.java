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

package org.openspcoop2.example.pdd.server.sdi.ricevi_file;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.example.pdd.server.sdi.ricevi_file package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _NotificaFileNonRecapitabile_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaFileNonRecapitabile");
    private static final QName _RicevutaConsegna_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "ricevutaConsegna");
    private static final QName _NotificaMancataConsegna_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaMancataConsegna");
    private static final QName _FileSdIAccoglienza_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "fileSdIAccoglienza");
    private static final QName _RispostaSdIRiceviFile_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "rispostaSdIRiceviFile");
    private static final QName _NotificaEsito_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaEsito");
    private static final QName _NotificaScarto_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaScarto");
    private static final QName _FileSdI_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "fileSdI");
    private static final QName _NotificaDecorrenzaTermini_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaDecorrenzaTermini");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.example.pdd.server.sdi.ricevi_file
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FileSdIType }
     * 
     */
    public FileSdIType createFileSdIType() {
        return new FileSdIType();
    }

    /**
     * Create an instance of {@link RispostaSdIRiceviFileType }
     * 
     */
    public RispostaSdIRiceviFileType createRispostaSdIRiceviFileType() {
        return new RispostaSdIRiceviFileType();
    }

    /**
     * Create an instance of {@link FileSdIBaseType }
     * 
     */
    public FileSdIBaseType createFileSdIBaseType() {
        return new FileSdIBaseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name = "notificaFileNonRecapitabile")
    public JAXBElement<FileSdIType> createNotificaFileNonRecapitabile(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._NotificaFileNonRecapitabile_QNAME, FileSdIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name = "ricevutaConsegna")
    public JAXBElement<FileSdIType> createRicevutaConsegna(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._RicevutaConsegna_QNAME, FileSdIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name = "notificaMancataConsegna")
    public JAXBElement<FileSdIType> createNotificaMancataConsegna(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._NotificaMancataConsegna_QNAME, FileSdIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIBaseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name = "fileSdIAccoglienza")
    public JAXBElement<FileSdIBaseType> createFileSdIAccoglienza(FileSdIBaseType value) {
        return new JAXBElement<FileSdIBaseType>(ObjectFactory._FileSdIAccoglienza_QNAME, FileSdIBaseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaSdIRiceviFileType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name = "rispostaSdIRiceviFile")
    public JAXBElement<RispostaSdIRiceviFileType> createRispostaSdIRiceviFile(RispostaSdIRiceviFileType value) {
        return new JAXBElement<RispostaSdIRiceviFileType>(ObjectFactory._RispostaSdIRiceviFile_QNAME, RispostaSdIRiceviFileType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name = "notificaEsito")
    public JAXBElement<FileSdIType> createNotificaEsito(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._NotificaEsito_QNAME, FileSdIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name = "notificaScarto")
    public JAXBElement<FileSdIType> createNotificaScarto(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._NotificaScarto_QNAME, FileSdIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name = "fileSdI")
    public JAXBElement<FileSdIType> createFileSdI(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._FileSdI_QNAME, FileSdIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name = "notificaDecorrenzaTermini")
    public JAXBElement<FileSdIType> createNotificaDecorrenzaTermini(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._NotificaDecorrenzaTermini_QNAME, FileSdIType.class, null, value);
    }

}
