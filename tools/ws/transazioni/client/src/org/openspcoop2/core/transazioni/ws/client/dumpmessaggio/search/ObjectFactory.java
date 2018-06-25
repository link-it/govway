/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.core.transazioni.ws.client.dumpmessaggio.search;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.transazioni.ws.client.dumpmessaggio.search package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SearchFilterDumpMessaggio_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "search-filter-dump-messaggio");
    private final static QName _TransazioniServiceException_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "transazioni-service-exception");
    private final static QName _TransazioniNotFoundException_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "transazioni-not-found-exception");
    private final static QName _TransazioniMultipleResultException_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "transazioni-multiple-result-exception");
    private final static QName _TransazioniNotImplementedException_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "transazioni-not-implemented-exception");
    private final static QName _TransazioniNotAuthorizedException_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "transazioni-not-authorized-exception");
    private final static QName _FindAll_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "findAll");
    private final static QName _FindAllResponse_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "findAllResponse");
    private final static QName _Find_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "find");
    private final static QName _FindResponse_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "findResponse");
    private final static QName _Count_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "count");
    private final static QName _CountResponse_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "countResponse");
    private final static QName _Get_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "get");
    private final static QName _GetResponse_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "getResponse");
    private final static QName _Exists_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "exists");
    private final static QName _ExistsResponse_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "existsResponse");
    private final static QName _FindAllIds_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "findAllIds");
    private final static QName _FindAllIdsResponse_QNAME = new QName("http://www.openspcoop2.org/core/transazioni/management", "findAllIdsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.transazioni.ws.client.dumpmessaggio.search
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SearchFilterDumpMessaggio }
     * 
     */
    public SearchFilterDumpMessaggio createSearchFilterDumpMessaggio() {
        return new SearchFilterDumpMessaggio();
    }

    /**
     * Create an instance of {@link TransazioniServiceException }
     * 
     */
    public TransazioniServiceException createTransazioniServiceException() {
        return new TransazioniServiceException();
    }

    /**
     * Create an instance of {@link TransazioniNotFoundException }
     * 
     */
    public TransazioniNotFoundException createTransazioniNotFoundException() {
        return new TransazioniNotFoundException();
    }

    /**
     * Create an instance of {@link TransazioniMultipleResultException }
     * 
     */
    public TransazioniMultipleResultException createTransazioniMultipleResultException() {
        return new TransazioniMultipleResultException();
    }

    /**
     * Create an instance of {@link TransazioniNotImplementedException }
     * 
     */
    public TransazioniNotImplementedException createTransazioniNotImplementedException() {
        return new TransazioniNotImplementedException();
    }

    /**
     * Create an instance of {@link TransazioniNotAuthorizedException }
     * 
     */
    public TransazioniNotAuthorizedException createTransazioniNotAuthorizedException() {
        return new TransazioniNotAuthorizedException();
    }

    /**
     * Create an instance of {@link FindAll }
     * 
     */
    public FindAll createFindAll() {
        return new FindAll();
    }

    /**
     * Create an instance of {@link FindAllResponse }
     * 
     */
    public FindAllResponse createFindAllResponse() {
        return new FindAllResponse();
    }

    /**
     * Create an instance of {@link Find }
     * 
     */
    public Find createFind() {
        return new Find();
    }

    /**
     * Create an instance of {@link FindResponse }
     * 
     */
    public FindResponse createFindResponse() {
        return new FindResponse();
    }

    /**
     * Create an instance of {@link Count }
     * 
     */
    public Count createCount() {
        return new Count();
    }

    /**
     * Create an instance of {@link CountResponse }
     * 
     */
    public CountResponse createCountResponse() {
        return new CountResponse();
    }

    /**
     * Create an instance of {@link Get }
     * 
     */
    public Get createGet() {
        return new Get();
    }

    /**
     * Create an instance of {@link GetResponse }
     * 
     */
    public GetResponse createGetResponse() {
        return new GetResponse();
    }

    /**
     * Create an instance of {@link Exists }
     * 
     */
    public Exists createExists() {
        return new Exists();
    }

    /**
     * Create an instance of {@link ExistsResponse }
     * 
     */
    public ExistsResponse createExistsResponse() {
        return new ExistsResponse();
    }

    /**
     * Create an instance of {@link FindAllIds }
     * 
     */
    public FindAllIds createFindAllIds() {
        return new FindAllIds();
    }

    /**
     * Create an instance of {@link FindAllIdsResponse }
     * 
     */
    public FindAllIdsResponse createFindAllIdsResponse() {
        return new FindAllIdsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchFilterDumpMessaggio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "search-filter-dump-messaggio")
    public JAXBElement<SearchFilterDumpMessaggio> createSearchFilterDumpMessaggio(SearchFilterDumpMessaggio value) {
        return new JAXBElement<SearchFilterDumpMessaggio>(ObjectFactory._SearchFilterDumpMessaggio_QNAME, SearchFilterDumpMessaggio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransazioniServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "transazioni-service-exception")
    public JAXBElement<TransazioniServiceException> createTransazioniServiceException(TransazioniServiceException value) {
        return new JAXBElement<TransazioniServiceException>(ObjectFactory._TransazioniServiceException_QNAME, TransazioniServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransazioniNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "transazioni-not-found-exception")
    public JAXBElement<TransazioniNotFoundException> createTransazioniNotFoundException(TransazioniNotFoundException value) {
        return new JAXBElement<TransazioniNotFoundException>(ObjectFactory._TransazioniNotFoundException_QNAME, TransazioniNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransazioniMultipleResultException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "transazioni-multiple-result-exception")
    public JAXBElement<TransazioniMultipleResultException> createTransazioniMultipleResultException(TransazioniMultipleResultException value) {
        return new JAXBElement<TransazioniMultipleResultException>(ObjectFactory._TransazioniMultipleResultException_QNAME, TransazioniMultipleResultException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransazioniNotImplementedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "transazioni-not-implemented-exception")
    public JAXBElement<TransazioniNotImplementedException> createTransazioniNotImplementedException(TransazioniNotImplementedException value) {
        return new JAXBElement<TransazioniNotImplementedException>(ObjectFactory._TransazioniNotImplementedException_QNAME, TransazioniNotImplementedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransazioniNotAuthorizedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "transazioni-not-authorized-exception")
    public JAXBElement<TransazioniNotAuthorizedException> createTransazioniNotAuthorizedException(TransazioniNotAuthorizedException value) {
        return new JAXBElement<TransazioniNotAuthorizedException>(ObjectFactory._TransazioniNotAuthorizedException_QNAME, TransazioniNotAuthorizedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "findAll")
    public JAXBElement<FindAll> createFindAll(FindAll value) {
        return new JAXBElement<FindAll>(ObjectFactory._FindAll_QNAME, FindAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "findAllResponse")
    public JAXBElement<FindAllResponse> createFindAllResponse(FindAllResponse value) {
        return new JAXBElement<FindAllResponse>(ObjectFactory._FindAllResponse_QNAME, FindAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Find }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "find")
    public JAXBElement<Find> createFind(Find value) {
        return new JAXBElement<Find>(ObjectFactory._Find_QNAME, Find.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "findResponse")
    public JAXBElement<FindResponse> createFindResponse(FindResponse value) {
        return new JAXBElement<FindResponse>(ObjectFactory._FindResponse_QNAME, FindResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Count }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "count")
    public JAXBElement<Count> createCount(Count value) {
        return new JAXBElement<Count>(ObjectFactory._Count_QNAME, Count.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "countResponse")
    public JAXBElement<CountResponse> createCountResponse(CountResponse value) {
        return new JAXBElement<CountResponse>(ObjectFactory._CountResponse_QNAME, CountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Get }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "get")
    public JAXBElement<Get> createGet(Get value) {
        return new JAXBElement<Get>(ObjectFactory._Get_QNAME, Get.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "getResponse")
    public JAXBElement<GetResponse> createGetResponse(GetResponse value) {
        return new JAXBElement<GetResponse>(ObjectFactory._GetResponse_QNAME, GetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exists }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "exists")
    public JAXBElement<Exists> createExists(Exists value) {
        return new JAXBElement<Exists>(ObjectFactory._Exists_QNAME, Exists.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExistsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "existsResponse")
    public JAXBElement<ExistsResponse> createExistsResponse(ExistsResponse value) {
        return new JAXBElement<ExistsResponse>(ObjectFactory._ExistsResponse_QNAME, ExistsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllIds }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "findAllIds")
    public JAXBElement<FindAllIds> createFindAllIds(FindAllIds value) {
        return new JAXBElement<FindAllIds>(ObjectFactory._FindAllIds_QNAME, FindAllIds.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllIdsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/transazioni/management", name = "findAllIdsResponse")
    public JAXBElement<FindAllIdsResponse> createFindAllIdsResponse(FindAllIdsResponse value) {
        return new JAXBElement<FindAllIdsResponse>(ObjectFactory._FindAllIdsResponse_QNAME, FindAllIdsResponse.class, null, value);
    }

}
