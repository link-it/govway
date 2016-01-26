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

package org.openspcoop2.core.registry.ws.client.portadominio.search;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.registry.ws.client.portadominio.search package. 
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

    private final static QName _Exists_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "exists");
    private final static QName _SearchFilterPortaDominio_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "search-filter-porta-dominio");
    private final static QName _RegistryNotAuthorizedException_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "registry-not-authorized-exception");
    private final static QName _WrapperIdAccordoServizioParteComune_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdAccordoServizioParteComune");
    private final static QName _RegistryServiceException_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "registry-service-exception");
    private final static QName _GetResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "getResponse");
    private final static QName _Get_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "get");
    private final static QName _FindAll_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "findAll");
    private final static QName _CountResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "countResponse");
    private final static QName _WrapperIdAccordoCooperazione_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdAccordoCooperazione");
    private final static QName _FindAllResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "findAllResponse");
    private final static QName _Find_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "find");
    private final static QName _FindAllIds_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "findAllIds");
    private final static QName _FindResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "findResponse");
    private final static QName _InUseResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "inUseResponse");
    private final static QName _InUse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "inUse");
    private final static QName _RegistryNotFoundException_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "registry-not-found-exception");
    private final static QName _ExistsResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "existsResponse");
    private final static QName _RegistryNotImplementedException_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "registry-not-implemented-exception");
    private final static QName _Count_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "count");
    private final static QName _RegistryMultipleResultException_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "registry-multiple-result-exception");
    private final static QName _WrapperIdAccordoServizioParteSpecifica_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdAccordoServizioParteSpecifica");
    private final static QName _FindAllIdsResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "findAllIdsResponse");
    private final static QName _WrapperIdSoggetto_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdSoggetto");
    private final static QName _WrapperIdPortaDominio_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdPortaDominio");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.registry.ws.client.portadominio.search
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WrapperIdSoggetto }
     * 
     */
    public WrapperIdSoggetto createWrapperIdSoggetto() {
        return new WrapperIdSoggetto();
    }

    /**
     * Create an instance of {@link FindAllIdsResponse }
     * 
     */
    public FindAllIdsResponse createFindAllIdsResponse() {
        return new FindAllIdsResponse();
    }

    /**
     * Create an instance of {@link WrapperIdAccordoServizioParteSpecifica }
     * 
     */
    public WrapperIdAccordoServizioParteSpecifica createWrapperIdAccordoServizioParteSpecifica() {
        return new WrapperIdAccordoServizioParteSpecifica();
    }

    /**
     * Create an instance of {@link WrapperIdPortaDominio }
     * 
     */
    public WrapperIdPortaDominio createWrapperIdPortaDominio() {
        return new WrapperIdPortaDominio();
    }

    /**
     * Create an instance of {@link Count }
     * 
     */
    public Count createCount() {
        return new Count();
    }

    /**
     * Create an instance of {@link RegistryMultipleResultException }
     * 
     */
    public RegistryMultipleResultException createRegistryMultipleResultException() {
        return new RegistryMultipleResultException();
    }

    /**
     * Create an instance of {@link RegistryNotImplementedException }
     * 
     */
    public RegistryNotImplementedException createRegistryNotImplementedException() {
        return new RegistryNotImplementedException();
    }

    /**
     * Create an instance of {@link ExistsResponse }
     * 
     */
    public ExistsResponse createExistsResponse() {
        return new ExistsResponse();
    }

    /**
     * Create an instance of {@link RegistryNotFoundException }
     * 
     */
    public RegistryNotFoundException createRegistryNotFoundException() {
        return new RegistryNotFoundException();
    }

    /**
     * Create an instance of {@link InUse }
     * 
     */
    public InUse createInUse() {
        return new InUse();
    }

    /**
     * Create an instance of {@link FindResponse }
     * 
     */
    public FindResponse createFindResponse() {
        return new FindResponse();
    }

    /**
     * Create an instance of {@link InUseResponse }
     * 
     */
    public InUseResponse createInUseResponse() {
        return new InUseResponse();
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
     * Create an instance of {@link CountResponse }
     * 
     */
    public CountResponse createCountResponse() {
        return new CountResponse();
    }

    /**
     * Create an instance of {@link WrapperIdAccordoCooperazione }
     * 
     */
    public WrapperIdAccordoCooperazione createWrapperIdAccordoCooperazione() {
        return new WrapperIdAccordoCooperazione();
    }

    /**
     * Create an instance of {@link Get }
     * 
     */
    public Get createGet() {
        return new Get();
    }

    /**
     * Create an instance of {@link FindAll }
     * 
     */
    public FindAll createFindAll() {
        return new FindAll();
    }

    /**
     * Create an instance of {@link RegistryServiceException }
     * 
     */
    public RegistryServiceException createRegistryServiceException() {
        return new RegistryServiceException();
    }

    /**
     * Create an instance of {@link GetResponse }
     * 
     */
    public GetResponse createGetResponse() {
        return new GetResponse();
    }

    /**
     * Create an instance of {@link WrapperIdAccordoServizioParteComune }
     * 
     */
    public WrapperIdAccordoServizioParteComune createWrapperIdAccordoServizioParteComune() {
        return new WrapperIdAccordoServizioParteComune();
    }

    /**
     * Create an instance of {@link RegistryNotAuthorizedException }
     * 
     */
    public RegistryNotAuthorizedException createRegistryNotAuthorizedException() {
        return new RegistryNotAuthorizedException();
    }

    /**
     * Create an instance of {@link Exists }
     * 
     */
    public Exists createExists() {
        return new Exists();
    }

    /**
     * Create an instance of {@link SearchFilterPortaDominio }
     * 
     */
    public SearchFilterPortaDominio createSearchFilterPortaDominio() {
        return new SearchFilterPortaDominio();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Exists }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "exists")
    public JAXBElement<Exists> createExists(Exists value) {
        return new JAXBElement<Exists>(ObjectFactory._Exists_QNAME, Exists.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchFilterPortaDominio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "search-filter-porta-dominio")
    public JAXBElement<SearchFilterPortaDominio> createSearchFilterPortaDominio(SearchFilterPortaDominio value) {
        return new JAXBElement<SearchFilterPortaDominio>(ObjectFactory._SearchFilterPortaDominio_QNAME, SearchFilterPortaDominio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryNotAuthorizedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "registry-not-authorized-exception")
    public JAXBElement<RegistryNotAuthorizedException> createRegistryNotAuthorizedException(RegistryNotAuthorizedException value) {
        return new JAXBElement<RegistryNotAuthorizedException>(ObjectFactory._RegistryNotAuthorizedException_QNAME, RegistryNotAuthorizedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdAccordoServizioParteComune }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdAccordoServizioParteComune")
    public JAXBElement<WrapperIdAccordoServizioParteComune> createWrapperIdAccordoServizioParteComune(WrapperIdAccordoServizioParteComune value) {
        return new JAXBElement<WrapperIdAccordoServizioParteComune>(ObjectFactory._WrapperIdAccordoServizioParteComune_QNAME, WrapperIdAccordoServizioParteComune.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "registry-service-exception")
    public JAXBElement<RegistryServiceException> createRegistryServiceException(RegistryServiceException value) {
        return new JAXBElement<RegistryServiceException>(ObjectFactory._RegistryServiceException_QNAME, RegistryServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "getResponse")
    public JAXBElement<GetResponse> createGetResponse(GetResponse value) {
        return new JAXBElement<GetResponse>(ObjectFactory._GetResponse_QNAME, GetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Get }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "get")
    public JAXBElement<Get> createGet(Get value) {
        return new JAXBElement<Get>(ObjectFactory._Get_QNAME, Get.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "findAll")
    public JAXBElement<FindAll> createFindAll(FindAll value) {
        return new JAXBElement<FindAll>(ObjectFactory._FindAll_QNAME, FindAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "countResponse")
    public JAXBElement<CountResponse> createCountResponse(CountResponse value) {
        return new JAXBElement<CountResponse>(ObjectFactory._CountResponse_QNAME, CountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdAccordoCooperazione }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdAccordoCooperazione")
    public JAXBElement<WrapperIdAccordoCooperazione> createWrapperIdAccordoCooperazione(WrapperIdAccordoCooperazione value) {
        return new JAXBElement<WrapperIdAccordoCooperazione>(ObjectFactory._WrapperIdAccordoCooperazione_QNAME, WrapperIdAccordoCooperazione.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "findAllResponse")
    public JAXBElement<FindAllResponse> createFindAllResponse(FindAllResponse value) {
        return new JAXBElement<FindAllResponse>(ObjectFactory._FindAllResponse_QNAME, FindAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Find }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "find")
    public JAXBElement<Find> createFind(Find value) {
        return new JAXBElement<Find>(ObjectFactory._Find_QNAME, Find.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllIds }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "findAllIds")
    public JAXBElement<FindAllIds> createFindAllIds(FindAllIds value) {
        return new JAXBElement<FindAllIds>(ObjectFactory._FindAllIds_QNAME, FindAllIds.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "findResponse")
    public JAXBElement<FindResponse> createFindResponse(FindResponse value) {
        return new JAXBElement<FindResponse>(ObjectFactory._FindResponse_QNAME, FindResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InUseResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "inUseResponse")
    public JAXBElement<InUseResponse> createInUseResponse(InUseResponse value) {
        return new JAXBElement<InUseResponse>(ObjectFactory._InUseResponse_QNAME, InUseResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InUse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "inUse")
    public JAXBElement<InUse> createInUse(InUse value) {
        return new JAXBElement<InUse>(ObjectFactory._InUse_QNAME, InUse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "registry-not-found-exception")
    public JAXBElement<RegistryNotFoundException> createRegistryNotFoundException(RegistryNotFoundException value) {
        return new JAXBElement<RegistryNotFoundException>(ObjectFactory._RegistryNotFoundException_QNAME, RegistryNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExistsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "existsResponse")
    public JAXBElement<ExistsResponse> createExistsResponse(ExistsResponse value) {
        return new JAXBElement<ExistsResponse>(ObjectFactory._ExistsResponse_QNAME, ExistsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryNotImplementedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "registry-not-implemented-exception")
    public JAXBElement<RegistryNotImplementedException> createRegistryNotImplementedException(RegistryNotImplementedException value) {
        return new JAXBElement<RegistryNotImplementedException>(ObjectFactory._RegistryNotImplementedException_QNAME, RegistryNotImplementedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Count }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "count")
    public JAXBElement<Count> createCount(Count value) {
        return new JAXBElement<Count>(ObjectFactory._Count_QNAME, Count.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryMultipleResultException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "registry-multiple-result-exception")
    public JAXBElement<RegistryMultipleResultException> createRegistryMultipleResultException(RegistryMultipleResultException value) {
        return new JAXBElement<RegistryMultipleResultException>(ObjectFactory._RegistryMultipleResultException_QNAME, RegistryMultipleResultException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdAccordoServizioParteSpecifica }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdAccordoServizioParteSpecifica")
    public JAXBElement<WrapperIdAccordoServizioParteSpecifica> createWrapperIdAccordoServizioParteSpecifica(WrapperIdAccordoServizioParteSpecifica value) {
        return new JAXBElement<WrapperIdAccordoServizioParteSpecifica>(ObjectFactory._WrapperIdAccordoServizioParteSpecifica_QNAME, WrapperIdAccordoServizioParteSpecifica.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllIdsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "findAllIdsResponse")
    public JAXBElement<FindAllIdsResponse> createFindAllIdsResponse(FindAllIdsResponse value) {
        return new JAXBElement<FindAllIdsResponse>(ObjectFactory._FindAllIdsResponse_QNAME, FindAllIdsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdSoggetto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdSoggetto")
    public JAXBElement<WrapperIdSoggetto> createWrapperIdSoggetto(WrapperIdSoggetto value) {
        return new JAXBElement<WrapperIdSoggetto>(ObjectFactory._WrapperIdSoggetto_QNAME, WrapperIdSoggetto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdPortaDominio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdPortaDominio")
    public JAXBElement<WrapperIdPortaDominio> createWrapperIdPortaDominio(WrapperIdPortaDominio value) {
        return new JAXBElement<WrapperIdPortaDominio>(ObjectFactory._WrapperIdPortaDominio_QNAME, WrapperIdPortaDominio.class, null, value);
    }

}
