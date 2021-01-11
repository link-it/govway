/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0 package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FileMetadatiType }
     */
    public FileMetadatiType createFileMetadatiType() {
        return new FileMetadatiType();
    }

    /**
     * Create an instance of {@link RicevutaConsegnaType }
     */
    public RicevutaConsegnaType createRicevutaConsegnaType() {
        return new RicevutaConsegnaType();
    }

    /**
     * Create an instance of {@link RiferimentoFatturaType }
     */
    public RiferimentoFatturaType createRiferimentoFatturaType() {
        return new RiferimentoFatturaType();
    }

    /**
     * Create an instance of {@link ListaErroriType }
     */
    public ListaErroriType createListaErroriType() {
        return new ListaErroriType();
    }

    /**
     * Create an instance of {@link RicevutaScartoType }
     */
    public RicevutaScartoType createRicevutaScartoType() {
        return new RicevutaScartoType();
    }

    /**
     * Create an instance of {@link RiferimentoArchivioType }
     */
    public RiferimentoArchivioType createRiferimentoArchivioType() {
        return new RiferimentoArchivioType();
    }

    /**
     * Create an instance of {@link DestinatarioType }
     */
    public DestinatarioType createDestinatarioType() {
        return new DestinatarioType();
    }

    /**
     * Create an instance of {@link RicevutaImpossibilitaRecapitoType }
     */
    public RicevutaImpossibilitaRecapitoType createRicevutaImpossibilitaRecapitoType() {
        return new RicevutaImpossibilitaRecapitoType();
    }

    /**
     * Create an instance of {@link ErroreType }
     */
    public ErroreType createErroreType() {
        return new ErroreType();
    }

    private final static QName _RicevutaConsegna = new QName("http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0", "RicevutaConsegna");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RicevutaConsegnaType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0", name="RicevutaConsegna")
    public JAXBElement<RicevutaConsegnaType> createRicevutaConsegna() {
        return new JAXBElement<RicevutaConsegnaType>(_RicevutaConsegna, RicevutaConsegnaType.class, null, this.createRicevutaConsegnaType());
    }
    public JAXBElement<RicevutaConsegnaType> createRicevutaConsegna(RicevutaConsegnaType ricevutaConsegna) {
        return new JAXBElement<RicevutaConsegnaType>(_RicevutaConsegna, RicevutaConsegnaType.class, null, ricevutaConsegna);
    }

    private final static QName _RicevutaImpossibilitaRecapito = new QName("http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0", "RicevutaImpossibilitaRecapito");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RicevutaImpossibilitaRecapitoType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0", name="RicevutaImpossibilitaRecapito")
    public JAXBElement<RicevutaImpossibilitaRecapitoType> createRicevutaImpossibilitaRecapito() {
        return new JAXBElement<RicevutaImpossibilitaRecapitoType>(_RicevutaImpossibilitaRecapito, RicevutaImpossibilitaRecapitoType.class, null, this.createRicevutaImpossibilitaRecapitoType());
    }
    public JAXBElement<RicevutaImpossibilitaRecapitoType> createRicevutaImpossibilitaRecapito(RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapito) {
        return new JAXBElement<RicevutaImpossibilitaRecapitoType>(_RicevutaImpossibilitaRecapito, RicevutaImpossibilitaRecapitoType.class, null, ricevutaImpossibilitaRecapito);
    }

    private final static QName _RicevutaScarto = new QName("http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0", "RicevutaScarto");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RicevutaScartoType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0", name="RicevutaScarto")
    public JAXBElement<RicevutaScartoType> createRicevutaScarto() {
        return new JAXBElement<RicevutaScartoType>(_RicevutaScarto, RicevutaScartoType.class, null, this.createRicevutaScartoType());
    }
    public JAXBElement<RicevutaScartoType> createRicevutaScarto(RicevutaScartoType ricevutaScarto) {
        return new JAXBElement<RicevutaScartoType>(_RicevutaScarto, RicevutaScartoType.class, null, ricevutaScarto);
    }

    private final static QName _FileMetadati = new QName("http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0", "FileMetadati");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileMetadatiType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0", name="FileMetadati")
    public JAXBElement<FileMetadatiType> createFileMetadati() {
        return new JAXBElement<FileMetadatiType>(_FileMetadati, FileMetadatiType.class, null, this.createFileMetadatiType());
    }
    public JAXBElement<FileMetadatiType> createFileMetadati(FileMetadatiType fileMetadati) {
        return new JAXBElement<FileMetadatiType>(_FileMetadati, FileMetadatiType.class, null, fileMetadati);
    }


 }
