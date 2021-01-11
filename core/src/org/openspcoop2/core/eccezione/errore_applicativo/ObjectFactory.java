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
package org.openspcoop2.core.eccezione.errore_applicativo;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.eccezione.errore_applicativo package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.eccezione.errore_applicativo
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DominioSoggetto }
     */
    public DominioSoggetto createDominioSoggetto() {
        return new DominioSoggetto();
    }

    /**
     * Create an instance of {@link Servizio }
     */
    public Servizio createServizio() {
        return new Servizio();
    }

    /**
     * Create an instance of {@link Dominio }
     */
    public Dominio createDominio() {
        return new Dominio();
    }

    /**
     * Create an instance of {@link Eccezione }
     */
    public Eccezione createEccezione() {
        return new Eccezione();
    }

    /**
     * Create an instance of {@link DatiCooperazione }
     */
    public DatiCooperazione createDatiCooperazione() {
        return new DatiCooperazione();
    }

    /**
     * Create an instance of {@link CodiceEccezione }
     */
    public CodiceEccezione createCodiceEccezione() {
        return new CodiceEccezione();
    }

    /**
     * Create an instance of {@link SoggettoIdentificativo }
     */
    public SoggettoIdentificativo createSoggettoIdentificativo() {
        return new SoggettoIdentificativo();
    }

    /**
     * Create an instance of {@link Soggetto }
     */
    public Soggetto createSoggetto() {
        return new Soggetto();
    }

    /**
     * Create an instance of {@link ErroreApplicativo }
     */
    public ErroreApplicativo createErroreApplicativo() {
        return new ErroreApplicativo();
    }

    private final static QName _Fault = new QName("http://govway.org/integration/fault", "fault");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErroreApplicativo }{@code >}}
     */
    @XmlElementDecl(namespace = "http://govway.org/integration/fault", name="fault")
    public JAXBElement<ErroreApplicativo> createFault() {
        return new JAXBElement<ErroreApplicativo>(_Fault, ErroreApplicativo.class, null, this.createErroreApplicativo());
    }
    public JAXBElement<ErroreApplicativo> createFault(ErroreApplicativo fault) {
        return new JAXBElement<ErroreApplicativo>(_Fault, ErroreApplicativo.class, null, fault);
    }


 }
