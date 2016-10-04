/*
 * OpenSPCoop - Customizable API Gateway 
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
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_NON_AUTENTICATO="PROXYMinisteroFruitore/PROXYMinisteroErogatoreEsterno/PROXYOnewayStateful/notifica";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_NON_AUTENTICATO="PROXYMinisteroFruitore/PROXYMinisteroErogatoreEsterno/PROXYOnewayStateless/notifica";
	
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_NON_AUTENTICATO="PROXYMinisteroFruitore/PROXYMinisteroErogatoreEsterno/PROXYSincronoStateful/aggiornamento";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_NON_AUTENTICATO="PROXYMinisteroFruitore/PROXYMinisteroErogatoreEsterno/PROXYSincronoStateless/aggiornamento";

	
	/**
	 * Credenziali per l'accesso ai servizi autenticati
	 */
	public static final String USERNAME_PORTA_DELEGATA_ONEWAY_AUTENTICATA="esempioFruitoreTrasparente";
	public static final String PASSWORD_PORTA_DELEGATA_ONEWAY_AUTENTICATA="123456";
	public static final String USERNAME_PORTA_DELEGATA_SINCRONO_AUTENTICATA="esempioFruitoreTrasparente";
	public static final String PASSWORD_PORTA_DELEGATA_SINCRONO_AUTENTICATA="123456";

	
	/** Porte Delegate per il test dei profili di collaborazione: OneWay Autenticato */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_AUTENTICATO="PROXYMinisteroFruitore/PROXYMinisteroErogatoreEsterno/PROXYOnewayStatefulAutenticato/notifica";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_AUTENTICATO="PROXYMinisteroFruitore/PROXYMinisteroErogatoreEsterno/PROXYOnewayStatelessAutenticato/notifica";
	
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono Autenticato */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_AUTENTICATO="PROXYMinisteroFruitore/PROXYMinisteroErogatoreEsterno/PROXYSincronoStatefulAutenticato/aggiornamento";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_AUTENTICATO="PROXYMinisteroFruitore/PROXYMinisteroErogatoreEsterno/PROXYSincronoStatelessAutenticato/aggiornamento";

	/** Porte Delegate per il test dei profili di collaborazione: OneWay Local Forward */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_LOCAL_FORWARD="PROXYMinisteroFruitore/PROXYMinisteroErogatore/PROXYOnewayStatefulLocalForward/notificaLocalForward";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_LOCAL_FORWARD="PROXYMinisteroFruitore/PROXYMinisteroErogatore/PROXYOnewayStatelessLocalForward/notificaLocalForward";

	/** Porte Delegate per il test dei profili di collaborazione: Sincrono Local Forward */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_LOCAL_FORWARD="PROXYMinisteroFruitore/PROXYMinisteroErogatore/PROXYSincronoStatefulLocalForward/aggiornamentoLocalForward";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_LOCAL_FORWARD="PROXYMinisteroFruitore/PROXYMinisteroErogatore/PROXYSincronoStatelessLocalForward/aggiornamentoLocalForward";

	/** Porte Delegate per il test dei profili di collaborazione: OneWay Stateful SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT11_200="PROXYClientSoapFault11/PROXYMinisteroErogatoreEsterno/PROXYOnewayStateful";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT12_200="PROXYClientSoapFault12/PROXYMinisteroErogatoreEsterno/PROXYOnewayStateful";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT11_500="PROXYClientSoapFault11500/PROXYMinisteroErogatoreEsterno/PROXYOnewayStateful";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT12_500="PROXYClientSoapFault12500/PROXYMinisteroErogatoreEsterno/PROXYOnewayStateful";

	/** Porte Delegate per il test dei profili di collaborazione: Sincrono Stateful SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT11_200="PROXYClientSoapFault11/PROXYMinisteroErogatoreEsterno/PROXYSincronoStateful";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT12_200="PROXYClientSoapFault12/PROXYMinisteroErogatoreEsterno/PROXYSincronoStateful";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT11_500="PROXYClientSoapFault11500/PROXYMinisteroErogatoreEsterno/PROXYSincronoStateful";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT12_500="PROXYClientSoapFault12500/PROXYMinisteroErogatoreEsterno/PROXYSincronoStateful";
	
	/** Porte Delegate per il test dei profili di collaborazione: OneWay Stateless SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT11_200="PROXYClientSoapFault11/PROXYMinisteroErogatoreEsterno/PROXYOnewayStateless";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT12_200="PROXYClientSoapFault12/PROXYMinisteroErogatoreEsterno/PROXYOnewayStateless";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT11_500="PROXYClientSoapFault11500/PROXYMinisteroErogatoreEsterno/PROXYOnewayStateless";
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT12_500="PROXYClientSoapFault12500/PROXYMinisteroErogatoreEsterno/PROXYOnewayStateless";

	/** Porte Delegate per il test dei profili di collaborazione: Sincrono Stateless SOAP Fault */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT11_200="PROXYClientSoapFault11/PROXYMinisteroErogatoreEsterno/PROXYSincronoStateless";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT12_200="PROXYClientSoapFault12/PROXYMinisteroErogatoreEsterno/PROXYSincronoStateless";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT11_500="PROXYClientSoapFault11500/PROXYMinisteroErogatoreEsterno/PROXYSincronoStateless";
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT12_500="PROXYClientSoapFault12500/PROXYMinisteroErogatoreEsterno/PROXYSincronoStateless";
	
	
	
	
	/** Porte Applicative per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_NON_AUTENTICATO="PROXYMinisteroErogatore/PROXYOnewayStateful";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_NON_AUTENTICATO="PROXYMinisteroErogatore/PROXYOnewayStateless";
	
	/** Porte Applicative per il test dei profili di collaborazione: Sincrono */
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_NON_AUTENTICATO="PROXYMinisteroErogatore/PROXYSincronoStateful";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_NON_AUTENTICATO="PROXYMinisteroErogatore/PROXYSincronoStateless";

	
	/**
	 * Credenziali per l'accesso ai servizi autenticati
	 */
	public static final String USERNAME_PORTA_APPLICATIVA_ONEWAY_AUTENTICATA="applicativoComunePisa";
	public static final String PASSWORD_PORTA_APPLICATIVA_ONEWAY_AUTENTICATA="123456";
	public static final String USERNAME_PORTA_APPLICATIVA_SINCRONO_AUTENTICATA="applicativoComunePisa";
	public static final String PASSWORD_PORTA_APPLICATIVA_SINCRONO_AUTENTICATA="123456";

	
	/** Porte Applicative per il test dei profili di collaborazione: OneWay Stateful SOAP Fault */
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT11_200="PROXYMinisteroErogatore/PROXYOneway/gestioneSOAPFault11Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT12_200="PROXYMinisteroErogatore/PROXYOneway/gestioneSOAPFault12Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT11_500="PROXYMinisteroErogatore/PROXYOneway/gestioneSOAPFault11500Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT12_500="PROXYMinisteroErogatore/PROXYOneway/gestioneSOAPFault12500Stateful";

	/** Porte Applicative per il test dei profili di collaborazione: Sincrono Stateful SOAP Fault */
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT11_200="PROXYMinisteroErogatore/PROXYSincrono/gestioneSOAPFault11Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT12_200="PROXYMinisteroErogatore/PROXYSincrono/gestioneSOAPFault12Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT11_500="PROXYMinisteroErogatore/PROXYSincrono/gestioneSOAPFault11500Stateful";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT12_500="PROXYMinisteroErogatore/PROXYSincrono/gestioneSOAPFault12500Stateful";
	
	/** Porte Applicative per il test dei profili di collaborazione: OneWay Stateless SOAP Fault */
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT11_200="PROXYMinisteroErogatore/PROXYOneway/gestioneSOAPFault11Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT12_200="PROXYMinisteroErogatore/PROXYOneway/gestioneSOAPFault12Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT11_500="PROXYMinisteroErogatore/PROXYOneway/gestioneSOAPFault11500Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT12_500="PROXYMinisteroErogatore/PROXYOneway/gestioneSOAPFault12500Stateless";

	/** Porte Applicative per il test dei profili di collaborazione: Sincrono Stateless SOAP Fault */
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT11_200="PROXYMinisteroErogatore/PROXYSincrono/gestioneSOAPFault11Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT12_200="PROXYMinisteroErogatore/PROXYSincrono/gestioneSOAPFault12Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT11_500="PROXYMinisteroErogatore/PROXYSincrono/gestioneSOAPFault11500Stateless";
	public static final String PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT12_500="PROXYMinisteroErogatore/PROXYSincrono/gestioneSOAPFault12500Stateless";
	
	
		
	/** ENTITA SPCOOP: Tipo Soggetto */
	public static final String PROXY_TIPO_SOGGETTO="PROXY";
	
	
	/** ENTITA SPCOOP: Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT11500="ClientSoapFault11500";
	/** ENTITA SPCOOP: Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT11200="ClientSoapFault11";
	/** ENTITA SPCOOP: Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT12500="ClientSoapFault12500";
	/** ENTITA SPCOOP: Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_FAULT12200="ClientSoapFault12";
	/** ENTITA SPCOOP: Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE="MinisteroFruitore";
	/** ENTITA SPCOOP: Nome Soggetto Fruitore Anomino*/
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_ANONIMO="Anonimo";
	/** ENTITA SPCOOP: Nome Soggetto Fruitore Anomino*/
	public static final String PROXY_NOME_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA="applicativoComunePisa";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA="applicativoComunePisaPdD";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE="MinisteroFruitorePdD";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11500="ClientSoapFault11500PdD";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11200="ClientSoapFault11PdD";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12500="ClientSoapFault12500PdD";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12200="ClientSoapFault12PdD";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_ANONIMO="AnonimoPdD";
	
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT11500 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT11500, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11500);
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT11200 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT11200, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT11200);
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT12500 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT12500, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12500);
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_FAULT12200 = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_FAULT12200, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_FAULT12200);
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE);
	/** ENTITA SPCOOP: IDSoggetto Anonimo*/
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_ANONIMO = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_ANONIMO, CostantiTestSuite.PROXY_IDPORTA_ANONIMO);
		
	/** ENTITA SPCOOP: IDSoggetto Autenticato Porta Applicativa */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA= new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA);
		
	/** ENTITA SPCOOP: Nome Soggetto Erogatore */
	public static final String PROXY_NOME_SOGGETTO_EROGATORE="MinisteroErogatore";
	/** ENTITA SPCOOP: IdPorta Soggetto Erogatore */
	public static final String PROXY_IDPORTA_SOGGETTO_EROGATORE="MinisteroErogatorePdD";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_EROGATORE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_EROGATORE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_EROGATORE);
	
	/** ENTITA SPCOOP: Nome Soggetto Erogatore Esterno */
	public static final String PROXY_NOME_SOGGETTO_EROGATORE_ESTERNO="MinisteroErogatoreEsterno";
	/** ENTITA SPCOOP: IdPorta Soggetto Erogatore Esterno */
	public static final String PROXY_IDPORTA_SOGGETTO_EROGATORE_ESTERNO="MinisteroErogatoreEsternoPdD";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_EROGATORE_ESTERNO = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_EROGATORE_ESTERNO, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_EROGATORE_ESTERNO);
		
	
	
	/** ENTITA SPCOOP: Versione Servizio */
	public static final Integer PROXY_VERSIONE_SERVIZIO_DEFAULT=1;
	
	
	/** ENTITA SPCOOP: Tipo Servizio */
	public static final String PROXY_TIPO_SERVIZIO="PROXY";
	
	/** ENTITA SPCOOP: Nome Servizio OneWay */
	public static final String PROXY_NOME_SERVIZIO_ONEWAY="Oneway";
	/** ENTITA SPCOOP: Nome Azione Affidabile del Servizio OneWay con notifica */
	public static final String PROXY_SERVIZIO_ONEWAY_AZIONE_NOTIFICA="notifica";
	

	/** ENTITA SPCOOP: Nome Servizio Sincrono */
	public static final String PROXY_NOME_SERVIZIO_SINCRONO="Sincrono";
    /** ENTITA SPCOOP: Nome Azione Collaborazione del Servizio RichiestaStatoAvanzamento */
    public static final String PROXY_SERVIZIO_SINCRONO_AZIONE_COLLABORAZIONE="Collaborazione";
    /** ENTITA SPCOOP: Nome Azione WSSTimestamp del Servizio RichiestaStatoAvanzamento */
    public static final String PROXY_SERVIZIO_SINCRONO_AZIONE_WSS_TIMESTAMP="WSSTimestamp";
   
	
}
