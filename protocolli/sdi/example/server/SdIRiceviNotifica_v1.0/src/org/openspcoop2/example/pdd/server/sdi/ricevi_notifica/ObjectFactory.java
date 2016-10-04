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

package org.openspcoop2.example.pdd.server.sdi.ricevi_notifica;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.example.pdd.server.sdi.ricevi_notifica package. 
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

    private final static QName _NotificaEsito_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "notificaEsito");
    private final static QName _FileSdI_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "fileSdI");
    private final static QName _NotificaDecorrenzaTermini_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "notificaDecorrenzaTermini");
    private final static QName _RispostaRiceviFatture_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "rispostaRiceviFatture");
    private final static QName _RispostaSdINotificaEsito_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "rispostaSdINotificaEsito");
    private final static QName _FileSdIConMetadati_QNAME = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "fileSdIConMetadati");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.example.pdd.server.sdi.ricevi_notifica
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RispostaSdINotificaEsitoType }
     * 
     */
    public RispostaSdINotificaEsitoType createRispostaSdINotificaEsitoType() {
        return new RispostaSdINotificaEsitoType();
    }

    /**
     * Create an instance of {@link RispostaRiceviFattureType }
     * 
     */
    public RispostaRiceviFattureType createRispostaRiceviFattureType() {
        return new RispostaRiceviFattureType();
    }

    /**
     * Create an instance of {@link FileSdIConMetadatiType }
     * 
     */
    public FileSdIConMetadatiType createFileSdIConMetadatiType() {
        return new FileSdIConMetadatiType();
    }

    /**
     * Create an instance of {@link FileSdIType }
     * 
     */
    public FileSdIType createFileSdIType() {
        return new FileSdIType();
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
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name = "notificaEsito")
    public JAXBElement<FileSdIType> createNotificaEsito(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._NotificaEsito_QNAME, FileSdIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name = "fileSdI")
    public JAXBElement<FileSdIType> createFileSdI(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._FileSdI_QNAME, FileSdIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name = "notificaDecorrenzaTermini")
    public JAXBElement<FileSdIType> createNotificaDecorrenzaTermini(FileSdIType value) {
        return new JAXBElement<FileSdIType>(ObjectFactory._NotificaDecorrenzaTermini_QNAME, FileSdIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaRiceviFattureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name = "rispostaRiceviFatture")
    public JAXBElement<RispostaRiceviFattureType> createRispostaRiceviFatture(RispostaRiceviFattureType value) {
        return new JAXBElement<RispostaRiceviFattureType>(ObjectFactory._RispostaRiceviFatture_QNAME, RispostaRiceviFattureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaSdINotificaEsitoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name = "rispostaSdINotificaEsito")
    public JAXBElement<RispostaSdINotificaEsitoType> createRispostaSdINotificaEsito(RispostaSdINotificaEsitoType value) {
        return new JAXBElement<RispostaSdINotificaEsitoType>(ObjectFactory._RispostaSdINotificaEsito_QNAME, RispostaSdINotificaEsitoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIConMetadatiType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name = "fileSdIConMetadati")
    public JAXBElement<FileSdIConMetadatiType> createFileSdIConMetadati(FileSdIConMetadatiType value) {
        return new JAXBElement<FileSdIConMetadatiType>(ObjectFactory._FileSdIConMetadati_QNAME, FileSdIConMetadatiType.class, null, value);
    }

}
