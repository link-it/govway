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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0 package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0
     * 
     */
    public ObjectFactory() {
        // Create a new ObjectFactory
    }

    /**
     * Create an instance of {@link RappresentanteFiscaleType }
     */
    public RappresentanteFiscaleType createRappresentanteFiscaleType() {
        return new RappresentanteFiscaleType();
    }

    /**
     * Create an instance of {@link DatiGeneraliDocumentoType }
     */
    public DatiGeneraliDocumentoType createDatiGeneraliDocumentoType() {
        return new DatiGeneraliDocumentoType();
    }

    /**
     * Create an instance of {@link DatiGeneraliType }
     */
    public DatiGeneraliType createDatiGeneraliType() {
        return new DatiGeneraliType();
    }

    /**
     * Create an instance of {@link IscrizioneREAType }
     */
    public IscrizioneREAType createIscrizioneREAType() {
        return new IscrizioneREAType();
    }

    /**
     * Create an instance of {@link CessionarioCommittenteType }
     */
    public CessionarioCommittenteType createCessionarioCommittenteType() {
        return new CessionarioCommittenteType();
    }

    /**
     * Create an instance of {@link IdFiscaleType }
     */
    public IdFiscaleType createIdFiscaleType() {
        return new IdFiscaleType();
    }

    /**
     * Create an instance of {@link DatiTrasmissioneType }
     */
    public DatiTrasmissioneType createDatiTrasmissioneType() {
        return new DatiTrasmissioneType();
    }

    /**
     * Create an instance of {@link DatiFatturaRettificataType }
     */
    public DatiFatturaRettificataType createDatiFatturaRettificataType() {
        return new DatiFatturaRettificataType();
    }

    /**
     * Create an instance of {@link FatturaElettronicaType }
     */
    public FatturaElettronicaType createFatturaElettronicaType() {
        return new FatturaElettronicaType();
    }

    /**
     * Create an instance of {@link AllegatiType }
     */
    public AllegatiType createAllegatiType() {
        return new AllegatiType();
    }

    /**
     * Create an instance of {@link IndirizzoType }
     */
    public IndirizzoType createIndirizzoType() {
        return new IndirizzoType();
    }

    /**
     * Create an instance of {@link AltriDatiIdentificativiType }
     */
    public AltriDatiIdentificativiType createAltriDatiIdentificativiType() {
        return new AltriDatiIdentificativiType();
    }

    /**
     * Create an instance of {@link IdentificativiFiscaliType }
     */
    public IdentificativiFiscaliType createIdentificativiFiscaliType() {
        return new IdentificativiFiscaliType();
    }

    /**
     * Create an instance of {@link FatturaElettronicaHeaderType }
     */
    public FatturaElettronicaHeaderType createFatturaElettronicaHeaderType() {
        return new FatturaElettronicaHeaderType();
    }

    /**
     * Create an instance of {@link DatiBeniServiziType }
     */
    public DatiBeniServiziType createDatiBeniServiziType() {
        return new DatiBeniServiziType();
    }

    /**
     * Create an instance of {@link DatiIVAType }
     */
    public DatiIVAType createDatiIVAType() {
        return new DatiIVAType();
    }

    /**
     * Create an instance of {@link CedentePrestatoreType }
     */
    public CedentePrestatoreType createCedentePrestatoreType() {
        return new CedentePrestatoreType();
    }

    /**
     * Create an instance of {@link FatturaElettronicaBodyType }
     */
    public FatturaElettronicaBodyType createFatturaElettronicaBodyType() {
        return new FatturaElettronicaBodyType();
    }

    private static final QName _FatturaElettronicaSemplificata = new QName("http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0", "FatturaElettronicaSemplificata");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FatturaElettronicaType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0", name="FatturaElettronicaSemplificata")
    public JAXBElement<FatturaElettronicaType> createFatturaElettronicaSemplificata() {
        return new JAXBElement<FatturaElettronicaType>(_FatturaElettronicaSemplificata, FatturaElettronicaType.class, null, this.createFatturaElettronicaType());
    }
    public JAXBElement<FatturaElettronicaType> createFatturaElettronicaSemplificata(FatturaElettronicaType fatturaElettronicaSemplificata) {
        return new JAXBElement<FatturaElettronicaType>(_FatturaElettronicaSemplificata, FatturaElettronicaType.class, null, fatturaElettronicaSemplificata);
    }


 }
