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

package org.openspcoop2.pdd.monitor.ws.client.statopdd.all;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.pdd.monitor.ws.client.statopdd.all package. 
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

    private final static QName _FindAllResponse_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "findAllResponse");
    private final static QName _MonitorServiceException_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "monitor-service-exception");
    private final static QName _Find_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "find");
    private final static QName _BustaSoggetto_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "busta-soggetto");
    private final static QName _DeleteAllResponse_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "deleteAllResponse");
    private final static QName _FindResponse_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "findResponse");
    private final static QName _CreateResponse_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "createResponse");
    private final static QName _MonitorNotFoundException_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "monitor-not-found-exception");
    private final static QName _DeleteAllByFilterResponse_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "deleteAllByFilterResponse");
    private final static QName _DeleteAll_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "deleteAll");
    private final static QName _MonitorMultipleResultException_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "monitor-multiple-result-exception");
    private final static QName _BustaServizio_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "busta-servizio");
    private final static QName _Busta_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "busta");
    private final static QName _MonitorNotImplementedException_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "monitor-not-implemented-exception");
    private final static QName _Create_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "create");
    private final static QName _SearchFilterStatoPdd_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "search-filter-stato-pdd");
    private final static QName _MonitorNotAuthorizedException_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "monitor-not-authorized-exception");
    private final static QName _Filtro_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "filtro");
    private final static QName _Count_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "count");
    private final static QName _FindAll_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "findAll");
    private final static QName _DeleteAllByFilter_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "deleteAllByFilter");
    private final static QName _CountResponse_QNAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "countResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.pdd.monitor.ws.client.statopdd.all
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CountResponse }
     * 
     */
    public CountResponse createCountResponse() {
        return new CountResponse();
    }

    /**
     * Create an instance of {@link DeleteAllByFilter }
     * 
     */
    public DeleteAllByFilter createDeleteAllByFilter() {
        return new DeleteAllByFilter();
    }

    /**
     * Create an instance of {@link Count }
     * 
     */
    public Count createCount() {
        return new Count();
    }

    /**
     * Create an instance of {@link Filtro }
     * 
     */
    public Filtro createFiltro() {
        return new Filtro();
    }

    /**
     * Create an instance of {@link FindAll }
     * 
     */
    public FindAll createFindAll() {
        return new FindAll();
    }

    /**
     * Create an instance of {@link Create }
     * 
     */
    public Create createCreate() {
        return new Create();
    }

    /**
     * Create an instance of {@link SearchFilterStatoPdd }
     * 
     */
    public SearchFilterStatoPdd createSearchFilterStatoPdd() {
        return new SearchFilterStatoPdd();
    }

    /**
     * Create an instance of {@link MonitorNotAuthorizedException }
     * 
     */
    public MonitorNotAuthorizedException createMonitorNotAuthorizedException() {
        return new MonitorNotAuthorizedException();
    }

    /**
     * Create an instance of {@link Busta }
     * 
     */
    public Busta createBusta() {
        return new Busta();
    }

    /**
     * Create an instance of {@link MonitorNotImplementedException }
     * 
     */
    public MonitorNotImplementedException createMonitorNotImplementedException() {
        return new MonitorNotImplementedException();
    }

    /**
     * Create an instance of {@link DeleteAll }
     * 
     */
    public DeleteAll createDeleteAll() {
        return new DeleteAll();
    }

    /**
     * Create an instance of {@link BustaServizio }
     * 
     */
    public BustaServizio createBustaServizio() {
        return new BustaServizio();
    }

    /**
     * Create an instance of {@link MonitorMultipleResultException }
     * 
     */
    public MonitorMultipleResultException createMonitorMultipleResultException() {
        return new MonitorMultipleResultException();
    }

    /**
     * Create an instance of {@link CreateResponse }
     * 
     */
    public CreateResponse createCreateResponse() {
        return new CreateResponse();
    }

    /**
     * Create an instance of {@link FindResponse }
     * 
     */
    public FindResponse createFindResponse() {
        return new FindResponse();
    }

    /**
     * Create an instance of {@link DeleteAllResponse }
     * 
     */
    public DeleteAllResponse createDeleteAllResponse() {
        return new DeleteAllResponse();
    }

    /**
     * Create an instance of {@link DeleteAllByFilterResponse }
     * 
     */
    public DeleteAllByFilterResponse createDeleteAllByFilterResponse() {
        return new DeleteAllByFilterResponse();
    }

    /**
     * Create an instance of {@link MonitorNotFoundException }
     * 
     */
    public MonitorNotFoundException createMonitorNotFoundException() {
        return new MonitorNotFoundException();
    }

    /**
     * Create an instance of {@link BustaSoggetto }
     * 
     */
    public BustaSoggetto createBustaSoggetto() {
        return new BustaSoggetto();
    }

    /**
     * Create an instance of {@link Find }
     * 
     */
    public Find createFind() {
        return new Find();
    }

    /**
     * Create an instance of {@link MonitorServiceException }
     * 
     */
    public MonitorServiceException createMonitorServiceException() {
        return new MonitorServiceException();
    }

    /**
     * Create an instance of {@link FindAllResponse }
     * 
     */
    public FindAllResponse createFindAllResponse() {
        return new FindAllResponse();
    }

    /**
     * Create an instance of {@link UseInfo }
     * 
     */
    public UseInfo createUseInfo() {
        return new UseInfo();
    }

    /**
     * Create an instance of {@link ObjectId }
     * 
     */
    public ObjectId createObjectId() {
        return new ObjectId();
    }

    /**
     * Create an instance of {@link InUseCondition }
     * 
     */
    public InUseCondition createInUseCondition() {
        return new InUseCondition();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "findAllResponse")
    public JAXBElement<FindAllResponse> createFindAllResponse(FindAllResponse value) {
        return new JAXBElement<FindAllResponse>(ObjectFactory._FindAllResponse_QNAME, FindAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MonitorServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "monitor-service-exception")
    public JAXBElement<MonitorServiceException> createMonitorServiceException(MonitorServiceException value) {
        return new JAXBElement<MonitorServiceException>(ObjectFactory._MonitorServiceException_QNAME, MonitorServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Find }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "find")
    public JAXBElement<Find> createFind(Find value) {
        return new JAXBElement<Find>(ObjectFactory._Find_QNAME, Find.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BustaSoggetto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "busta-soggetto")
    public JAXBElement<BustaSoggetto> createBustaSoggetto(BustaSoggetto value) {
        return new JAXBElement<BustaSoggetto>(ObjectFactory._BustaSoggetto_QNAME, BustaSoggetto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "deleteAllResponse")
    public JAXBElement<DeleteAllResponse> createDeleteAllResponse(DeleteAllResponse value) {
        return new JAXBElement<DeleteAllResponse>(ObjectFactory._DeleteAllResponse_QNAME, DeleteAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "findResponse")
    public JAXBElement<FindResponse> createFindResponse(FindResponse value) {
        return new JAXBElement<FindResponse>(ObjectFactory._FindResponse_QNAME, FindResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "createResponse")
    public JAXBElement<CreateResponse> createCreateResponse(CreateResponse value) {
        return new JAXBElement<CreateResponse>(ObjectFactory._CreateResponse_QNAME, CreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MonitorNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "monitor-not-found-exception")
    public JAXBElement<MonitorNotFoundException> createMonitorNotFoundException(MonitorNotFoundException value) {
        return new JAXBElement<MonitorNotFoundException>(ObjectFactory._MonitorNotFoundException_QNAME, MonitorNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAllByFilterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "deleteAllByFilterResponse")
    public JAXBElement<DeleteAllByFilterResponse> createDeleteAllByFilterResponse(DeleteAllByFilterResponse value) {
        return new JAXBElement<DeleteAllByFilterResponse>(ObjectFactory._DeleteAllByFilterResponse_QNAME, DeleteAllByFilterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "deleteAll")
    public JAXBElement<DeleteAll> createDeleteAll(DeleteAll value) {
        return new JAXBElement<DeleteAll>(ObjectFactory._DeleteAll_QNAME, DeleteAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MonitorMultipleResultException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "monitor-multiple-result-exception")
    public JAXBElement<MonitorMultipleResultException> createMonitorMultipleResultException(MonitorMultipleResultException value) {
        return new JAXBElement<MonitorMultipleResultException>(ObjectFactory._MonitorMultipleResultException_QNAME, MonitorMultipleResultException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BustaServizio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "busta-servizio")
    public JAXBElement<BustaServizio> createBustaServizio(BustaServizio value) {
        return new JAXBElement<BustaServizio>(ObjectFactory._BustaServizio_QNAME, BustaServizio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Busta }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "busta")
    public JAXBElement<Busta> createBusta(Busta value) {
        return new JAXBElement<Busta>(ObjectFactory._Busta_QNAME, Busta.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MonitorNotImplementedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "monitor-not-implemented-exception")
    public JAXBElement<MonitorNotImplementedException> createMonitorNotImplementedException(MonitorNotImplementedException value) {
        return new JAXBElement<MonitorNotImplementedException>(ObjectFactory._MonitorNotImplementedException_QNAME, MonitorNotImplementedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Create }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "create")
    public JAXBElement<Create> createCreate(Create value) {
        return new JAXBElement<Create>(ObjectFactory._Create_QNAME, Create.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchFilterStatoPdd }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "search-filter-stato-pdd")
    public JAXBElement<SearchFilterStatoPdd> createSearchFilterStatoPdd(SearchFilterStatoPdd value) {
        return new JAXBElement<SearchFilterStatoPdd>(ObjectFactory._SearchFilterStatoPdd_QNAME, SearchFilterStatoPdd.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MonitorNotAuthorizedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "monitor-not-authorized-exception")
    public JAXBElement<MonitorNotAuthorizedException> createMonitorNotAuthorizedException(MonitorNotAuthorizedException value) {
        return new JAXBElement<MonitorNotAuthorizedException>(ObjectFactory._MonitorNotAuthorizedException_QNAME, MonitorNotAuthorizedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Filtro }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "filtro")
    public JAXBElement<Filtro> createFiltro(Filtro value) {
        return new JAXBElement<Filtro>(ObjectFactory._Filtro_QNAME, Filtro.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Count }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "count")
    public JAXBElement<Count> createCount(Count value) {
        return new JAXBElement<Count>(ObjectFactory._Count_QNAME, Count.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "findAll")
    public JAXBElement<FindAll> createFindAll(FindAll value) {
        return new JAXBElement<FindAll>(ObjectFactory._FindAll_QNAME, FindAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAllByFilter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "deleteAllByFilter")
    public JAXBElement<DeleteAllByFilter> createDeleteAllByFilter(DeleteAllByFilter value) {
        return new JAXBElement<DeleteAllByFilter>(ObjectFactory._DeleteAllByFilter_QNAME, DeleteAllByFilter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "countResponse")
    public JAXBElement<CountResponse> createCountResponse(CountResponse value) {
        return new JAXBElement<CountResponse>(ObjectFactory._CountResponse_QNAME, CountResponse.class, null, value);
    }

}
