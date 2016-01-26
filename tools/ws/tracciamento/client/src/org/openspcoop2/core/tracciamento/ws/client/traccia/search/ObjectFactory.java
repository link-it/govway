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

package org.openspcoop2.core.tracciamento.ws.client.traccia.search;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.tracciamento.ws.client.traccia.search package. 
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

    private final static QName _Protocollo_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "protocollo");
    private final static QName _Count_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "count");
    private final static QName _TracciamentoMultipleResultException_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "tracciamento-multiple-result-exception");
    private final static QName _FindAllIdsResponse_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "findAllIdsResponse");
    private final static QName _Dominio_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "dominio");
    private final static QName _Servizio_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "servizio");
    private final static QName _ExistsResponse_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "existsResponse");
    private final static QName _SearchFilterTraccia_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "search-filter-traccia");
    private final static QName _FindAllResponse_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "findAllResponse");
    private final static QName _Find_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "find");
    private final static QName _FindAllIds_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "findAllIds");
    private final static QName _FindResponse_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "findResponse");
    private final static QName _DominioSoggetto_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "dominio-soggetto");
    private final static QName _FindAll_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "findAll");
    private final static QName _TracciamentoNotAuthorizedException_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "tracciamento-not-authorized-exception");
    private final static QName _Get_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "get");
    private final static QName _TracciamentoNotImplementedException_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "tracciamento-not-implemented-exception");
    private final static QName _CountResponse_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "countResponse");
    private final static QName _SoggettoIdentificativo_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "soggetto-identificativo");
    private final static QName _TracciamentoNotFoundException_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "tracciamento-not-found-exception");
    private final static QName _GetResponse_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "getResponse");
    private final static QName _ProfiloCollaborazione_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "profilo-collaborazione");
    private final static QName _TracciamentoServiceException_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "tracciamento-service-exception");
    private final static QName _Busta_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "busta");
    private final static QName _Exists_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "exists");
    private final static QName _Soggetto_QNAME = new QName("http://www.openspcoop2.org/core/tracciamento/management", "soggetto");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.tracciamento.ws.client.traccia.search
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FindAllIdsResponse }
     * 
     */
    public FindAllIdsResponse createFindAllIdsResponse() {
        return new FindAllIdsResponse();
    }

    /**
     * Create an instance of {@link TracciamentoMultipleResultException }
     * 
     */
    public TracciamentoMultipleResultException createTracciamentoMultipleResultException() {
        return new TracciamentoMultipleResultException();
    }

    /**
     * Create an instance of {@link Count }
     * 
     */
    public Count createCount() {
        return new Count();
    }

    /**
     * Create an instance of {@link Protocollo }
     * 
     */
    public Protocollo createProtocollo() {
        return new Protocollo();
    }

    /**
     * Create an instance of {@link SearchFilterTraccia }
     * 
     */
    public SearchFilterTraccia createSearchFilterTraccia() {
        return new SearchFilterTraccia();
    }

    /**
     * Create an instance of {@link ExistsResponse }
     * 
     */
    public ExistsResponse createExistsResponse() {
        return new ExistsResponse();
    }

    /**
     * Create an instance of {@link Servizio }
     * 
     */
    public Servizio createServizio() {
        return new Servizio();
    }

    /**
     * Create an instance of {@link Dominio }
     * 
     */
    public Dominio createDominio() {
        return new Dominio();
    }

    /**
     * Create an instance of {@link DominioSoggetto }
     * 
     */
    public DominioSoggetto createDominioSoggetto() {
        return new DominioSoggetto();
    }

    /**
     * Create an instance of {@link FindResponse }
     * 
     */
    public FindResponse createFindResponse() {
        return new FindResponse();
    }

    /**
     * Create an instance of {@link FindAllIds }
     * 
     */
    public FindAllIds createFindAllIds() {
        return new FindAllIds();
    }

    /**
     * Create an instance of {@link Find }
     * 
     */
    public Find createFind() {
        return new Find();
    }

    /**
     * Create an instance of {@link FindAllResponse }
     * 
     */
    public FindAllResponse createFindAllResponse() {
        return new FindAllResponse();
    }

    /**
     * Create an instance of {@link TracciamentoNotImplementedException }
     * 
     */
    public TracciamentoNotImplementedException createTracciamentoNotImplementedException() {
        return new TracciamentoNotImplementedException();
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
     * Create an instance of {@link TracciamentoNotAuthorizedException }
     * 
     */
    public TracciamentoNotAuthorizedException createTracciamentoNotAuthorizedException() {
        return new TracciamentoNotAuthorizedException();
    }

    /**
     * Create an instance of {@link FindAll }
     * 
     */
    public FindAll createFindAll() {
        return new FindAll();
    }

    /**
     * Create an instance of {@link GetResponse }
     * 
     */
    public GetResponse createGetResponse() {
        return new GetResponse();
    }

    /**
     * Create an instance of {@link TracciamentoNotFoundException }
     * 
     */
    public TracciamentoNotFoundException createTracciamentoNotFoundException() {
        return new TracciamentoNotFoundException();
    }

    /**
     * Create an instance of {@link SoggettoIdentificativo }
     * 
     */
    public SoggettoIdentificativo createSoggettoIdentificativo() {
        return new SoggettoIdentificativo();
    }

    /**
     * Create an instance of {@link Busta }
     * 
     */
    public Busta createBusta() {
        return new Busta();
    }

    /**
     * Create an instance of {@link TracciamentoServiceException }
     * 
     */
    public TracciamentoServiceException createTracciamentoServiceException() {
        return new TracciamentoServiceException();
    }

    /**
     * Create an instance of {@link ProfiloCollaborazione }
     * 
     */
    public ProfiloCollaborazione createProfiloCollaborazione() {
        return new ProfiloCollaborazione();
    }

    /**
     * Create an instance of {@link Soggetto }
     * 
     */
    public Soggetto createSoggetto() {
        return new Soggetto();
    }

    /**
     * Create an instance of {@link Exists }
     * 
     */
    public Exists createExists() {
        return new Exists();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Protocollo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "protocollo")
    public JAXBElement<Protocollo> createProtocollo(Protocollo value) {
        return new JAXBElement<Protocollo>(ObjectFactory._Protocollo_QNAME, Protocollo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Count }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "count")
    public JAXBElement<Count> createCount(Count value) {
        return new JAXBElement<Count>(ObjectFactory._Count_QNAME, Count.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TracciamentoMultipleResultException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "tracciamento-multiple-result-exception")
    public JAXBElement<TracciamentoMultipleResultException> createTracciamentoMultipleResultException(TracciamentoMultipleResultException value) {
        return new JAXBElement<TracciamentoMultipleResultException>(ObjectFactory._TracciamentoMultipleResultException_QNAME, TracciamentoMultipleResultException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllIdsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "findAllIdsResponse")
    public JAXBElement<FindAllIdsResponse> createFindAllIdsResponse(FindAllIdsResponse value) {
        return new JAXBElement<FindAllIdsResponse>(ObjectFactory._FindAllIdsResponse_QNAME, FindAllIdsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Dominio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "dominio")
    public JAXBElement<Dominio> createDominio(Dominio value) {
        return new JAXBElement<Dominio>(ObjectFactory._Dominio_QNAME, Dominio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Servizio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "servizio")
    public JAXBElement<Servizio> createServizio(Servizio value) {
        return new JAXBElement<Servizio>(ObjectFactory._Servizio_QNAME, Servizio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExistsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "existsResponse")
    public JAXBElement<ExistsResponse> createExistsResponse(ExistsResponse value) {
        return new JAXBElement<ExistsResponse>(ObjectFactory._ExistsResponse_QNAME, ExistsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchFilterTraccia }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "search-filter-traccia")
    public JAXBElement<SearchFilterTraccia> createSearchFilterTraccia(SearchFilterTraccia value) {
        return new JAXBElement<SearchFilterTraccia>(ObjectFactory._SearchFilterTraccia_QNAME, SearchFilterTraccia.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "findAllResponse")
    public JAXBElement<FindAllResponse> createFindAllResponse(FindAllResponse value) {
        return new JAXBElement<FindAllResponse>(ObjectFactory._FindAllResponse_QNAME, FindAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Find }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "find")
    public JAXBElement<Find> createFind(Find value) {
        return new JAXBElement<Find>(ObjectFactory._Find_QNAME, Find.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllIds }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "findAllIds")
    public JAXBElement<FindAllIds> createFindAllIds(FindAllIds value) {
        return new JAXBElement<FindAllIds>(ObjectFactory._FindAllIds_QNAME, FindAllIds.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "findResponse")
    public JAXBElement<FindResponse> createFindResponse(FindResponse value) {
        return new JAXBElement<FindResponse>(ObjectFactory._FindResponse_QNAME, FindResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DominioSoggetto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "dominio-soggetto")
    public JAXBElement<DominioSoggetto> createDominioSoggetto(DominioSoggetto value) {
        return new JAXBElement<DominioSoggetto>(ObjectFactory._DominioSoggetto_QNAME, DominioSoggetto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "findAll")
    public JAXBElement<FindAll> createFindAll(FindAll value) {
        return new JAXBElement<FindAll>(ObjectFactory._FindAll_QNAME, FindAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TracciamentoNotAuthorizedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "tracciamento-not-authorized-exception")
    public JAXBElement<TracciamentoNotAuthorizedException> createTracciamentoNotAuthorizedException(TracciamentoNotAuthorizedException value) {
        return new JAXBElement<TracciamentoNotAuthorizedException>(ObjectFactory._TracciamentoNotAuthorizedException_QNAME, TracciamentoNotAuthorizedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Get }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "get")
    public JAXBElement<Get> createGet(Get value) {
        return new JAXBElement<Get>(ObjectFactory._Get_QNAME, Get.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TracciamentoNotImplementedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "tracciamento-not-implemented-exception")
    public JAXBElement<TracciamentoNotImplementedException> createTracciamentoNotImplementedException(TracciamentoNotImplementedException value) {
        return new JAXBElement<TracciamentoNotImplementedException>(ObjectFactory._TracciamentoNotImplementedException_QNAME, TracciamentoNotImplementedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "countResponse")
    public JAXBElement<CountResponse> createCountResponse(CountResponse value) {
        return new JAXBElement<CountResponse>(ObjectFactory._CountResponse_QNAME, CountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SoggettoIdentificativo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "soggetto-identificativo")
    public JAXBElement<SoggettoIdentificativo> createSoggettoIdentificativo(SoggettoIdentificativo value) {
        return new JAXBElement<SoggettoIdentificativo>(ObjectFactory._SoggettoIdentificativo_QNAME, SoggettoIdentificativo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TracciamentoNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "tracciamento-not-found-exception")
    public JAXBElement<TracciamentoNotFoundException> createTracciamentoNotFoundException(TracciamentoNotFoundException value) {
        return new JAXBElement<TracciamentoNotFoundException>(ObjectFactory._TracciamentoNotFoundException_QNAME, TracciamentoNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "getResponse")
    public JAXBElement<GetResponse> createGetResponse(GetResponse value) {
        return new JAXBElement<GetResponse>(ObjectFactory._GetResponse_QNAME, GetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProfiloCollaborazione }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "profilo-collaborazione")
    public JAXBElement<ProfiloCollaborazione> createProfiloCollaborazione(ProfiloCollaborazione value) {
        return new JAXBElement<ProfiloCollaborazione>(ObjectFactory._ProfiloCollaborazione_QNAME, ProfiloCollaborazione.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TracciamentoServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "tracciamento-service-exception")
    public JAXBElement<TracciamentoServiceException> createTracciamentoServiceException(TracciamentoServiceException value) {
        return new JAXBElement<TracciamentoServiceException>(ObjectFactory._TracciamentoServiceException_QNAME, TracciamentoServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Busta }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "busta")
    public JAXBElement<Busta> createBusta(Busta value) {
        return new JAXBElement<Busta>(ObjectFactory._Busta_QNAME, Busta.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exists }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "exists")
    public JAXBElement<Exists> createExists(Exists value) {
        return new JAXBElement<Exists>(ObjectFactory._Exists_QNAME, Exists.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Soggetto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "soggetto")
    public JAXBElement<Soggetto> createSoggetto(Soggetto value) {
        return new JAXBElement<Soggetto>(ObjectFactory._Soggetto_QNAME, Soggetto.class, null, value);
    }

}
