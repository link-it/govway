/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.message.context;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.message.context package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.message.context
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ContentLength }
     */
    public ContentLength createContentLength() {
        return new ContentLength();
    }

    /**
     * Create an instance of {@link SerializedParameter }
     */
    public SerializedParameter createSerializedParameter() {
        return new SerializedParameter();
    }

    /**
     * Create an instance of {@link Soap }
     */
    public Soap createSoap() {
        return new Soap();
    }

    /**
     * Create an instance of {@link TransportRequestContext }
     */
    public TransportRequestContext createTransportRequestContext() {
        return new TransportRequestContext();
    }

    /**
     * Create an instance of {@link MessageContext }
     */
    public MessageContext createMessageContext() {
        return new MessageContext();
    }

    /**
     * Create an instance of {@link ForcedResponseMessage }
     */
    public ForcedResponseMessage createForcedResponseMessage() {
        return new ForcedResponseMessage();
    }

    /**
     * Create an instance of {@link SerializedContext }
     */
    public SerializedContext createSerializedContext() {
        return new SerializedContext();
    }

    /**
     * Create an instance of {@link HeaderParameters }
     */
    public HeaderParameters createHeaderParameters() {
        return new HeaderParameters();
    }

    /**
     * Create an instance of {@link TransportResponseContext }
     */
    public TransportResponseContext createTransportResponseContext() {
        return new TransportResponseContext();
    }

    /**
     * Create an instance of {@link ContentTypeParameters }
     */
    public ContentTypeParameters createContentTypeParameters() {
        return new ContentTypeParameters();
    }

    /**
     * Create an instance of {@link ForcedResponse }
     */
    public ForcedResponse createForcedResponse() {
        return new ForcedResponse();
    }

    /**
     * Create an instance of {@link StringParameter }
     */
    public StringParameter createStringParameter() {
        return new StringParameter();
    }

    /**
     * Create an instance of {@link Credentials }
     */
    public Credentials createCredentials() {
        return new Credentials();
    }

    /**
     * Create an instance of {@link UrlParameters }
     */
    public UrlParameters createUrlParameters() {
        return new UrlParameters();
    }


 }
