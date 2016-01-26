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

package org.openspcoop2.core.diagnostica.ws.client.messaggiodiagnostico.search;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.diagnostica.ws.client.messaggiodiagnostico.search package. 
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

    private final static QName _DominioDiagnostico_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "dominio-diagnostico");
    private final static QName _Find_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "find");
    private final static QName _FindAllResponse_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "findAllResponse");
    private final static QName _DiagnosticaNotAuthorizedException_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "diagnostica-not-authorized-exception");
    private final static QName _Soggetto_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "soggetto");
    private final static QName _FindResponse_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "findResponse");
    private final static QName _DominioSoggetto_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "dominio-soggetto");
    private final static QName _DiagnosticaMultipleResultException_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "diagnostica-multiple-result-exception");
    private final static QName _SearchFilterMessaggioDiagnostico_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "search-filter-messaggio-diagnostico");
    private final static QName _FiltroInformazioneProtocollo_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "filtro-informazione-protocollo");
    private final static QName _DiagnosticaNotImplementedException_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "diagnostica-not-implemented-exception");
    private final static QName _DiagnosticaServiceException_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "diagnostica-service-exception");
    private final static QName _SoggettoIdentificativo_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "soggetto-identificativo");
    private final static QName _Servizio_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "servizio");
    private final static QName _Count_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "count");
    private final static QName _Protocollo_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "protocollo");
    private final static QName _DiagnosticaNotFoundException_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "diagnostica-not-found-exception");
    private final static QName _FindAll_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "findAll");
    private final static QName _CountResponse_QNAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "countResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.diagnostica.ws.client.messaggiodiagnostico.search
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
     * Create an instance of {@link DiagnosticaNotFoundException }
     * 
     */
    public DiagnosticaNotFoundException createDiagnosticaNotFoundException() {
        return new DiagnosticaNotFoundException();
    }

    /**
     * Create an instance of {@link FindAll }
     * 
     */
    public FindAll createFindAll() {
        return new FindAll();
    }

    /**
     * Create an instance of {@link FiltroInformazioneProtocollo }
     * 
     */
    public FiltroInformazioneProtocollo createFiltroInformazioneProtocollo() {
        return new FiltroInformazioneProtocollo();
    }

    /**
     * Create an instance of {@link DiagnosticaNotImplementedException }
     * 
     */
    public DiagnosticaNotImplementedException createDiagnosticaNotImplementedException() {
        return new DiagnosticaNotImplementedException();
    }

    /**
     * Create an instance of {@link DiagnosticaServiceException }
     * 
     */
    public DiagnosticaServiceException createDiagnosticaServiceException() {
        return new DiagnosticaServiceException();
    }

    /**
     * Create an instance of {@link SoggettoIdentificativo }
     * 
     */
    public SoggettoIdentificativo createSoggettoIdentificativo() {
        return new SoggettoIdentificativo();
    }

    /**
     * Create an instance of {@link Servizio }
     * 
     */
    public Servizio createServizio() {
        return new Servizio();
    }

    /**
     * Create an instance of {@link SearchFilterMessaggioDiagnostico }
     * 
     */
    public SearchFilterMessaggioDiagnostico createSearchFilterMessaggioDiagnostico() {
        return new SearchFilterMessaggioDiagnostico();
    }

    /**
     * Create an instance of {@link FindResponse }
     * 
     */
    public FindResponse createFindResponse() {
        return new FindResponse();
    }

    /**
     * Create an instance of {@link DominioSoggetto }
     * 
     */
    public DominioSoggetto createDominioSoggetto() {
        return new DominioSoggetto();
    }

    /**
     * Create an instance of {@link Soggetto }
     * 
     */
    public Soggetto createSoggetto() {
        return new Soggetto();
    }

    /**
     * Create an instance of {@link DiagnosticaNotAuthorizedException }
     * 
     */
    public DiagnosticaNotAuthorizedException createDiagnosticaNotAuthorizedException() {
        return new DiagnosticaNotAuthorizedException();
    }

    /**
     * Create an instance of {@link DiagnosticaMultipleResultException }
     * 
     */
    public DiagnosticaMultipleResultException createDiagnosticaMultipleResultException() {
        return new DiagnosticaMultipleResultException();
    }

    /**
     * Create an instance of {@link DominioDiagnostico }
     * 
     */
    public DominioDiagnostico createDominioDiagnostico() {
        return new DominioDiagnostico();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DominioDiagnostico }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "dominio-diagnostico")
    public JAXBElement<DominioDiagnostico> createDominioDiagnostico(DominioDiagnostico value) {
        return new JAXBElement<DominioDiagnostico>(ObjectFactory._DominioDiagnostico_QNAME, DominioDiagnostico.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Find }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "find")
    public JAXBElement<Find> createFind(Find value) {
        return new JAXBElement<Find>(ObjectFactory._Find_QNAME, Find.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "findAllResponse")
    public JAXBElement<FindAllResponse> createFindAllResponse(FindAllResponse value) {
        return new JAXBElement<FindAllResponse>(ObjectFactory._FindAllResponse_QNAME, FindAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DiagnosticaNotAuthorizedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "diagnostica-not-authorized-exception")
    public JAXBElement<DiagnosticaNotAuthorizedException> createDiagnosticaNotAuthorizedException(DiagnosticaNotAuthorizedException value) {
        return new JAXBElement<DiagnosticaNotAuthorizedException>(ObjectFactory._DiagnosticaNotAuthorizedException_QNAME, DiagnosticaNotAuthorizedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Soggetto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "soggetto")
    public JAXBElement<Soggetto> createSoggetto(Soggetto value) {
        return new JAXBElement<Soggetto>(ObjectFactory._Soggetto_QNAME, Soggetto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "findResponse")
    public JAXBElement<FindResponse> createFindResponse(FindResponse value) {
        return new JAXBElement<FindResponse>(ObjectFactory._FindResponse_QNAME, FindResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DominioSoggetto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "dominio-soggetto")
    public JAXBElement<DominioSoggetto> createDominioSoggetto(DominioSoggetto value) {
        return new JAXBElement<DominioSoggetto>(ObjectFactory._DominioSoggetto_QNAME, DominioSoggetto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DiagnosticaMultipleResultException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "diagnostica-multiple-result-exception")
    public JAXBElement<DiagnosticaMultipleResultException> createDiagnosticaMultipleResultException(DiagnosticaMultipleResultException value) {
        return new JAXBElement<DiagnosticaMultipleResultException>(ObjectFactory._DiagnosticaMultipleResultException_QNAME, DiagnosticaMultipleResultException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchFilterMessaggioDiagnostico }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "search-filter-messaggio-diagnostico")
    public JAXBElement<SearchFilterMessaggioDiagnostico> createSearchFilterMessaggioDiagnostico(SearchFilterMessaggioDiagnostico value) {
        return new JAXBElement<SearchFilterMessaggioDiagnostico>(ObjectFactory._SearchFilterMessaggioDiagnostico_QNAME, SearchFilterMessaggioDiagnostico.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FiltroInformazioneProtocollo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "filtro-informazione-protocollo")
    public JAXBElement<FiltroInformazioneProtocollo> createFiltroInformazioneProtocollo(FiltroInformazioneProtocollo value) {
        return new JAXBElement<FiltroInformazioneProtocollo>(ObjectFactory._FiltroInformazioneProtocollo_QNAME, FiltroInformazioneProtocollo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DiagnosticaNotImplementedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "diagnostica-not-implemented-exception")
    public JAXBElement<DiagnosticaNotImplementedException> createDiagnosticaNotImplementedException(DiagnosticaNotImplementedException value) {
        return new JAXBElement<DiagnosticaNotImplementedException>(ObjectFactory._DiagnosticaNotImplementedException_QNAME, DiagnosticaNotImplementedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DiagnosticaServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "diagnostica-service-exception")
    public JAXBElement<DiagnosticaServiceException> createDiagnosticaServiceException(DiagnosticaServiceException value) {
        return new JAXBElement<DiagnosticaServiceException>(ObjectFactory._DiagnosticaServiceException_QNAME, DiagnosticaServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SoggettoIdentificativo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "soggetto-identificativo")
    public JAXBElement<SoggettoIdentificativo> createSoggettoIdentificativo(SoggettoIdentificativo value) {
        return new JAXBElement<SoggettoIdentificativo>(ObjectFactory._SoggettoIdentificativo_QNAME, SoggettoIdentificativo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Servizio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "servizio")
    public JAXBElement<Servizio> createServizio(Servizio value) {
        return new JAXBElement<Servizio>(ObjectFactory._Servizio_QNAME, Servizio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Count }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "count")
    public JAXBElement<Count> createCount(Count value) {
        return new JAXBElement<Count>(ObjectFactory._Count_QNAME, Count.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Protocollo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "protocollo")
    public JAXBElement<Protocollo> createProtocollo(Protocollo value) {
        return new JAXBElement<Protocollo>(ObjectFactory._Protocollo_QNAME, Protocollo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DiagnosticaNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "diagnostica-not-found-exception")
    public JAXBElement<DiagnosticaNotFoundException> createDiagnosticaNotFoundException(DiagnosticaNotFoundException value) {
        return new JAXBElement<DiagnosticaNotFoundException>(ObjectFactory._DiagnosticaNotFoundException_QNAME, DiagnosticaNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "findAll")
    public JAXBElement<FindAll> createFindAll(FindAll value) {
        return new JAXBElement<FindAll>(ObjectFactory._FindAll_QNAME, FindAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "countResponse")
    public JAXBElement<CountResponse> createCountResponse(CountResponse value) {
        return new JAXBElement<CountResponse>(ObjectFactory._CountResponse_QNAME, CountResponse.class, null, value);
    }

}
