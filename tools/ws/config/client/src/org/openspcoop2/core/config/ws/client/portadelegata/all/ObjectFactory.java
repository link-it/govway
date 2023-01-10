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

package org.openspcoop2.core.config.ws.client.portadelegata.all;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.config.ws.client.portadelegata.all package. 
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

    private final static QName _SearchFilterPortaDelegata_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "search-filter-porta-delegata");
    private final static QName _PortaDelegataSoggettoErogatore_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "porta-delegata-soggetto-erogatore");
    private final static QName _PortaDelegataServizio_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "porta-delegata-servizio");
    private final static QName _PortaDelegataAzione_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "porta-delegata-azione");
    private final static QName _AutorizzazioneRuoli_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "autorizzazione-ruoli");
    private final static QName _PortaDelegataLocalForward_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "porta-delegata-local-forward");
    private final static QName _MtomProcessorFlow_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "mtom-processor-flow");
    private final static QName _MtomProcessor_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "mtom-processor");
    private final static QName _ValidazioneContenutiApplicativi_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "validazione-contenuti-applicativi");
    private final static QName _WrapperIdSoggetto_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "wrapperIdSoggetto");
    private final static QName _WrapperIdPortaDelegata_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "wrapperIdPortaDelegata");
    private final static QName _WrapperIdPortaApplicativa_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "wrapperIdPortaApplicativa");
    private final static QName _WrapperIdServizioApplicativo_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "wrapperIdServizioApplicativo");
    private final static QName _ConfigServiceException_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "config-service-exception");
    private final static QName _ConfigNotFoundException_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "config-not-found-exception");
    private final static QName _ConfigMultipleResultException_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "config-multiple-result-exception");
    private final static QName _ConfigNotImplementedException_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "config-not-implemented-exception");
    private final static QName _ConfigNotAuthorizedException_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "config-not-authorized-exception");
    private final static QName _FindAll_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "findAll");
    private final static QName _FindAllResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "findAllResponse");
    private final static QName _Find_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "find");
    private final static QName _FindResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "findResponse");
    private final static QName _Count_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "count");
    private final static QName _CountResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "countResponse");
    private final static QName _Get_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "get");
    private final static QName _GetResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "getResponse");
    private final static QName _Exists_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "exists");
    private final static QName _ExistsResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "existsResponse");
    private final static QName _FindAllIds_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "findAllIds");
    private final static QName _FindAllIdsResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "findAllIdsResponse");
    private final static QName _InUse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "inUse");
    private final static QName _InUseResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "inUseResponse");
    private final static QName _Create_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "create");
    private final static QName _CreateResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "createResponse");
    private final static QName _Update_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "update");
    private final static QName _UpdateResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "updateResponse");
    private final static QName _UpdateOrCreate_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "updateOrCreate");
    private final static QName _UpdateOrCreateResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "updateOrCreateResponse");
    private final static QName _DeleteById_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "deleteById");
    private final static QName _DeleteByIdResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "deleteByIdResponse");
    private final static QName _DeleteAll_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "deleteAll");
    private final static QName _DeleteAllResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "deleteAllResponse");
    private final static QName _DeleteAllByFilter_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "deleteAllByFilter");
    private final static QName _DeleteAllByFilterResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "deleteAllByFilterResponse");
    private final static QName _Delete_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "delete");
    private final static QName _DeleteResponse_QNAME = new QName("http://www.openspcoop2.org/core/config/management", "deleteResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.config.ws.client.portadelegata.all
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SearchFilterPortaDelegata }
     * 
     */
    public SearchFilterPortaDelegata createSearchFilterPortaDelegata() {
        return new SearchFilterPortaDelegata();
    }

    /**
     * Create an instance of {@link PortaDelegataSoggettoErogatore }
     * 
     */
    public PortaDelegataSoggettoErogatore createPortaDelegataSoggettoErogatore() {
        return new PortaDelegataSoggettoErogatore();
    }

    /**
     * Create an instance of {@link PortaDelegataServizio }
     * 
     */
    public PortaDelegataServizio createPortaDelegataServizio() {
        return new PortaDelegataServizio();
    }

    /**
     * Create an instance of {@link PortaDelegataAzione }
     * 
     */
    public PortaDelegataAzione createPortaDelegataAzione() {
        return new PortaDelegataAzione();
    }

    /**
     * Create an instance of {@link AutorizzazioneRuoli }
     * 
     */
    public AutorizzazioneRuoli createAutorizzazioneRuoli() {
        return new AutorizzazioneRuoli();
    }

    /**
     * Create an instance of {@link PortaDelegataLocalForward }
     * 
     */
    public PortaDelegataLocalForward createPortaDelegataLocalForward() {
        return new PortaDelegataLocalForward();
    }

    /**
     * Create an instance of {@link MtomProcessorFlow }
     * 
     */
    public MtomProcessorFlow createMtomProcessorFlow() {
        return new MtomProcessorFlow();
    }

    /**
     * Create an instance of {@link MtomProcessor }
     * 
     */
    public MtomProcessor createMtomProcessor() {
        return new MtomProcessor();
    }

    /**
     * Create an instance of {@link ValidazioneContenutiApplicativi }
     * 
     */
    public ValidazioneContenutiApplicativi createValidazioneContenutiApplicativi() {
        return new ValidazioneContenutiApplicativi();
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
     * Create an instance of {@link Create }
     * 
     */
    public Create createCreate() {
        return new Create();
    }

    /**
     * Create an instance of {@link CreateResponse }
     * 
     */
    public CreateResponse createCreateResponse() {
        return new CreateResponse();
    }

    /**
     * Create an instance of {@link Update }
     * 
     */
    public Update createUpdate() {
        return new Update();
    }

    /**
     * Create an instance of {@link UpdateResponse }
     * 
     */
    public UpdateResponse createUpdateResponse() {
        return new UpdateResponse();
    }

    /**
     * Create an instance of {@link UpdateOrCreate }
     * 
     */
    public UpdateOrCreate createUpdateOrCreate() {
        return new UpdateOrCreate();
    }

    /**
     * Create an instance of {@link UpdateOrCreateResponse }
     * 
     */
    public UpdateOrCreateResponse createUpdateOrCreateResponse() {
        return new UpdateOrCreateResponse();
    }

    /**
     * Create an instance of {@link DeleteById }
     * 
     */
    public DeleteById createDeleteById() {
        return new DeleteById();
    }

    /**
     * Create an instance of {@link DeleteByIdResponse }
     * 
     */
    public DeleteByIdResponse createDeleteByIdResponse() {
        return new DeleteByIdResponse();
    }

    /**
     * Create an instance of {@link DeleteAll }
     * 
     */
    public DeleteAll createDeleteAll() {
        return new DeleteAll();
    }

    /**
     * Create an instance of {@link DeleteAllResponse }
     * 
     */
    public DeleteAllResponse createDeleteAllResponse() {
        return new DeleteAllResponse();
    }

    /**
     * Create an instance of {@link DeleteAllByFilter }
     * 
     */
    public DeleteAllByFilter createDeleteAllByFilter() {
        return new DeleteAllByFilter();
    }

    /**
     * Create an instance of {@link DeleteAllByFilterResponse }
     * 
     */
    public DeleteAllByFilterResponse createDeleteAllByFilterResponse() {
        return new DeleteAllByFilterResponse();
    }

    /**
     * Create an instance of {@link Delete }
     * 
     */
    public Delete createDelete() {
        return new Delete();
    }

    /**
     * Create an instance of {@link DeleteResponse }
     * 
     */
    public DeleteResponse createDeleteResponse() {
        return new DeleteResponse();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchFilterPortaDelegata }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "search-filter-porta-delegata")
    public JAXBElement<SearchFilterPortaDelegata> createSearchFilterPortaDelegata(SearchFilterPortaDelegata value) {
        return new JAXBElement<SearchFilterPortaDelegata>(ObjectFactory._SearchFilterPortaDelegata_QNAME, SearchFilterPortaDelegata.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PortaDelegataSoggettoErogatore }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "porta-delegata-soggetto-erogatore")
    public JAXBElement<PortaDelegataSoggettoErogatore> createPortaDelegataSoggettoErogatore(PortaDelegataSoggettoErogatore value) {
        return new JAXBElement<PortaDelegataSoggettoErogatore>(ObjectFactory._PortaDelegataSoggettoErogatore_QNAME, PortaDelegataSoggettoErogatore.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PortaDelegataServizio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "porta-delegata-servizio")
    public JAXBElement<PortaDelegataServizio> createPortaDelegataServizio(PortaDelegataServizio value) {
        return new JAXBElement<PortaDelegataServizio>(ObjectFactory._PortaDelegataServizio_QNAME, PortaDelegataServizio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PortaDelegataAzione }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "porta-delegata-azione")
    public JAXBElement<PortaDelegataAzione> createPortaDelegataAzione(PortaDelegataAzione value) {
        return new JAXBElement<PortaDelegataAzione>(ObjectFactory._PortaDelegataAzione_QNAME, PortaDelegataAzione.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AutorizzazioneRuoli }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "autorizzazione-ruoli")
    public JAXBElement<AutorizzazioneRuoli> createAutorizzazioneRuoli(AutorizzazioneRuoli value) {
        return new JAXBElement<AutorizzazioneRuoli>(ObjectFactory._AutorizzazioneRuoli_QNAME, AutorizzazioneRuoli.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PortaDelegataLocalForward }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "porta-delegata-local-forward")
    public JAXBElement<PortaDelegataLocalForward> createPortaDelegataLocalForward(PortaDelegataLocalForward value) {
        return new JAXBElement<PortaDelegataLocalForward>(ObjectFactory._PortaDelegataLocalForward_QNAME, PortaDelegataLocalForward.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MtomProcessorFlow }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "mtom-processor-flow")
    public JAXBElement<MtomProcessorFlow> createMtomProcessorFlow(MtomProcessorFlow value) {
        return new JAXBElement<MtomProcessorFlow>(ObjectFactory._MtomProcessorFlow_QNAME, MtomProcessorFlow.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MtomProcessor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "mtom-processor")
    public JAXBElement<MtomProcessor> createMtomProcessor(MtomProcessor value) {
        return new JAXBElement<MtomProcessor>(ObjectFactory._MtomProcessor_QNAME, MtomProcessor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidazioneContenutiApplicativi }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "validazione-contenuti-applicativi")
    public JAXBElement<ValidazioneContenutiApplicativi> createValidazioneContenutiApplicativi(ValidazioneContenutiApplicativi value) {
        return new JAXBElement<ValidazioneContenutiApplicativi>(ObjectFactory._ValidazioneContenutiApplicativi_QNAME, ValidazioneContenutiApplicativi.class, null, value);
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

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Create }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "create")
    public JAXBElement<Create> createCreate(Create value) {
        return new JAXBElement<Create>(ObjectFactory._Create_QNAME, Create.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "createResponse")
    public JAXBElement<CreateResponse> createCreateResponse(CreateResponse value) {
        return new JAXBElement<CreateResponse>(ObjectFactory._CreateResponse_QNAME, CreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Update }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "update")
    public JAXBElement<Update> createUpdate(Update value) {
        return new JAXBElement<Update>(ObjectFactory._Update_QNAME, Update.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "updateResponse")
    public JAXBElement<UpdateResponse> createUpdateResponse(UpdateResponse value) {
        return new JAXBElement<UpdateResponse>(ObjectFactory._UpdateResponse_QNAME, UpdateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateOrCreate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "updateOrCreate")
    public JAXBElement<UpdateOrCreate> createUpdateOrCreate(UpdateOrCreate value) {
        return new JAXBElement<UpdateOrCreate>(ObjectFactory._UpdateOrCreate_QNAME, UpdateOrCreate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateOrCreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "updateOrCreateResponse")
    public JAXBElement<UpdateOrCreateResponse> createUpdateOrCreateResponse(UpdateOrCreateResponse value) {
        return new JAXBElement<UpdateOrCreateResponse>(ObjectFactory._UpdateOrCreateResponse_QNAME, UpdateOrCreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteById }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "deleteById")
    public JAXBElement<DeleteById> createDeleteById(DeleteById value) {
        return new JAXBElement<DeleteById>(ObjectFactory._DeleteById_QNAME, DeleteById.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteByIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "deleteByIdResponse")
    public JAXBElement<DeleteByIdResponse> createDeleteByIdResponse(DeleteByIdResponse value) {
        return new JAXBElement<DeleteByIdResponse>(ObjectFactory._DeleteByIdResponse_QNAME, DeleteByIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "deleteAll")
    public JAXBElement<DeleteAll> createDeleteAll(DeleteAll value) {
        return new JAXBElement<DeleteAll>(ObjectFactory._DeleteAll_QNAME, DeleteAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "deleteAllResponse")
    public JAXBElement<DeleteAllResponse> createDeleteAllResponse(DeleteAllResponse value) {
        return new JAXBElement<DeleteAllResponse>(ObjectFactory._DeleteAllResponse_QNAME, DeleteAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAllByFilter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "deleteAllByFilter")
    public JAXBElement<DeleteAllByFilter> createDeleteAllByFilter(DeleteAllByFilter value) {
        return new JAXBElement<DeleteAllByFilter>(ObjectFactory._DeleteAllByFilter_QNAME, DeleteAllByFilter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAllByFilterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "deleteAllByFilterResponse")
    public JAXBElement<DeleteAllByFilterResponse> createDeleteAllByFilterResponse(DeleteAllByFilterResponse value) {
        return new JAXBElement<DeleteAllByFilterResponse>(ObjectFactory._DeleteAllByFilterResponse_QNAME, DeleteAllByFilterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Delete }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "delete")
    public JAXBElement<Delete> createDelete(Delete value) {
        return new JAXBElement<Delete>(ObjectFactory._Delete_QNAME, Delete.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/config/management", name = "deleteResponse")
    public JAXBElement<DeleteResponse> createDeleteResponse(DeleteResponse value) {
        return new JAXBElement<DeleteResponse>(ObjectFactory._DeleteResponse_QNAME, DeleteResponse.class, null, value);
    }

}
