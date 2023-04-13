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
package it.gov.fatturapa.sdi.ws.ricezione.v1_0.types;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.gov.fatturapa.sdi.ws.ricezione.v1_0.types package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.fatturapa.sdi.ws.ricezione.v1_0.types
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FileSdIType }
     */
    public FileSdIType createFileSdIType() {
        return new FileSdIType();
    }

    /**
     * Create an instance of {@link FileSdIConMetadatiType }
     */
    public FileSdIConMetadatiType createFileSdIConMetadatiType() {
        return new FileSdIConMetadatiType();
    }

    /**
     * Create an instance of {@link FileSdIBaseType }
     */
    public FileSdIBaseType createFileSdIBaseType() {
        return new FileSdIBaseType();
    }

    /**
     * Create an instance of {@link RispostaSdINotificaEsitoType }
     */
    public RispostaSdINotificaEsitoType createRispostaSdINotificaEsitoType() {
        return new RispostaSdINotificaEsitoType();
    }

    /**
     * Create an instance of {@link RispostaRiceviFattureType }
     */
    public RispostaRiceviFattureType createRispostaRiceviFattureType() {
        return new RispostaRiceviFattureType();
    }

    private static final QName _NotificaEsito = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "notificaEsito");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name="notificaEsito")
    public JAXBElement<FileSdIType> createNotificaEsito() {
        return new JAXBElement<FileSdIType>(_NotificaEsito, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createNotificaEsito(FileSdIType notificaEsito) {
        return new JAXBElement<FileSdIType>(_NotificaEsito, FileSdIType.class, null, notificaEsito);
    }

    private static final QName _FileSdIConMetadati = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "fileSdIConMetadati");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIConMetadatiType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name="fileSdIConMetadati")
    public JAXBElement<FileSdIConMetadatiType> createFileSdIConMetadati() {
        return new JAXBElement<FileSdIConMetadatiType>(_FileSdIConMetadati, FileSdIConMetadatiType.class, null, this.createFileSdIConMetadatiType());
    }
    public JAXBElement<FileSdIConMetadatiType> createFileSdIConMetadati(FileSdIConMetadatiType fileSdIConMetadati) {
        return new JAXBElement<FileSdIConMetadatiType>(_FileSdIConMetadati, FileSdIConMetadatiType.class, null, fileSdIConMetadati);
    }

    private static final QName _RispostaRiceviFatture = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "rispostaRiceviFatture");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaRiceviFattureType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name="rispostaRiceviFatture")
    public JAXBElement<RispostaRiceviFattureType> createRispostaRiceviFatture() {
        return new JAXBElement<RispostaRiceviFattureType>(_RispostaRiceviFatture, RispostaRiceviFattureType.class, null, this.createRispostaRiceviFattureType());
    }
    public JAXBElement<RispostaRiceviFattureType> createRispostaRiceviFatture(RispostaRiceviFattureType rispostaRiceviFatture) {
        return new JAXBElement<RispostaRiceviFattureType>(_RispostaRiceviFatture, RispostaRiceviFattureType.class, null, rispostaRiceviFatture);
    }

    private static final QName _FileSdI = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "fileSdI");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name="fileSdI")
    public JAXBElement<FileSdIType> createFileSdI() {
        return new JAXBElement<FileSdIType>(_FileSdI, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createFileSdI(FileSdIType fileSdI) {
        return new JAXBElement<FileSdIType>(_FileSdI, FileSdIType.class, null, fileSdI);
    }

    private static final QName _RispostaSdINotificaEsito = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "rispostaSdINotificaEsito");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaSdINotificaEsitoType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name="rispostaSdINotificaEsito")
    public JAXBElement<RispostaSdINotificaEsitoType> createRispostaSdINotificaEsito() {
        return new JAXBElement<RispostaSdINotificaEsitoType>(_RispostaSdINotificaEsito, RispostaSdINotificaEsitoType.class, null, this.createRispostaSdINotificaEsitoType());
    }
    public JAXBElement<RispostaSdINotificaEsitoType> createRispostaSdINotificaEsito(RispostaSdINotificaEsitoType rispostaSdINotificaEsito) {
        return new JAXBElement<RispostaSdINotificaEsitoType>(_RispostaSdINotificaEsito, RispostaSdINotificaEsitoType.class, null, rispostaSdINotificaEsito);
    }

    private static final QName _NotificaDecorrenzaTermini = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", "notificaDecorrenzaTermini");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types", name="notificaDecorrenzaTermini")
    public JAXBElement<FileSdIType> createNotificaDecorrenzaTermini() {
        return new JAXBElement<FileSdIType>(_NotificaDecorrenzaTermini, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createNotificaDecorrenzaTermini(FileSdIType notificaDecorrenzaTermini) {
        return new JAXBElement<FileSdIType>(_NotificaDecorrenzaTermini, FileSdIType.class, null, notificaDecorrenzaTermini);
    }


 }
