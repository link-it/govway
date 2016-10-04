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

package org.openspcoop2.example.pdd.server.stampadocumento;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.example.pdd.server.stampadocumento package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Esito_QNAME = new QName("http://openspcoop2.org/example/pdd/server/StampaDocumento", "esito");
    private final static QName _StatoDocumento_QNAME = new QName("http://openspcoop2.org/example/pdd/server/StampaDocumento", "statoDocumento");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.example.pdd.server.stampadocumento
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RichiestaDocumento }
     * 
     */
    public RichiestaDocumento createRichiestaDocumento() {
        return new RichiestaDocumento();
    }

    /**
     * Create an instance of {@link PresaConsegnaStampa }
     * 
     */
    public PresaConsegnaStampa createPresaConsegnaStampa() {
        return new PresaConsegnaStampa();
    }

    /**
     * Create an instance of {@link StampaDocumento_Type }
     * 
     */
    public StampaDocumento_Type createStampaDocumento_Type() {
        return new StampaDocumento_Type();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://openspcoop2.org/example/pdd/server/StampaDocumento", name = "esito")
    public JAXBElement<String> createEsito(String value) {
        return new JAXBElement<String>(_Esito_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://openspcoop2.org/example/pdd/server/StampaDocumento", name = "statoDocumento")
    public JAXBElement<Long> createStatoDocumento(Long value) {
        return new JAXBElement<Long>(_StatoDocumento_QNAME, Long.class, null, value);
    }

}
