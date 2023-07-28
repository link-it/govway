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
package backend.ecodex.org._1_1;

import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the backend.ecodex.org._1_1 package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: backend.ecodex.org._1_1
     * 
     */
    public ObjectFactory() {
        // Create a new ObjectFactory
    }

    /**
     * Create an instance of {@link PayloadType }
     */
    public PayloadType createPayloadType() {
        return new PayloadType();
    }

    /**
     * Create an instance of {@link ErrorResultImplArray }
     */
    public ErrorResultImplArray createErrorResultImplArray() {
        return new ErrorResultImplArray();
    }

    /**
     * Create an instance of {@link RetrieveMessageResponse }
     */
    public RetrieveMessageResponse createRetrieveMessageResponse() {
        return new RetrieveMessageResponse();
    }

    /**
     * Create an instance of {@link MessageErrorsRequest }
     */
    public MessageErrorsRequest createMessageErrorsRequest() {
        return new MessageErrorsRequest();
    }

    /**
     * Create an instance of {@link ListPendingMessagesResponse }
     */
    public ListPendingMessagesResponse createListPendingMessagesResponse() {
        return new ListPendingMessagesResponse();
    }

    /**
     * Create an instance of {@link MessageStatusRequest }
     */
    public MessageStatusRequest createMessageStatusRequest() {
        return new MessageStatusRequest();
    }

    /**
     * Create an instance of {@link GetStatusRequest }
     */
    public GetStatusRequest createGetStatusRequest() {
        return new GetStatusRequest();
    }

    /**
     * Create an instance of {@link ErrorResultImpl }
     */
    public ErrorResultImpl createErrorResultImpl() {
        return new ErrorResultImpl();
    }

    /**
     * Create an instance of {@link PayloadURLType }
     */
    public PayloadURLType createPayloadURLType() {
        return new PayloadURLType();
    }

    /**
     * Create an instance of {@link SubmitResponse }
     */
    public SubmitResponse createSubmitResponse() {
        return new SubmitResponse();
    }

    /**
     * Create an instance of {@link GetErrorsRequest }
     */
    public GetErrorsRequest createGetErrorsRequest() {
        return new GetErrorsRequest();
    }

    /**
     * Create an instance of {@link FaultDetail }
     */
    public FaultDetail createFaultDetail() {
        return new FaultDetail();
    }

    /**
     * Create an instance of {@link SubmitRequest }
     */
    public SubmitRequest createSubmitRequest() {
        return new SubmitRequest();
    }

    /**
     * Create an instance of {@link RetrieveMessageRequest }
     */
    public RetrieveMessageRequest createRetrieveMessageRequest() {
        return new RetrieveMessageRequest();
    }

    /**
     * Create an instance of {@link Collection }
     */
    public Collection createCollection() {
        return new Collection();
    }

    /**
     * Create an instance of {@link LargePayloadType }
     */
    public LargePayloadType createLargePayloadType() {
        return new LargePayloadType();
    }

    /**
     * Create an instance of {@link StatusRequest }
     */
    public StatusRequest createStatusRequest() {
        return new StatusRequest();
    }

    private static final QName _GetMessageErrorsResponse = new QName("http://org.ecodex.backend/1_1/", "getMessageErrorsResponse");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorResultImplArray }{@code >}}
     */
    @XmlElementDecl(namespace = "http://org.ecodex.backend/1_1/", name="getMessageErrorsResponse")
    public JAXBElement<ErrorResultImplArray> createGetMessageErrorsResponse() {
        return new JAXBElement<ErrorResultImplArray>(_GetMessageErrorsResponse, ErrorResultImplArray.class, null, this.createErrorResultImplArray());
    }
    public JAXBElement<ErrorResultImplArray> createGetMessageErrorsResponse(ErrorResultImplArray getMessageErrorsResponse) {
        return new JAXBElement<ErrorResultImplArray>(_GetMessageErrorsResponse, ErrorResultImplArray.class, null, getMessageErrorsResponse);
    }


 }
