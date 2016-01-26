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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.protocol.manifest package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.protocol.manifest
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Soggetti }
     */
    public Soggetti createSoggetti() {
        return new Soggetti();
    }

    /**
     * Create an instance of {@link Profilo }
     */
    public Profilo createProfilo() {
        return new Profilo();
    }

    /**
     * Create an instance of {@link SoapHeaderBypassMustUnderstandHeader }
     */
    public SoapHeaderBypassMustUnderstandHeader createSoapHeaderBypassMustUnderstandHeader() {
        return new SoapHeaderBypassMustUnderstandHeader();
    }

    /**
     * Create an instance of {@link Binding }
     */
    public Binding createBinding() {
        return new Binding();
    }

    /**
     * Create an instance of {@link RegistroServizi }
     */
    public RegistroServizi createRegistroServizi() {
        return new RegistroServizi();
    }

    /**
     * Create an instance of {@link Servizi }
     */
    public Servizi createServizi() {
        return new Servizi();
    }

    /**
     * Create an instance of {@link Tipi }
     */
    public Tipi createTipi() {
        return new Tipi();
    }

    /**
     * Create an instance of {@link UrlMapping }
     */
    public UrlMapping createUrlMapping() {
        return new UrlMapping();
    }

    /**
     * Create an instance of {@link Web }
     */
    public Web createWeb() {
        return new Web();
    }

    /**
     * Create an instance of {@link Openspcoop2 }
     */
    public Openspcoop2 createOpenspcoop2() {
        return new Openspcoop2();
    }

    /**
     * Create an instance of {@link Versioni }
     */
    public Versioni createVersioni() {
        return new Versioni();
    }

    /**
     * Create an instance of {@link Funzionalita }
     */
    public Funzionalita createFunzionalita() {
        return new Funzionalita();
    }

    /**
     * Create an instance of {@link WebEmptyContext }
     */
    public WebEmptyContext createWebEmptyContext() {
        return new WebEmptyContext();
    }

    /**
     * Create an instance of {@link SoapHeaderBypassMustUnderstand }
     */
    public SoapHeaderBypassMustUnderstand createSoapHeaderBypassMustUnderstand() {
        return new SoapHeaderBypassMustUnderstand();
    }


 }
