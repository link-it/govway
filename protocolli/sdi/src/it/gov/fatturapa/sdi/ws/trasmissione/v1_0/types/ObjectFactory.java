/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types
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
     * Create an instance of {@link FileSdIBaseType }
     */
    public FileSdIBaseType createFileSdIBaseType() {
        return new FileSdIBaseType();
    }

    /**
     * Create an instance of {@link RispostaSdIRiceviFileType }
     */
    public RispostaSdIRiceviFileType createRispostaSdIRiceviFileType() {
        return new RispostaSdIRiceviFileType();
    }

    private final static QName _NotificaMancataConsegna = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaMancataConsegna");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name="notificaMancataConsegna")
    public JAXBElement<FileSdIType> createNotificaMancataConsegna() {
        return new JAXBElement<FileSdIType>(_NotificaMancataConsegna, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createNotificaMancataConsegna(FileSdIType notificaMancataConsegna) {
        return new JAXBElement<FileSdIType>(_NotificaMancataConsegna, FileSdIType.class, null, notificaMancataConsegna);
    }

    private final static QName _NotificaEsito = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaEsito");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name="notificaEsito")
    public JAXBElement<FileSdIType> createNotificaEsito() {
        return new JAXBElement<FileSdIType>(_NotificaEsito, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createNotificaEsito(FileSdIType notificaEsito) {
        return new JAXBElement<FileSdIType>(_NotificaEsito, FileSdIType.class, null, notificaEsito);
    }

    private final static QName _NotificaFileNonRecapitabile = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaFileNonRecapitabile");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name="notificaFileNonRecapitabile")
    public JAXBElement<FileSdIType> createNotificaFileNonRecapitabile() {
        return new JAXBElement<FileSdIType>(_NotificaFileNonRecapitabile, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createNotificaFileNonRecapitabile(FileSdIType notificaFileNonRecapitabile) {
        return new JAXBElement<FileSdIType>(_NotificaFileNonRecapitabile, FileSdIType.class, null, notificaFileNonRecapitabile);
    }

    private final static QName _RicevutaConsegna = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "ricevutaConsegna");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name="ricevutaConsegna")
    public JAXBElement<FileSdIType> createRicevutaConsegna() {
        return new JAXBElement<FileSdIType>(_RicevutaConsegna, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createRicevutaConsegna(FileSdIType ricevutaConsegna) {
        return new JAXBElement<FileSdIType>(_RicevutaConsegna, FileSdIType.class, null, ricevutaConsegna);
    }

    private final static QName _RispostaSdIRiceviFile = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "rispostaSdIRiceviFile");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaSdIRiceviFileType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name="rispostaSdIRiceviFile")
    public JAXBElement<RispostaSdIRiceviFileType> createRispostaSdIRiceviFile() {
        return new JAXBElement<RispostaSdIRiceviFileType>(_RispostaSdIRiceviFile, RispostaSdIRiceviFileType.class, null, this.createRispostaSdIRiceviFileType());
    }
    public JAXBElement<RispostaSdIRiceviFileType> createRispostaSdIRiceviFile(RispostaSdIRiceviFileType rispostaSdIRiceviFile) {
        return new JAXBElement<RispostaSdIRiceviFileType>(_RispostaSdIRiceviFile, RispostaSdIRiceviFileType.class, null, rispostaSdIRiceviFile);
    }

    private final static QName _FileSdIAccoglienza = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "fileSdIAccoglienza");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIBaseType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name="fileSdIAccoglienza")
    public JAXBElement<FileSdIBaseType> createFileSdIAccoglienza() {
        return new JAXBElement<FileSdIBaseType>(_FileSdIAccoglienza, FileSdIBaseType.class, null, this.createFileSdIBaseType());
    }
    public JAXBElement<FileSdIBaseType> createFileSdIAccoglienza(FileSdIBaseType fileSdIAccoglienza) {
        return new JAXBElement<FileSdIBaseType>(_FileSdIAccoglienza, FileSdIBaseType.class, null, fileSdIAccoglienza);
    }

    private final static QName _AttestazioneTrasmissioneFattura = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "attestazioneTrasmissioneFattura");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name="attestazioneTrasmissioneFattura")
    public JAXBElement<FileSdIType> createAttestazioneTrasmissioneFattura() {
        return new JAXBElement<FileSdIType>(_AttestazioneTrasmissioneFattura, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createAttestazioneTrasmissioneFattura(FileSdIType attestazioneTrasmissioneFattura) {
        return new JAXBElement<FileSdIType>(_AttestazioneTrasmissioneFattura, FileSdIType.class, null, attestazioneTrasmissioneFattura);
    }

    private final static QName _FileSdI = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "fileSdI");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name="fileSdI")
    public JAXBElement<FileSdIType> createFileSdI() {
        return new JAXBElement<FileSdIType>(_FileSdI, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createFileSdI(FileSdIType fileSdI) {
        return new JAXBElement<FileSdIType>(_FileSdI, FileSdIType.class, null, fileSdI);
    }

    private final static QName _NotificaScarto = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaScarto");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name="notificaScarto")
    public JAXBElement<FileSdIType> createNotificaScarto() {
        return new JAXBElement<FileSdIType>(_NotificaScarto, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createNotificaScarto(FileSdIType notificaScarto) {
        return new JAXBElement<FileSdIType>(_NotificaScarto, FileSdIType.class, null, notificaScarto);
    }

    private final static QName _NotificaDecorrenzaTermini = new QName("http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", "notificaDecorrenzaTermini");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSdIType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types", name="notificaDecorrenzaTermini")
    public JAXBElement<FileSdIType> createNotificaDecorrenzaTermini() {
        return new JAXBElement<FileSdIType>(_NotificaDecorrenzaTermini, FileSdIType.class, null, this.createFileSdIType());
    }
    public JAXBElement<FileSdIType> createNotificaDecorrenzaTermini(FileSdIType notificaDecorrenzaTermini) {
        return new JAXBElement<FileSdIType>(_NotificaDecorrenzaTermini, FileSdIType.class, null, notificaDecorrenzaTermini);
    }


 }
