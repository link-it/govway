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
package it.gov.fatturapa.sdi.messaggi.v1_0;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.gov.fatturapa.sdi.messaggi.v1_0 package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.fatturapa.sdi.messaggi.v1_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NotificaDecorrenzaTerminiType }
     */
    public NotificaDecorrenzaTerminiType createNotificaDecorrenzaTerminiType() {
        return new NotificaDecorrenzaTerminiType();
    }

    /**
     * Create an instance of {@link ListaErroriType }
     */
    public ListaErroriType createListaErroriType() {
        return new ListaErroriType();
    }

    /**
     * Create an instance of {@link MetadatiInvioFileType }
     */
    public MetadatiInvioFileType createMetadatiInvioFileType() {
        return new MetadatiInvioFileType();
    }

    /**
     * Create an instance of {@link ErroreType }
     */
    public ErroreType createErroreType() {
        return new ErroreType();
    }

    /**
     * Create an instance of {@link ScartoEsitoCommittenteType }
     */
    public ScartoEsitoCommittenteType createScartoEsitoCommittenteType() {
        return new ScartoEsitoCommittenteType();
    }

    /**
     * Create an instance of {@link AttestazioneTrasmissioneFatturaType }
     */
    public AttestazioneTrasmissioneFatturaType createAttestazioneTrasmissioneFatturaType() {
        return new AttestazioneTrasmissioneFatturaType();
    }

    /**
     * Create an instance of {@link DestinatarioType }
     */
    public DestinatarioType createDestinatarioType() {
        return new DestinatarioType();
    }

    /**
     * Create an instance of {@link RiferimentoFatturaType }
     */
    public RiferimentoFatturaType createRiferimentoFatturaType() {
        return new RiferimentoFatturaType();
    }

    /**
     * Create an instance of {@link NotificaEsitoCommittenteType }
     */
    public NotificaEsitoCommittenteType createNotificaEsitoCommittenteType() {
        return new NotificaEsitoCommittenteType();
    }

    /**
     * Create an instance of {@link RiferimentoArchivioType }
     */
    public RiferimentoArchivioType createRiferimentoArchivioType() {
        return new RiferimentoArchivioType();
    }

    /**
     * Create an instance of {@link NotificaMancataConsegnaType }
     */
    public NotificaMancataConsegnaType createNotificaMancataConsegnaType() {
        return new NotificaMancataConsegnaType();
    }

    /**
     * Create an instance of {@link RicevutaConsegnaType }
     */
    public RicevutaConsegnaType createRicevutaConsegnaType() {
        return new RicevutaConsegnaType();
    }

    /**
     * Create an instance of {@link NotificaScartoType }
     */
    public NotificaScartoType createNotificaScartoType() {
        return new NotificaScartoType();
    }

    /**
     * Create an instance of {@link NotificaEsitoType }
     */
    public NotificaEsitoType createNotificaEsitoType() {
        return new NotificaEsitoType();
    }

    private final static QName _MetadatiInvioFile = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "MetadatiInvioFile");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetadatiInvioFileType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", name="MetadatiInvioFile")
    public JAXBElement<MetadatiInvioFileType> createMetadatiInvioFile() {
        return new JAXBElement<MetadatiInvioFileType>(_MetadatiInvioFile, MetadatiInvioFileType.class, null, this.createMetadatiInvioFileType());
    }
    public JAXBElement<MetadatiInvioFileType> createMetadatiInvioFile(MetadatiInvioFileType metadatiInvioFile) {
        return new JAXBElement<MetadatiInvioFileType>(_MetadatiInvioFile, MetadatiInvioFileType.class, null, metadatiInvioFile);
    }

    private final static QName _NotificaScarto = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "NotificaScarto");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificaScartoType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", name="NotificaScarto")
    public JAXBElement<NotificaScartoType> createNotificaScarto() {
        return new JAXBElement<NotificaScartoType>(_NotificaScarto, NotificaScartoType.class, null, this.createNotificaScartoType());
    }
    public JAXBElement<NotificaScartoType> createNotificaScarto(NotificaScartoType notificaScarto) {
        return new JAXBElement<NotificaScartoType>(_NotificaScarto, NotificaScartoType.class, null, notificaScarto);
    }

    private final static QName _ScartoEsitoCommittente = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "ScartoEsitoCommittente");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ScartoEsitoCommittenteType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", name="ScartoEsitoCommittente")
    public JAXBElement<ScartoEsitoCommittenteType> createScartoEsitoCommittente() {
        return new JAXBElement<ScartoEsitoCommittenteType>(_ScartoEsitoCommittente, ScartoEsitoCommittenteType.class, null, this.createScartoEsitoCommittenteType());
    }
    public JAXBElement<ScartoEsitoCommittenteType> createScartoEsitoCommittente(ScartoEsitoCommittenteType scartoEsitoCommittente) {
        return new JAXBElement<ScartoEsitoCommittenteType>(_ScartoEsitoCommittente, ScartoEsitoCommittenteType.class, null, scartoEsitoCommittente);
    }

    private final static QName _NotificaDecorrenzaTermini = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "NotificaDecorrenzaTermini");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificaDecorrenzaTerminiType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", name="NotificaDecorrenzaTermini")
    public JAXBElement<NotificaDecorrenzaTerminiType> createNotificaDecorrenzaTermini() {
        return new JAXBElement<NotificaDecorrenzaTerminiType>(_NotificaDecorrenzaTermini, NotificaDecorrenzaTerminiType.class, null, this.createNotificaDecorrenzaTerminiType());
    }
    public JAXBElement<NotificaDecorrenzaTerminiType> createNotificaDecorrenzaTermini(NotificaDecorrenzaTerminiType notificaDecorrenzaTermini) {
        return new JAXBElement<NotificaDecorrenzaTerminiType>(_NotificaDecorrenzaTermini, NotificaDecorrenzaTerminiType.class, null, notificaDecorrenzaTermini);
    }

    private final static QName _NotificaEsitoCommittente = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "NotificaEsitoCommittente");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificaEsitoCommittenteType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", name="NotificaEsitoCommittente")
    public JAXBElement<NotificaEsitoCommittenteType> createNotificaEsitoCommittente() {
        return new JAXBElement<NotificaEsitoCommittenteType>(_NotificaEsitoCommittente, NotificaEsitoCommittenteType.class, null, this.createNotificaEsitoCommittenteType());
    }
    public JAXBElement<NotificaEsitoCommittenteType> createNotificaEsitoCommittente(NotificaEsitoCommittenteType notificaEsitoCommittente) {
        return new JAXBElement<NotificaEsitoCommittenteType>(_NotificaEsitoCommittente, NotificaEsitoCommittenteType.class, null, notificaEsitoCommittente);
    }

    private final static QName _NotificaEsito = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "NotificaEsito");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificaEsitoType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", name="NotificaEsito")
    public JAXBElement<NotificaEsitoType> createNotificaEsito() {
        return new JAXBElement<NotificaEsitoType>(_NotificaEsito, NotificaEsitoType.class, null, this.createNotificaEsitoType());
    }
    public JAXBElement<NotificaEsitoType> createNotificaEsito(NotificaEsitoType notificaEsito) {
        return new JAXBElement<NotificaEsitoType>(_NotificaEsito, NotificaEsitoType.class, null, notificaEsito);
    }

    private final static QName _RicevutaConsegna = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "RicevutaConsegna");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RicevutaConsegnaType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", name="RicevutaConsegna")
    public JAXBElement<RicevutaConsegnaType> createRicevutaConsegna() {
        return new JAXBElement<RicevutaConsegnaType>(_RicevutaConsegna, RicevutaConsegnaType.class, null, this.createRicevutaConsegnaType());
    }
    public JAXBElement<RicevutaConsegnaType> createRicevutaConsegna(RicevutaConsegnaType ricevutaConsegna) {
        return new JAXBElement<RicevutaConsegnaType>(_RicevutaConsegna, RicevutaConsegnaType.class, null, ricevutaConsegna);
    }

    private final static QName _AttestazioneTrasmissioneFattura = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "AttestazioneTrasmissioneFattura");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttestazioneTrasmissioneFatturaType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", name="AttestazioneTrasmissioneFattura")
    public JAXBElement<AttestazioneTrasmissioneFatturaType> createAttestazioneTrasmissioneFattura() {
        return new JAXBElement<AttestazioneTrasmissioneFatturaType>(_AttestazioneTrasmissioneFattura, AttestazioneTrasmissioneFatturaType.class, null, this.createAttestazioneTrasmissioneFatturaType());
    }
    public JAXBElement<AttestazioneTrasmissioneFatturaType> createAttestazioneTrasmissioneFattura(AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFattura) {
        return new JAXBElement<AttestazioneTrasmissioneFatturaType>(_AttestazioneTrasmissioneFattura, AttestazioneTrasmissioneFatturaType.class, null, attestazioneTrasmissioneFattura);
    }

    private final static QName _NotificaMancataConsegna = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "NotificaMancataConsegna");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificaMancataConsegnaType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", name="NotificaMancataConsegna")
    public JAXBElement<NotificaMancataConsegnaType> createNotificaMancataConsegna() {
        return new JAXBElement<NotificaMancataConsegnaType>(_NotificaMancataConsegna, NotificaMancataConsegnaType.class, null, this.createNotificaMancataConsegnaType());
    }
    public JAXBElement<NotificaMancataConsegnaType> createNotificaMancataConsegna(NotificaMancataConsegnaType notificaMancataConsegna) {
        return new JAXBElement<NotificaMancataConsegnaType>(_NotificaMancataConsegna, NotificaMancataConsegnaType.class, null, notificaMancataConsegna);
    }


 }
