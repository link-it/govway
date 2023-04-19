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

package org.openspcoop2.core.config.ws.client.soggetto.search;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.config.ws.client.soggetto.search package. 
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

    private static final QName _SearchFilterSoggetto_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "search-filter-soggetto");
    private static final QName _WrapperIdSoggetto_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "wrapperIdSoggetto");
    private static final QName _WrapperIdPortaDelegata_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "wrapperIdPortaDelegata");
    private static final QName _WrapperIdPortaApplicativa_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "wrapperIdPortaApplicativa");
    private static final QName _WrapperIdServizioApplicativo_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "wrapperIdServizioApplicativo");
    private static final QName _ConfigServiceException_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "config-service-exception");
    private static final QName _ConfigNotFoundException_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "config-not-found-exception");
    private static final QName _ConfigMultipleResultException_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "config-multiple-result-exception");
    private static final QName _ConfigNotImplementedException_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "config-not-implemented-exception");
    private static final QName _ConfigNotAuthorizedException_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "config-not-authorized-exception");
    private static final QName _FindAll_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "findAll");
    private static final QName _FindAllResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "findAllResponse");
    private static final QName _Find_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "find");
    private static final QName _FindResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "findResponse");
    private static final QName _Count_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "count");
    private static final QName _CountResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "countResponse");
    private static final QName _Get_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "get");
    private static final QName _GetResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "getResponse");
    private static final QName _Exists_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "exists");
    private static final QName _ExistsResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "existsResponse");
    private static final QName _FindAllIds_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "findAllIds");
    private static final QName _FindAllIdsResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "findAllIdsResponse");
    private static final QName _InUse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "inUse");
    private static final QName _InUseResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "inUseResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.config.ws.client.soggetto.search
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SearchFilterSoggetto }
     * 
     */
    public SearchFilterSoggetto createSearchFilterSoggetto() {
        return new SearchFilterSoggetto();
    }

    /**
     * Create an instance of {@link WrapperIdSoggetto }
     * 
     */
    public WrapperIdSoggetto createWrapperIdSoggetto() {
        return new WrapperIdSoggetto();
    }

    /**
     * Create an instance of {@link WrapperIdPortaDelegata }
     * 
     */
    public WrapperIdPortaDelegata createWrapperIdPortaDelegata() {
        return new WrapperIdPortaDelegata();
    }

    /**
     * Create an instance of {@link WrapperIdPortaApplicativa }
     * 
     */
    public WrapperIdPortaApplicativa createWrapperIdPortaApplicativa() {
        return new WrapperIdPortaApplicativa();
    }

    /**
     * Create an instance of {@link WrapperIdServizioApplicativo }
     * 
     */
    public WrapperIdServizioApplicativo createWrapperIdServizioApplicativo() {
        return new WrapperIdServizioApplicativo();
    }

    /**
     * Create an instance of {@link ConfigServiceException }
     * 
     */
    public ConfigServiceException createConfigServiceException() {
        return new ConfigServiceException();
    }

    /**
     * Create an instance of {@link ConfigNotFoundException }
     * 
     */
    public ConfigNotFoundException createConfigNotFoundException() {
        return new ConfigNotFoundException();
    }

    /**
     * Create an instance of {@link ConfigMultipleResultException }
     * 
     */
    public ConfigMultipleResultException createConfigMultipleResultException() {
        return new ConfigMultipleResultException();
    }

    /**
     * Create an instance of {@link ConfigNotImplementedException }
     * 
     */
    public ConfigNotImplementedException createConfigNotImplementedException() {
        return new ConfigNotImplementedException();
    }

    /**
     * Create an instance of {@link ConfigNotAuthorizedException }
     * 
     */
    public ConfigNotAuthorizedException createConfigNotAuthorizedException() {
        return new ConfigNotAuthorizedException();
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
     * Create an instance of {@link InUse }
     * 
     */
    public InUse createInUse() {
        return new InUse();
    }

    /**
     * Create an instance of {@link InUseResponse }
     * 
     */
    public InUseResponse createInUseResponse() {
        return new InUseResponse();
    }

    /**
     * Create an instance of {@link UseInfo }
     * 
     */
    public UseInfo createUseInfo() {
        return new UseInfo();
    }

    /**
     * Create an instance of {@link InUseCondition }
     * 
     */
    public InUseCondition createInUseCondition() {
        return new InUseCondition();
    }

    /**
     * Create an instance of {@link ObjectId }
     * 
     */
    public ObjectId createObjectId() {
        return new ObjectId();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchFilterSoggetto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "search-filter-soggetto")
    public JAXBElement<SearchFilterSoggetto> createSearchFilterSoggetto(SearchFilterSoggetto value) {
        return new JAXBElement<SearchFilterSoggetto>(ObjectFactory._SearchFilterSoggetto_QNAME, SearchFilterSoggetto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdSoggetto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "wrapperIdSoggetto")
    public JAXBElement<WrapperIdSoggetto> createWrapperIdSoggetto(WrapperIdSoggetto value) {
        return new JAXBElement<WrapperIdSoggetto>(ObjectFactory._WrapperIdSoggetto_QNAME, WrapperIdSoggetto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdPortaDelegata }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "wrapperIdPortaDelegata")
    public JAXBElement<WrapperIdPortaDelegata> createWrapperIdPortaDelegata(WrapperIdPortaDelegata value) {
        return new JAXBElement<WrapperIdPortaDelegata>(ObjectFactory._WrapperIdPortaDelegata_QNAME, WrapperIdPortaDelegata.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdPortaApplicativa }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "wrapperIdPortaApplicativa")
    public JAXBElement<WrapperIdPortaApplicativa> createWrapperIdPortaApplicativa(WrapperIdPortaApplicativa value) {
        return new JAXBElement<WrapperIdPortaApplicativa>(ObjectFactory._WrapperIdPortaApplicativa_QNAME, WrapperIdPortaApplicativa.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdServizioApplicativo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "wrapperIdServizioApplicativo")
    public JAXBElement<WrapperIdServizioApplicativo> createWrapperIdServizioApplicativo(WrapperIdServizioApplicativo value) {
        return new JAXBElement<WrapperIdServizioApplicativo>(ObjectFactory._WrapperIdServizioApplicativo_QNAME, WrapperIdServizioApplicativo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfigServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "config-service-exception")
    public JAXBElement<ConfigServiceException> createConfigServiceException(ConfigServiceException value) {
        return new JAXBElement<ConfigServiceException>(ObjectFactory._ConfigServiceException_QNAME, ConfigServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfigNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "config-not-found-exception")
    public JAXBElement<ConfigNotFoundException> createConfigNotFoundException(ConfigNotFoundException value) {
        return new JAXBElement<ConfigNotFoundException>(ObjectFactory._ConfigNotFoundException_QNAME, ConfigNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfigMultipleResultException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "config-multiple-result-exception")
    public JAXBElement<ConfigMultipleResultException> createConfigMultipleResultException(ConfigMultipleResultException value) {
        return new JAXBElement<ConfigMultipleResultException>(ObjectFactory._ConfigMultipleResultException_QNAME, ConfigMultipleResultException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfigNotImplementedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "config-not-implemented-exception")
    public JAXBElement<ConfigNotImplementedException> createConfigNotImplementedException(ConfigNotImplementedException value) {
        return new JAXBElement<ConfigNotImplementedException>(ObjectFactory._ConfigNotImplementedException_QNAME, ConfigNotImplementedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfigNotAuthorizedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "config-not-authorized-exception")
    public JAXBElement<ConfigNotAuthorizedException> createConfigNotAuthorizedException(ConfigNotAuthorizedException value) {
        return new JAXBElement<ConfigNotAuthorizedException>(ObjectFactory._ConfigNotAuthorizedException_QNAME, ConfigNotAuthorizedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "findAll")
    public JAXBElement<FindAll> createFindAll(FindAll value) {
        return new JAXBElement<FindAll>(ObjectFactory._FindAll_QNAME, FindAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "findAllResponse")
    public JAXBElement<FindAllResponse> createFindAllResponse(FindAllResponse value) {
        return new JAXBElement<FindAllResponse>(ObjectFactory._FindAllResponse_QNAME, FindAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Find }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "find")
    public JAXBElement<Find> createFind(Find value) {
        return new JAXBElement<Find>(ObjectFactory._Find_QNAME, Find.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "findResponse")
    public JAXBElement<FindResponse> createFindResponse(FindResponse value) {
        return new JAXBElement<FindResponse>(ObjectFactory._FindResponse_QNAME, FindResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Count }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "count")
    public JAXBElement<Count> createCount(Count value) {
        return new JAXBElement<Count>(ObjectFactory._Count_QNAME, Count.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "countResponse")
    public JAXBElement<CountResponse> createCountResponse(CountResponse value) {
        return new JAXBElement<CountResponse>(ObjectFactory._CountResponse_QNAME, CountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Get }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "get")
    public JAXBElement<Get> createGet(Get value) {
        return new JAXBElement<Get>(ObjectFactory._Get_QNAME, Get.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "getResponse")
    public JAXBElement<GetResponse> createGetResponse(GetResponse value) {
        return new JAXBElement<GetResponse>(ObjectFactory._GetResponse_QNAME, GetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exists }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "exists")
    public JAXBElement<Exists> createExists(Exists value) {
        return new JAXBElement<Exists>(ObjectFactory._Exists_QNAME, Exists.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExistsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "existsResponse")
    public JAXBElement<ExistsResponse> createExistsResponse(ExistsResponse value) {
        return new JAXBElement<ExistsResponse>(ObjectFactory._ExistsResponse_QNAME, ExistsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllIds }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "findAllIds")
    public JAXBElement<FindAllIds> createFindAllIds(FindAllIds value) {
        return new JAXBElement<FindAllIds>(ObjectFactory._FindAllIds_QNAME, FindAllIds.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllIdsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "findAllIdsResponse")
    public JAXBElement<FindAllIdsResponse> createFindAllIdsResponse(FindAllIdsResponse value) {
        return new JAXBElement<FindAllIdsResponse>(ObjectFactory._FindAllIdsResponse_QNAME, FindAllIdsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InUse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "inUse")
    public JAXBElement<InUse> createInUse(InUse value) {
        return new JAXBElement<InUse>(ObjectFactory._InUse_QNAME, InUse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InUseResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "inUseResponse")
    public JAXBElement<InUseResponse> createInUseResponse(InUseResponse value) {
        return new JAXBElement<InUseResponse>(ObjectFactory._InUseResponse_QNAME, InUseResponse.class, null, value);
    }

}
