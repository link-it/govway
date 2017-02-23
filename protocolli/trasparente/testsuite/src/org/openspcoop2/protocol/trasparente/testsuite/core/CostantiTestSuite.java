/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;

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
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT11_200="PROXYClientSoapFault11/APIMinisteroErogatoreEsterno/SOAPOnewayStateful";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT12_200="PROXYClientSoapFault12/APIMinisteroErogatoreEsterno/SOAPOnewayStateful";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT11_500="PROXYClientSoapFault11500/APIMinisteroErogatoreEsterno/SOAPOnewayStateful";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT12_500="PROXYClientSoapFault12500/APIMinisteroErogatoreEsterno/SOAPOnewayStateful";

	/** Porte Delegate per il test dei profili di collaborazione: Sincrono Stateful SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT11_200="PROXYClientSoapFault11/APIMinisteroErogatoreEsterno/SOAPSincronoStateful";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT12_200="PROXYClientSoapFault12/APIMinisteroErogatoreEsterno/SOAPSincronoStateful";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT11_500="PROXYClientSoapFault11500/APIMinisteroErogatoreEsterno/SOAPSincronoStateful";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT12_500="PROXYClientSoapFault12500/APIMinisteroErogatoreEsterno/SOAPSincronoStateful";
	
	/** Porte Delegate per il test dei profili di collaborazione: OneWay Stateless SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT11_200="PROXYClientSoapFault11/APIMinisteroErogatoreEsterno/SOAPOnewayStateless";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT12_200="PROXYClientSoapFault12/APIMinisteroErogatoreEsterno/SOAPOnewayStateless";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT11_500="PROXYClientSoapFault11500/APIMinisteroErogatoreEsterno/SOAPOnewayStateless";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT12_500="PROXYClientSoapFault12500/APIMinisteroErogatoreEsterno/SOAPOnewayStateless";

	/** Porte Delegate per il test dei profili di collaborazione: Sincrono Stateless SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT11_200="PROXYClientSoapFault11/APIMinisteroErogatoreEsterno/SOAPSincronoStateless";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT12_200="PROXYClientSoapFault12/APIMinisteroErogatoreEsterno/SOAPSincronoStateless";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT11_500="PROXYClientSoapFault11500/APIMinisteroErogatoreEsterno/SOAPSincronoStateless";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT12_500="PROXYClientSoapFault12500/APIMinisteroErogatoreEsterno/SOAPSincronoStateless";
	
	/** Porte Delegate per il test MTOM */
	public static final String PORTA_DELEGATA_MTOM_SOAP11="PROXYMinisteroFruitore/PROXYMinisteroErogatore/PROXYMTOMServiceExampleSOAP11/echo";
	public static final String PORTA_DELEGATA_MTOM_SOAP12="PROXYMinisteroFruitore/PROXYMinisteroErogatore/PROXYMTOMServiceExampleSOAP12/echo";
	
	/** Porte Delegate per il test dei profili di collaborazione: API */
	public static final String PORTA_DELEGATA_REST_API="APIMinisteroFruitore/APIMinisteroErogatore/RESTAPI";
	
	/** Porte Delegate per il test dei profili di collaborazione: API */
	public static final String PORTA_DELEGATA_REST_API_LOCAL_FORWARD="APIMinisteroFruitore/APIMinisteroErogatore/RESTAPI_viaLocalForward";
	

	
	
	/** Porte Applicative per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_NON_AUTENTICATO="APIMinisteroErogatore/SOAPOnewayStateful";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_NON_AUTENTICATO="APIMinisteroErogatore/SOAPOnewayStateless";
	
	/** Porte Applicative per il test dei profili di collaborazione: Sincrono */
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_NON_AUTENTICATO="APIMinisteroErogatore/SOAPSincronoStateful";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_NON_AUTENTICATO="APIMinisteroErogatore/SOAPSincronoStateless";

	
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
	

	/** Porte Delegate per il test dei profili di collaborazione: API */
	public static final String PORTA_APPLICATIVA_REST_API="APIMinisteroErogatore/RESTAPI";
		
	
	public static final String USERNAME_PORTA_APPLICATIVA_MTOM="MinisteroFruitore";
	public static final String PASSWORD_PORTA_APPLICATIVA_MTOM="123456";
	
		
	/** Tipo Soggetto */
	public static final String PROXY_TIPO_SOGGETTO="API";
	
	
	/** Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT11500="ClientSoapFault11500";
	/** Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT11200="ClientSoapFault11";
	/** Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT12500="ClientSoapFault12500";
	/** Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT12200="ClientSoapFault12";
	/** Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE="MinisteroFruitore";
	/** Nome Soggetto Fruitore Anomino*/
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_ANONIMO="Anonimo";
	/** Nome Soggetto Fruitore Anomino*/
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA="applicativoComunePisa";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA="applicativoComunePisaPdD";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE="MinisteroFruitorePdD";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11500="ClientSoapFault11500PdD";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11200="ClientSoapFault11PdD";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12500="ClientSoapFault12500PdD";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12200="ClientSoapFault12PdD";
	/** IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_ANONIMO="AnonimoPdD";
	
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT11500 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT11500, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11500);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT11200 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT11200, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11200);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT12500 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT12500, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12500);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT12200 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT12200, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12200);
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE);
	/** IDSoggetto Anonimo*/
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_ANONIMO = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_ANONIMO, CostantiTestSuite.PROXY_IDPORTA_ANONIMO);
		
	/** IDSoggetto Autenticato Porta Applicativa */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA= new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA);
		
	/** Nome Soggetto Erogatore */
	public static final String PROXY_NOME_SOGGETTO_EROGATORE="MinisteroErogatore";
	/** IdPorta Soggetto Erogatore */
	public static final String PROXY_IDPORTA_SOGGETTO_EROGATORE="MinisteroErogatorePdD";
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_EROGATORE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_EROGATORE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_EROGATORE);
	
	/** Nome Soggetto Erogatore Esterno */
	public static final String PROXY_NOME_SOGGETTO_EROGATORE_ESTERNO="MinisteroErogatoreEsterno";
	/** IdPorta Soggetto Erogatore Esterno */
	public static final String PROXY_IDPORTA_SOGGETTO_EROGATORE_ESTERNO="MinisteroErogatoreEsternoPdD";
	/** IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_EROGATORE_ESTERNO = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_EROGATORE_ESTERNO, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_EROGATORE_ESTERNO);
		
	
	
	/** Versione Servizio */
	public static final Integer PROXY_VERSIONE_SERVIZIO_DEFAULT=1;
	
	
	/** Tipo Servizio */
	public static final String SOAP_TIPO_SERVIZIO="SOAP";
	
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
    
    
    
	/** Tipo Servizio */
	public static final String REST_TIPO_SERVIZIO="REST";
    
    
	/** ENTITA SPCOOP: Nome Servizio MTOM SOAP11  */
	public static final String PROXY_NOME_SERVIZIO_MTOM_SOAP11="MTOMServiceExampleSOAP11";
	/** ENTITA SPCOOP: Nome Servizio MTOM SOAP12  */
	public static final String PROXY_NOME_SERVIZIO_MTOM_SOAP12="MTOMServiceExampleSOAP12";
    /** ENTITA SPCOOP: Nome Azione Echo del Servizio RichiestaStatoAvanzamento */
    public static final String PROXY_SERVIZIO_MTOM_AZIONE_ECHO="echo";
   
	
	/** Nome Servizio API */
	public static final String SOAP_NOME_SERVIZIO_API="API";
	
}
