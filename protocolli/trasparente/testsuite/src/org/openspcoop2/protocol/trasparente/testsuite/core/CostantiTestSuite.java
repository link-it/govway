/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.trasparente.testsuite.core;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * Costanti utilizzate nelle units test.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CostantiTestSuite {

	
	/** Protocollo */
	public static final String PROTOCOL_NAME = "trasparente";
	
	private static IProtocolFactory<?> protocolFactory = null;
	static {
		try {
			protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(PROTOCOL_NAME);
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
	private static String getIdentificativoPortaDefault(String nome) {
		try {
			return protocolFactory.createTraduttore().getIdentificativoPortaDefault(new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, nome));
		}catch(Exception e) {
			e.printStackTrace(System.out);
			return "!!ERRORE!!";
		}
	}
	
	
	/** ENTITA Profili Protocollo */
	public static final String PROXY_PROFILO_COLLABORAZIONE_ONEWAY=ProfiloDiCollaborazione.ONEWAY.getEngineValue();
	public static final String PROXY_PROFILO_COLLABORAZIONE_SINCRONO=ProfiloDiCollaborazione.SINCRONO.getEngineValue();
	
	/** ENTITA Profili Trasmissione */
	public static final String PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI=Inoltro.CON_DUPLICATI.getEngineValue();
	public static final String PROXY_PROFILO_TRASMISSIONE_SENZA_DUPLICATI=Inoltro.SENZA_DUPLICATI.getEngineValue();
	
	
	
	/** Porte Delegate per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_NON_AUTENTICATO="APIMinisteroFruitore/APIMinisteroErogatoreEsterno/SOAPOnewayStateful/notifica";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_NON_AUTENTICATO="APIMinisteroFruitore/APIMinisteroErogatoreEsterno/SOAPOnewayStateless/notifica";
	
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_NON_AUTENTICATO="APIMinisteroFruitore/APIMinisteroErogatoreEsterno/SOAPSincronoStateful/aggiornamento";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_NON_AUTENTICATO="APIMinisteroFruitore/APIMinisteroErogatoreEsterno/SOAPSincronoStateless/aggiornamento";

	
	/**
	 * Credenziali per l'accesso ai servizi autenticati
	 */
	public static final String USERNAME_PORTA_DELEGATA_ONEWAY_AUTENTICATA="esempioFruitoreTrasparente";
	public static final String PASSWORD_PORTA_DELEGATA_ONEWAY_AUTENTICATA="123456";
	public static final String USERNAME_PORTA_DELEGATA_SINCRONO_AUTENTICATA="esempioFruitoreTrasparente";
	public static final String PASSWORD_PORTA_DELEGATA_SINCRONO_AUTENTICATA="123456";
	public static final String USERNAME_PORTA_DELEGATA_MTOM="MinisteroFruitore";
	public static final String PASSWORD_PORTA_DELEGATA_MTOM="123456";

	
	/** Porte Delegate per il test dei profili di collaborazione: OneWay Autenticato */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_AUTENTICATO="APIMinisteroFruitore/APIMinisteroErogatoreEsterno/SOAPOnewayStatefulAutenticato/notifica";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_AUTENTICATO="APIMinisteroFruitore/APIMinisteroErogatoreEsterno/SOAPOnewayStatelessAutenticato/notifica";
	
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono Autenticato */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_AUTENTICATO="APIMinisteroFruitore/APIMinisteroErogatoreEsterno/SOAPSincronoStatefulAutenticato/aggiornamento";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_AUTENTICATO="APIMinisteroFruitore/APIMinisteroErogatoreEsterno/SOAPSincronoStatelessAutenticato/aggiornamento";

	/** Porte Delegate per il test dei profili di collaborazione: OneWay Local Forward */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_LOCAL_FORWARD="APIMinisteroFruitore/APIMinisteroErogatore/SOAPOnewayStatefulLocalForward/notificaLocalForward";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_LOCAL_FORWARD="APIMinisteroFruitore/APIMinisteroErogatore/SOAPOnewayStatelessLocalForward/notificaLocalForward";

	/** Porte Delegate per il test dei profili di collaborazione: Sincrono Local Forward */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_LOCAL_FORWARD="APIMinisteroFruitore/APIMinisteroErogatore/SOAPSincronoStatefulLocalForward/aggiornamentoLocalForward";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_LOCAL_FORWARD="APIMinisteroFruitore/APIMinisteroErogatore/SOAPSincronoStatelessLocalForward/aggiornamentoLocalForward";

	/** Porte Delegate per il test dei profili di collaborazione: OneWay Stateful SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT11_200="APIClientSoapFault11/APIMinisteroErogatoreEsterno/SOAPOnewayStateful";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT12_200="APIClientSoapFault12/APIMinisteroErogatoreEsterno/SOAPOnewayStateful";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT11_500="APIClientSoapFault11500/APIMinisteroErogatoreEsterno/SOAPOnewayStateful";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT12_500="APIClientSoapFault12500/APIMinisteroErogatoreEsterno/SOAPOnewayStateful";

	/** Porte Delegate per il test dei profili di collaborazione: Sincrono Stateful SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT11_200="APIClientSoapFault11/APIMinisteroErogatoreEsterno/SOAPSincronoStateful";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT12_200="APIClientSoapFault12/APIMinisteroErogatoreEsterno/SOAPSincronoStateful";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT11_500="APIClientSoapFault11500/APIMinisteroErogatoreEsterno/SOAPSincronoStateful";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT12_500="APIClientSoapFault12500/APIMinisteroErogatoreEsterno/SOAPSincronoStateful";
	
	/** Porte Delegate per il test dei profili di collaborazione: OneWay Stateless SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT11_200="APIClientSoapFault11/APIMinisteroErogatoreEsterno/SOAPOnewayStateless";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT12_200="APIClientSoapFault12/APIMinisteroErogatoreEsterno/SOAPOnewayStateless";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT11_500="APIClientSoapFault11500/APIMinisteroErogatoreEsterno/SOAPOnewayStateless";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT12_500="APIClientSoapFault12500/APIMinisteroErogatoreEsterno/SOAPOnewayStateless";

	/** Porte Delegate per il test dei profili di collaborazione: Sincrono Stateless SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT11_200="APIClientSoapFault11/APIMinisteroErogatoreEsterno/SOAPSincronoStateless";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT12_200="APIClientSoapFault12/APIMinisteroErogatoreEsterno/SOAPSincronoStateless";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT11_500="APIClientSoapFault11500/APIMinisteroErogatoreEsterno/SOAPSincronoStateless";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT12_500="APIClientSoapFault12500/APIMinisteroErogatoreEsterno/SOAPSincronoStateless";
	
	/** Porte Delegate per il test MTOM */
	public static final String PORTA_DELEGATA_MTOM_SOAP11="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP11/echo";
	public static final String PORTA_DELEGATA_MTOM_SOAP12="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP12/echo";
	public static final String PORTA_DELEGATA_MTOM_SOAP11_VALIDAZIONE="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP11/validazione";
	public static final String PORTA_DELEGATA_MTOM_SOAP12_VALIDAZIONE="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP12/validazione";
	public static final String PORTA_DELEGATA_MTOM_SOAP11_PACKAGE_UNPACKAGE="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP11/packageUnpackage";
	public static final String PORTA_DELEGATA_MTOM_SOAP12_PACKAGE_UNPACKAGE="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP12/packageUnpackage";
	public static final String PORTA_DELEGATA_MTOM_SOAP11_UNPACKAGE_PACKAGE="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP11/unpackagePackage";
	public static final String PORTA_DELEGATA_MTOM_SOAP12_UNPACKAGE_PACKAGE="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP12/unpackagePackage";
	public static final String PORTA_DELEGATA_MTOM_SOAP11_VERIFY_OK="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP11/verifyOk";
	public static final String PORTA_DELEGATA_MTOM_SOAP12_VERIFY_OK="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP12/verifyOk";
	public static final String PORTA_DELEGATA_MTOM_SOAP11_VERIFY_KO_REQUEST="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP11/verifyKoRequest";
	public static final String PORTA_DELEGATA_MTOM_SOAP12_VERIFY_KO_REQUEST="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP12/verifyKoRequest";
	public static final String PORTA_DELEGATA_MTOM_SOAP11_VERIFY_KO_RESPONSE="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP11/verifyKoResponse";
	public static final String PORTA_DELEGATA_MTOM_SOAP12_VERIFY_KO_RESPONSE="APIMinisteroFruitore/APIMinisteroErogatore/SOAPMTOMServiceExampleSOAP12/verifyKoResponse";
	
	/** Porte Delegate per autenticazione */
	public static final String PORTA_DELEGATA_AUTH_BASIC = "AuthenticationBasic";
	public static final String PORTA_DELEGATA_AUTH_SSL = "AuthenticationSsl";
	public static final String PORTA_DELEGATA_AUTH_PRINCIPAL = "AuthenticationPrincipal";
	public static final String PORTA_DELEGATA_AUTH_OPTIONAL_BASIC = "AuthenticationOptionalBasic";
	public static final String PORTA_DELEGATA_AUTH_OPTIONAL_SSL = "AuthenticationOptionalSsl";
	public static final String PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL = "AuthenticationOptionalPrincipal";
	
	/** Porte Delegate per autorizzazione */
	public static final String PORTA_DELEGATA_AUTHZ_AUTHENTICATED = "AuthorizationAuthenticated";
	public static final String PORTA_DELEGATA_AUTHZ_ROLES_ALL = "AuthorizationRolesAll";
	public static final String PORTA_DELEGATA_AUTHZ_ROLES_ANY = "AuthorizationRolesAny";
	public static final String PORTA_DELEGATA_AUTHZ_INTERNAL_ROLES_ALL = "AuthorizationInternalRolesAll";
	public static final String PORTA_DELEGATA_AUTHZ_INTERNAL_ROLES_ANY = "AuthorizationInternalRolesAny";
	public static final String PORTA_DELEGATA_AUTHZ_EXTERNAL_ROLES_ALL = "AuthorizationExternalRolesAll";
	public static final String PORTA_DELEGATA_AUTHZ_EXTERNAL_ROLES_ANY = "AuthorizationExternalRolesAny";
	public static final String PORTA_DELEGATA_AUTHZ_EXTERNAL_ROLES_ALL_NO_AUTHENTICATION = "AuthorizationExternalRolesAll_noAuthentication";
	public static final String PORTA_DELEGATA_AUTHZ_EXTERNAL_ROLES_ANY_NO_AUTHENTICATION = "AuthorizationExternalRolesAny_noAuthentication";
	public static final String PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_ROLES_ALL = "AuthorizationAuthenticatedOrRolesAll";
	public static final String PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_ROLES_ANY = "AuthorizationAuthenticatedOrRolesAny";
	public static final String PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_INTERNAL_ROLES_ALL = "AuthorizationAuthenticatedOrInternalRolesAll";
	public static final String PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_INTERNAL_ROLES_ANY = "AuthorizationAuthenticatedOrInternalRolesAny";
	public static final String PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_EXTERNAL_ROLES_ALL = "AuthorizationAuthenticatedOrExternalRolesAll";
	public static final String PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_EXTERNAL_ROLES_ANY = "AuthorizationAuthenticatedOrExternalRolesAny";
	public static final String PORTA_DELEGATA_AUTHZ_XACML_POLICY = "AuthorizationXacmlPolicy";
	public static final String PORTA_DELEGATA_AUTHZ_INTERNAL_XACML_POLICY = "AuthorizationInternalXacmlPolicy";
	public static final String PORTA_DELEGATA_AUTHZ_EXTERNAL_XACML_POLICY = "AuthorizationExternalXacmlPolicy";
	public static final String PORTA_DELEGATA_AUTHZ_EXTERNAL_XACML_POLICY_NO_AUTHENTICATION = "AuthorizationExternalXacmlPolicy_noAuthentication";
	
	/** Porte Delegate per il test dei profili di collaborazione: API */
	public static final String PORTA_DELEGATA_REST_API="APIMinisteroFruitore/APIMinisteroErogatore/RESTAPI";

	/** Porte Delegate per il test dei profili di collaborazione: API */
	public static final String PORTA_DELEGATA_REST_API_LOCAL_FORWARD="APIMinisteroFruitore/APIMinisteroErogatore/RESTAPI_viaLocalForward";
	
	/** Porte Delegate per il test degli header Authorization e WWW-Authenticate: API */
	public static final String PORTA_DELEGATA_REST_BASIC_PDD_SERVICE_WITH_BASIC_AUTH="AuthenticationREST_basic";
	public static final String PORTA_DELEGATA_REST_BASIC_PDD_SERVICE_WITH_BASIC_AUTH_FORWARD_CREDENTIALS="AuthenticationREST_basic_forwardCredentials";
	public static final String PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH="AuthenticationREST_serviceWithBasicAuth";
	public static final String PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH_DOMAIN="AuthenticationREST_serviceWithBasicAuthDomain";
	
	/** Porte Delegate per il test degli header CORS */
	public static final String PORTA_DELEGATA_REST_CORS="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaREST";
	public static final String PORTA_DELEGATA_SOAP_CORS="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaSOAP";
	public static final String PORTA_DELEGATA_REST_CORS_ALLORIGINS="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaREST_allOrigins";
	public static final String PORTA_DELEGATA_SOAP_CORS_ALLORIGINS="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaSOAP_allOrigins";
	public static final String PORTA_DELEGATA_REST_CORS_ALLORIGINS_CREDENTIALS="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaREST_allOrigins_credentials";
	public static final String PORTA_DELEGATA_SOAP_CORS_ALLORIGINS_CREDENTIALS="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaSOAP_allOrigins_credentials";
	public static final String PORTA_DELEGATA_REST_CORS_EXPOSE="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaREST_expose";
	public static final String PORTA_DELEGATA_SOAP_CORS_EXPOSE="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaSOAP_expose";
	public static final String PORTA_DELEGATA_REST_CORS_MAXAGE_DISABLED="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaREST_maxAge_disabled";
	public static final String PORTA_DELEGATA_SOAP_CORS_MAXAGE_DISABLED="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaSOAP_maxAge_disabled";
	public static final String PORTA_DELEGATA_REST_CORS_MAXAGE="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaREST_maxAge";
	public static final String PORTA_DELEGATA_SOAP_CORS_MAXAGE="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaSOAP_maxAge";

	public static final String PORTA_DELEGATA_REST_CORS_TRASPARENTE="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaRESTtrasparente";
	public static final String PORTA_DELEGATA_SOAP_CORS_TRASPARENTE="APIMinisteroFruitore/APIMinisteroErogatore/gwCORSviaSOAPtrasparente";

	
	

	/** Porte Applicative per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_NON_AUTENTICATO="NOAUTH_APIMinisteroErogatore/SOAPOnewayStateful";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_NON_AUTENTICATO="NOAUTH_APIMinisteroErogatore/SOAPOnewayStateless";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_AUTENTICATO="AUTH_APIMinisteroErogatore/SOAPOnewayStateful";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_AUTENTICATO="AUTH_APIMinisteroErogatore/SOAPOnewayStateless";
	
	/** Porte Applicative per il test dei profili di collaborazione: Sincrono */
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_NON_AUTENTICATO="NOAUTH_APIMinisteroErogatore/SOAPSincronoStateful";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_NON_AUTENTICATO="NOAUTH_APIMinisteroErogatore/SOAPSincronoStateless";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_AUTENTICATO="AUTH_APIMinisteroErogatore/SOAPSincronoStateful";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_AUTENTICATO="AUTH_APIMinisteroErogatore/SOAPSincronoStateless";

	
	/**
	 * Credenziali per l'accesso ai servizi autenticati
	 */
	public static final String USERNAME_PORTA_APPLICATIVA_ONEWAY_AUTENTICATA="applicativoComunePisa";
	public static final String PASSWORD_PORTA_APPLICATIVA_ONEWAY_AUTENTICATA="123456";
	public static final String USERNAME_PORTA_APPLICATIVA_SINCRONO_AUTENTICATA="applicativoComunePisa";
	public static final String PASSWORD_PORTA_APPLICATIVA_SINCRONO_AUTENTICATA="123456";

	
	/** Porte Applicative per il test dei profili di collaborazione: OneWay Stateful SOAP Fault */
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT11_200="APIMinisteroErogatore/SOAPOneway/gestioneSOAPFault11Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT12_200="APIMinisteroErogatore/SOAPOneway/gestioneSOAPFault12Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT11_500="APIMinisteroErogatore/SOAPOneway/gestioneSOAPFault11500Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT12_500="APIMinisteroErogatore/SOAPOneway/gestioneSOAPFault12500Stateful";

	/** Porte Applicative per il test dei profili di collaborazione: Sincrono Stateful SOAP Fault */
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT11_200="APIMinisteroErogatore/SOAPSincrono/gestioneSOAPFault11Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT12_200="APIMinisteroErogatore/SOAPSincrono/gestioneSOAPFault12Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT11_500="APIMinisteroErogatore/SOAPSincrono/gestioneSOAPFault11500Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT12_500="APIMinisteroErogatore/SOAPSincrono/gestioneSOAPFault12500Stateful";
	
	/** Porte Applicative per il test dei profili di collaborazione: OneWay Stateless SOAP Fault */
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT11_200="APIMinisteroErogatore/SOAPOneway/gestioneSOAPFault11Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT12_200="APIMinisteroErogatore/SOAPOneway/gestioneSOAPFault12Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT11_500="APIMinisteroErogatore/SOAPOneway/gestioneSOAPFault11500Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT12_500="APIMinisteroErogatore/SOAPOneway/gestioneSOAPFault12500Stateless";

	/** Porte Applicative per il test dei profili di collaborazione: Sincrono Stateless SOAP Fault */
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT11_200="APIMinisteroErogatore/SOAPSincrono/gestioneSOAPFault11Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT12_200="APIMinisteroErogatore/SOAPSincrono/gestioneSOAPFault12Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT11_500="APIMinisteroErogatore/SOAPSincrono/gestioneSOAPFault11500Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT12_500="APIMinisteroErogatore/SOAPSincrono/gestioneSOAPFault12500Stateless";
	
	/** Porte Applicative per il test MTOM */
	public static final String PORTA_APPLICATIVA_MTOM_SOAP11="MTOMServiceExampleSOAP11";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP12="MTOMServiceExampleSOAP12";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP11_VALIDAZIONE="MTOMServiceExampleSOAP11/validazione";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP12_VALIDAZIONE="MTOMServiceExampleSOAP12/validazione";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP11_PACKAGE_UNPACKAGE="MTOMServiceExampleSOAP11/packageUnpackage";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP12_PACKAGE_UNPACKAGE="MTOMServiceExampleSOAP12/packageUnpackage";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP11_UNPACKAGE_PACKAGE="MTOMServiceExampleSOAP11/unpackagePackage";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP12_UNPACKAGE_PACKAGE="MTOMServiceExampleSOAP12/unpackagePackage";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP11_VERIFY_OK="MTOMServiceExampleSOAP11/verifyOk";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP12_VERIFY_OK="MTOMServiceExampleSOAP12/verifyOk";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP11_VERIFY_KO_REQUEST="MTOMServiceExampleSOAP11/verifyKoRequest";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP12_VERIFY_KO_REQUEST="MTOMServiceExampleSOAP12/verifyKoRequest";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP11_VERIFY_KO_RESPONSE="MTOMServiceExampleSOAP11/verifyKoResponse";
	public static final String PORTA_APPLICATIVA_MTOM_SOAP12_VERIFY_KO_RESPONSE="MTOMServiceExampleSOAP12/verifyKoResponse";

	public static final String USERNAME_PORTA_APPLICATIVA_MTOM="MinisteroFruitore";
	public static final String PASSWORD_PORTA_APPLICATIVA_MTOM="123456";
	
	/** Porte Applicative per autenticazione */
	public static final String PORTA_APPLICATIVA_AUTH_NONE = "AuthenticationNone";
	public static final String PORTA_APPLICATIVA_AUTH_BASIC = "AuthenticationBasic";
	public static final String PORTA_APPLICATIVA_AUTH_SSL = "AuthenticationSsl";
	public static final String PORTA_APPLICATIVA_AUTH_PRINCIPAL = "AuthenticationPrincipal";
	public static final String PORTA_APPLICATIVA_AUTH_OPTIONAL_BASIC = "AuthenticationOptionalBasic";
	public static final String PORTA_APPLICATIVA_AUTH_OPTIONAL_SSL = "AuthenticationOptionalSsl";
	public static final String PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL = "AuthenticationOptionalPrincipal";
	
	/** Porte Applicative per autorizzazione */
	public static final String PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED = "AuthorizationAuthenticated";
	public static final String PORTA_APPLICATIVA_AUTHZ_ROLES_ALL = "AuthorizationRolesAll";
	public static final String PORTA_APPLICATIVA_AUTHZ_ROLES_ANY = "AuthorizationRolesAny";
	public static final String PORTA_APPLICATIVA_AUTHZ_INTERNAL_ROLES_ALL = "AuthorizationInternalRolesAll";
	public static final String PORTA_APPLICATIVA_AUTHZ_INTERNAL_ROLES_ANY = "AuthorizationInternalRolesAny";
	public static final String PORTA_APPLICATIVA_AUTHZ_EXTERNAL_ROLES_ALL = "AuthorizationExternalRolesAll";
	public static final String PORTA_APPLICATIVA_AUTHZ_EXTERNAL_ROLES_ANY = "AuthorizationExternalRolesAny";
	public static final String PORTA_APPLICATIVA_AUTHZ_EXTERNAL_ROLES_ALL_NO_AUTHENTICATION = "AuthorizationExternalRolesAll_noAuthentication";
	public static final String PORTA_APPLICATIVA_AUTHZ_EXTERNAL_ROLES_ANY_NO_AUTHENTICATION = "AuthorizationExternalRolesAny_noAuthentication";
	public static final String PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_ROLES_ALL = "AuthorizationAuthenticatedOrRolesAll";
	public static final String PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_ROLES_ANY = "AuthorizationAuthenticatedOrRolesAny";
	public static final String PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_INTERNAL_ROLES_ALL = "AuthorizationAuthenticatedOrInternalRolesAll";
	public static final String PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_INTERNAL_ROLES_ANY = "AuthorizationAuthenticatedOrInternalRolesAny";
	public static final String PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_EXTERNAL_ROLES_ALL = "AuthorizationAuthenticatedOrExternalRolesAll";
	public static final String PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_EXTERNAL_ROLES_ANY = "AuthorizationAuthenticatedOrExternalRolesAny";
	public static final String PORTA_APPLICATIVA_AUTHZ_XACML_POLICY = "AuthorizationXacmlPolicy";
	public static final String PORTA_APPLICATIVA_AUTHZ_INTERNAL_XACML_POLICY = "AuthorizationInternalXacmlPolicy";
	public static final String PORTA_APPLICATIVA_AUTHZ_EXTERNAL_XACML_POLICY = "AuthorizationExternalXacmlPolicy";
	public static final String PORTA_APPLICATIVA_AUTHZ_EXTERNAL_XACML_POLICY_NO_AUTHENTICATION = "AuthorizationExternalXacmlPolicy_noAuthentication";
	

	/** Porte Applicative per il test dei profili di collaborazione: API */
	public static final String PORTA_APPLICATIVA_REST_API="APIMinisteroErogatore/RESTAPI";
	
	/** Porte Applicative per il test degli header Authorization e WWW-Authenticate: API */
	public static final String PORTA_APPLICATIVA_REST_BASIC_PDD_SERVICE_WITH_BASIC_AUTH="AuthenticationREST_basic";
	public static final String PORTA_APPLICATIVA_REST_BASIC_PDD_SERVICE_WITH_BASIC_AUTH_FORWARD_CREDENTIALS="AuthenticationREST_basic_forwardCredentials";
	public static final String PORTA_APPLICATIVA_REST_SERVICE_WITH_BASIC_AUTH="AuthenticationREST_serviceWithBasicAuth";
	public static final String PORTA_APPLICATIVA_REST_SERVICE_WITH_BASIC_AUTH_DOMAIN="AuthenticationREST_serviceWithBasicAuthDomain";
	
	/** Porte Applicative per il test degli header CORS */
	public static final String PORTA_APPLICATIVA_REST_CORS="APIMinisteroErogatore/gwCORSviaREST";
	public static final String PORTA_APPLICATIVA_SOAP_CORS="APIMinisteroErogatore/gwCORSviaSOAP";
	public static final String PORTA_APPLICATIVA_REST_CORS_ALLORIGINS="APIMinisteroErogatore/gwCORSviaREST_allOrigins";
	public static final String PORTA_APPLICATIVA_SOAP_CORS_ALLORIGINS="APIMinisteroErogatore/gwCORSviaSOAP_allOrigins";
	public static final String PORTA_APPLICATIVA_REST_CORS_ALLORIGINS_CREDENTIALS="APIMinisteroErogatore/gwCORSviaREST_allOrigins_credentials";
	public static final String PORTA_APPLICATIVA_SOAP_CORS_ALLORIGINS_CREDENTIALS="APIMinisteroErogatore/gwCORSviaSOAP_allOrigins_credentials";
	public static final String PORTA_APPLICATIVA_REST_CORS_EXPOSE="APIMinisteroErogatore/gwCORSviaREST_expose";
	public static final String PORTA_APPLICATIVA_SOAP_CORS_EXPOSE="APIMinisteroErogatore/gwCORSviaSOAP_expose";
	public static final String PORTA_APPLICATIVA_REST_CORS_MAXAGE_DISABLED="APIMinisteroErogatore/gwCORSviaREST_maxAge_disabled";
	public static final String PORTA_APPLICATIVA_SOAP_CORS_MAXAGE_DISABLED="APIMinisteroErogatore/gwCORSviaSOAP_maxAge_disabled";
	public static final String PORTA_APPLICATIVA_REST_CORS_MAXAGE="APIMinisteroErogatore/gwCORSviaREST_maxAge";
	public static final String PORTA_APPLICATIVA_SOAP_CORS_MAXAGE="APIMinisteroErogatore/gwCORSviaSOAP_maxAge";
	
	
	
	public static final String PORTA_APPLICATIVA_REST_CORS_TRASPARENTE="APIMinisteroErogatore/gwCORSviaRESTtrasparente";
	public static final String PORTA_APPLICATIVA_SOAP_CORS_TRASPARENTE="APIMinisteroErogatore/gwCORSviaSOAPtrasparente";
	

	
		
	/** Tipo Soggetto */
	public static final String PROXY_TIPO_SOGGETTO="gw";
	
	/** IDSoggetto Anonimo*/
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_ANONIMO = null; // volutamente null
	
	/** Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT11500="ClientSoapFault11500";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11500=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_FRUITORE_FAULT11500);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT11500 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT11500, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11500);
	
	/** Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT11200="ClientSoapFault11";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11200=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_FRUITORE_FAULT11200);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT11200 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT11200, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11200);
	
	/** Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT12500="ClientSoapFault12500";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12500=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_FRUITORE_FAULT12500);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT12500 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT12500, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12500);
	
	/** Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT12200="ClientSoapFault12";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12200=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_FRUITORE_FAULT12200);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT12200 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT12200, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12200);
	
	/** Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE="MinisteroFruitore";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_FRUITORE);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE);
	
	/** Nome Soggetto Fruitore Anomino*/
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA="applicativoComunePisa";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA);
	/** IDSoggetto Autenticato Porta Applicativa */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA= new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA);
	
	/** Nome Soggetto Erogatore */
	public static final String PROXY_NOME_SOGGETTO_EROGATORE="MinisteroErogatore";
	/** IdPorta Soggetto Erogatore */
	public static final String PROXY_IDPORTA_SOGGETTO_EROGATORE=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_EROGATORE);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_EROGATORE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_EROGATORE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_EROGATORE);
	
	/** Nome Soggetto Erogatore Esterno */
	public static final String PROXY_NOME_SOGGETTO_EROGATORE_ESTERNO="MinisteroErogatoreEsterno";
	/** IdPorta Soggetto Erogatore Esterno */
	public static final String PROXY_IDPORTA_SOGGETTO_EROGATORE_ESTERNO=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_EROGATORE_ESTERNO);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_EROGATORE_ESTERNO = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_EROGATORE_ESTERNO, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_EROGATORE_ESTERNO);
	
	
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparenteBasic  */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_BASIC="EsempioSoggettoTrasparenteBasic";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparenteBasic */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_BASIC=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_BASIC);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparenteBasic */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_BASIC = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_BASIC, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_BASIC);
	
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparenteBasic2  */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_BASIC_2="EsempioSoggettoTrasparenteBasic2";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparenteBasic2 */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_BASIC_2=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_BASIC_2);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparenteBasic */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_BASIC_2 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_BASIC_2, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_BASIC_2);
	
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparenteBasic3  */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_BASIC_3="EsempioSoggettoTrasparenteBasic3";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparenteBasic3 */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_BASIC_3=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_BASIC_3);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparenteBasic */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_BASIC_3 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_BASIC_3, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_BASIC_3);
	
	
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparenteSsl  */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_SSL="EsempioSoggettoTrasparenteSsl";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparenteSsl */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_SSL=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_SSL);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparenteSsl */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_SSL = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_SSL, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_SSL);
	
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparenteSsl2  */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_SSL_2="EsempioSoggettoTrasparenteSsl2";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparenteSsl2 */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_SSL_2=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_SSL_2);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparenteSsl */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_SSL_2 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_SSL_2, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_SSL_2);
		
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparenteCert1  */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_CERT1="EsempioSoggettoTrasparenteCert1";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparenteSsl */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_CERT1=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_CERT1);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparenteSsl */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_CERT1 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_CERT1, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_CERT1);
	
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparenteCert1 - serialNumberDifferente */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_CERT1_SERIALNUMBERDIFFERENTE="EsempioSoggettoTrasparenteCert1_serialNumberDifferente";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparenteSsl */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_CERT1_SERIALNUMBERDIFFERENTE=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_CERT1_SERIALNUMBERDIFFERENTE);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparenteSsl */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_CERT1_SERIALNUMBERDIFFERENTE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_CERT1_SERIALNUMBERDIFFERENTE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_CERT1_SERIALNUMBERDIFFERENTE);
	
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparenteCert2  */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_CERT2="EsempioSoggettoTrasparenteCert2";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparenteSsl */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_CERT2=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_CERT2);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparenteSsl */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_CERT2 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_CERT2, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_CERT2);
	
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparenteCert2 - serialNumberDifferente */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_CERT2_SERIALNUMBERDIFFERENTE="EsempioSoggettoTrasparenteCert2_serialNumberDifferente";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparenteSsl */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_CERT2_SERIALNUMBERDIFFERENTE=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_CERT2_SERIALNUMBERDIFFERENTE);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparenteSsl */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_CERT2_SERIALNUMBERDIFFERENTE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_CERT2_SERIALNUMBERDIFFERENTE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_CERT2_SERIALNUMBERDIFFERENTE);
	
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparentePrincipal  */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_PRINCIPAL="EsempioSoggettoTrasparentePrincipal";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparentePrincipal */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_PRINCIPAL=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_PRINCIPAL);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparentePrincipal */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_PRINCIPAL, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_PRINCIPAL);
	
	/** ENTITA SPCOOP: Nome Soggetto EsempioSoggettoTrasparentePrincipal2  */
	public static final String PROXY_NOME_SOGGETTO_TRASPARENTE_PRINCIPAL_2="EsempioSoggettoTrasparentePrincipal2";
	/** ENTITA SPCOOP: IdPorta Soggetto EsempioSoggettoTrasparentePrincipal2 */
	public static final String PROXY_IDPORTA_SOGGETTO_TRASPARENTE_PRINCIPAL_2=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TRASPARENTE_PRINCIPAL_2);
	/** ENTITA SPCOOP: IDSoggetto EsempioSoggettoTrasparentePrincipal */
	public static final IDSoggetto PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TRASPARENTE_PRINCIPAL_2, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TRASPARENTE_PRINCIPAL_2);
		
	/** Nome Soggetto Erogatore */
	public static final String PROXY_NOME_SOGGETTO_TEST_CREDENZIALI_BRUCIATE="TestPerCredenzialiBruciateBasic";
	/** IdPorta Soggetto Erogatore */
	public static final String PROXY_IDPORTA_SOGGETTO_TEST_CREDENZIALI_BRUCIATE=getIdentificativoPortaDefault(PROXY_NOME_SOGGETTO_TEST_CREDENZIALI_BRUCIATE);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_TEST_CREDENZIALI_BRUCIATE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_TEST_CREDENZIALI_BRUCIATE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_TEST_CREDENZIALI_BRUCIATE);
	
	
	
	/** Versione Servizio */
	public static final Integer PROXY_VERSIONE_SERVIZIO_DEFAULT=1;
	
	
	/** Tipo Servizio */
	public static final String SOAP_TIPO_SERVIZIO="gw";
	
	/** Nome Servizio OneWay */
	public static final String SOAP_NOME_SERVIZIO_ONEWAY="Oneway";
	/** Nome Azione Affidabile del Servizio OneWay con notifica */
	public static final String SOAP_SERVIZIO_ONEWAY_AZIONE_NOTIFICA="notifica";
	

	/** Nome Servizio Sincrono */
	public static final String SOAP_NOME_SERVIZIO_SINCRONO="Sincrono";
    /** Nome Azione Collaborazione del Servizio RichiestaStatoAvanzamento */
    public static final String SOAP_SERVIZIO_SINCRONO_AZIONE_COLLABORAZIONE="Collaborazione";
    /** Nome Azione WSSTimestamp del Servizio RichiestaStatoAvanzamento */
    public static final String SOAP_SERVIZIO_SINCRONO_AZIONE_WSS_TIMESTAMP="WSSTimestamp";
    /** ENTITA SPCOOP: Nome Azione Aggiornamento del Servizio RichiestaStatoAvanzamento */
    public static final String PROXY_SERVIZIO_SINCRONO_AZIONE_AGGIORNAMENTO="aggiornamento";
    
    
    
	/** Tipo Servizio */
	public static final String REST_TIPO_SERVIZIO="gw";
    
    
	/** ENTITA SPCOOP: Nome Servizio MTOM SOAP11  */
	public static final String PROXY_NOME_SERVIZIO_MTOM_SOAP11="MTOMServiceExampleSOAP11";
	/** ENTITA SPCOOP: Nome Servizio MTOM SOAP12  */
	public static final String PROXY_NOME_SERVIZIO_MTOM_SOAP12="MTOMServiceExampleSOAP12";
    /** ENTITA SPCOOP: Nome Azione Echo del Servizio MTOM */
    public static final String PROXY_SERVIZIO_MTOM_AZIONE_ECHO="echo";
    /** ENTITA SPCOOP: Nome Azione packageUnpackage del Servizio MTOM  */
    public static final String PROXY_SERVIZIO_MTOM_AZIONE_PACKAGE_UNPACKAGE="packageUnpackage";
    /** ENTITA SPCOOP: Nome Azione unpackagePackage del Servizio MTOM  */
    public static final String PROXY_SERVIZIO_MTOM_AZIONE_UNPACKAGE_PACKAGE="unpackagePackage";
   
	

	/** Nome Servizio API */
	public static final String SOAP_NOME_SERVIZIO_API="API";
	

    
    
    
    
    
    public static final String MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE = "Autenticazione fallita, credenziali non fornite";
    public static final String MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE = "Autenticazione fallita, credenziali fornite non corrette";
    
    private static final String SUFFIX_MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO = " non risulta autorizzato a fruire del servizio richiesto";
    public static final String MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE = "NOMESERVIZIOAPPLICATIVO";
    public static final String MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_XACML_POLICY = ": result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok";
    public static final String MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO = "Il servizio applicativo "+MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE+SUFFIX_MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO;
    public static final String MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO_XACML_POLICY = 
    		"Il servizio applicativo "+MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE+SUFFIX_MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO+MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_XACML_POLICY;
    
    private static final String SUFFIX_MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO = " non è autorizzato ad invocare il servizio "+
    		SOAP_TIPO_SERVIZIO+"/"+SOAP_NOME_SERVIZIO_SINCRONO+" (versione:1) erogato da "+PROXY_TIPO_SOGGETTO+"/"+PROXY_NOME_SOGGETTO_EROGATORE;
    public static final String MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE = "NOMESOGGETTO";
    public static final String MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_XACML_POLICY = " (result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok)";
    public static final String MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO = "Il soggetto "+MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE+
    		SUFFIX_MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO;
    public static final String MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO_XACML_POLICY = "Il soggetto "+MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE+
    		SUFFIX_MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO+MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_XACML_POLICY;
    public static final String MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_ANONIMO_NON_AUTORIZZATO = "Il mittente"+
    		SUFFIX_MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO;
    public static final String MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_ANONIMO_NON_AUTORIZZATO_XACML_POLICY = "Il mittente"+
    		SUFFIX_MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO+MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_XACML_POLICY;
    

    
    public static final String ID_GRUPPO_TEST_CORS = "CORS";
    public static final String ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA = "CORSPortaApplicativa";
    public static final String ID_GRUPPO_TEST_CORS_PORTA_DELEGATA = "CORSPortaDelegata";
    
    
    public static final String TEST_HTTPS_ORIGIN = "https://www.govway.org";
    public static final String TEST_HTTP_ORIGIN = "http://www.govway.org";
	public static final String TEST_HTTPS_ORIGIN_UPPER_CASE = "https://www.GOVWAY.org";
	public static final String TEST_HTTPS_ORIGIN_2 = "https://www.govway2.org";
	public static final String TEST_HTTPS_ORIGIN_PORT_DIFFERENT = "https://www.govway2.org:8443";
	
	public static final String TEST_FILE_ORIGIN = "file://";
	public static final String TEST_NULL_ORIGIN = "null";
	
	public static final String TEST_INVALID_ORIGIN_1 = "http://www.w3.org%0d%0a"; 
	public static final String TEST_INVALID_ORIGIN_2 = "http://www.w3.org%0D%0A"; 
	public static final String TEST_INVALID_ORIGIN_3 = "http://www.w3.org%0%0d%0ad%0%0d%0aa";
	public static final String TEST_INVALID_ORIGIN_4 = "http://www.w3.org http://altraUrl"; 
	
	public static final String TEST_CORS_EXPOSE_HDR1 = "X-GovWayTest-HDR1";
	public static final String TEST_CORS_EXPOSE_HDR2 = "X-GovWayTest-HDR2";
	public static final String TEST_CORS_EXPOSE_HEADERS = TEST_CORS_EXPOSE_HDR1+", "+TEST_CORS_EXPOSE_HDR2;
    
	private final static List<String> TEST_CORS_ALLOW_METHOD_DEFAULT = new ArrayList<String> ();
	static {
		TEST_CORS_ALLOW_METHOD_DEFAULT.add(HttpRequestMethod.GET.name());
		TEST_CORS_ALLOW_METHOD_DEFAULT.add(HttpRequestMethod.POST.name());
		TEST_CORS_ALLOW_METHOD_DEFAULT.add(HttpRequestMethod.PUT.name());
		TEST_CORS_ALLOW_METHOD_DEFAULT.add(HttpRequestMethod.DELETE.name());
		TEST_CORS_ALLOW_METHOD_DEFAULT.add(HttpRequestMethod.PATCH.name());
	}
	public final static String TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING = TEST_CORS_ALLOW_METHOD_DEFAULT.toString().substring(1, TEST_CORS_ALLOW_METHOD_DEFAULT.toString().length()-1);

	private final static List<String> TEST_CORS_ALLOW_HEADER_DEFAULT = new ArrayList<String> ();
	static {
		TEST_CORS_ALLOW_HEADER_DEFAULT.add(HttpConstants.AUTHORIZATION);
		TEST_CORS_ALLOW_HEADER_DEFAULT.add(HttpConstants.CONTENT_TYPE);
		TEST_CORS_ALLOW_HEADER_DEFAULT.add(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION);
		TEST_CORS_ALLOW_HEADER_DEFAULT.add(HttpConstants.CACHE_STATUS_HTTP_1_1);
	}
	public final static String TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING = TEST_CORS_ALLOW_HEADER_DEFAULT.toString().substring(1, TEST_CORS_ALLOW_HEADER_DEFAULT.toString().length()-1);

}
