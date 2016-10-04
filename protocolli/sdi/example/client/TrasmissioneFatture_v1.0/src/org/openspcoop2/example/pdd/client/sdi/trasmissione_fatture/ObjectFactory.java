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

package org.openspcoop2.example.pdd.client.sdi.trasmissione_fatture;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.example.pdd.client.sdi.trasmissione_fatture package. 
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

    private final static QName _RicevutaConsegna_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "ricevutaConsegna");
    private final static QName _NotificaMancataConsegna_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaMancataConsegna");
    private final static QName _FileSdIAccoglienza_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "fileSdIAccoglienza");
    private final static QName _RispostaSdIRiceviFile_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "rispostaSdIRiceviFile");
    private final static QName _NotificaEsito_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaEsito");
    private final static QName _NotificaScarto_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaScarto");
    private final static QName _FileSdI_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "fileSdI");
    private final static QName _NotificaDecorrenzaTermini_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaDecorrenzaTermini");
    private final static QName _AttestazioneTrasmissioneFattura_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "attestazioneTrasmissioneFattura");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.example.pdd.client.sdi.trasmissione_fatture
     * 
     */
    public ObjectFactory() {
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
     * Create an instance of {@link FileSdIType }
     * 
     */
    public FileSdIType createFileSdIType() {
        return new FileSdIType();
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

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name = "attestazioneTrasmissioneFattura")
    public JAXBElement<FileSdIType> createAttestazioneTrasmissioneFattura(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._AttestazioneTrasmissioneFattura_QNAME, FileSdIType.class, null, value);
    }

}
