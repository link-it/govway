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



package org.openspcoop2.protocol.spcoop.testsuite.core;

import org.openspcoop2.core.id.IDSoggetto;

/**
 * Costanti utilizzate nelle units test.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CostantiTestSuite {

	
	/** Protocollo SPcoop */
	public static final String PROTOCOL_NAME = "spcoop";
	
	
	/** Porte Delegate per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY="HelloWorld";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_LOOPBACK="HelloWorldLoopback";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay tramissione sincrona */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS="HelloWorld_Stateless";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay tramissione sincrona affidabile */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_AFFIDABILE="CooperazioneOneWay_StatelessAffidabile";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay con integrazione */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE="IntegrazioneOneway";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay con integrazione con WSAddressing */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_WSADDRESSING="IntegrazioneOnewayWithWSAddressing";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay con integrazione con connettore SAAJ */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_SAAJ="IntegrazioneOnewaySAAJ";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay con integrazione con WSAddressing con connettore SAAJ */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_WSADDRESSING_SAAJ="IntegrazioneOnewayWithWSAddressingSAAJ";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay con integrazione con connettore HTTPCORE */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_HTTPCORE="IntegrazioneOnewayHTTPCORE";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay con integrazione con WSAddressing con connettore HTTPCORE */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_WSADDRESSING_HTTPCORE="IntegrazioneOnewayWithWSAddressingHTTPCORE";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay tramissione risposta completamente vuota */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_RISPOSTA_VUOTA="OnewayCompletamenteNullRisposta";
	
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO="CooperazioneSincrona";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono stateful */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL="CooperazioneSincrona_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE="IntegrazioneSincrona";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione con WSAddressing */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING="IntegrazioneSincronaWithWSAddressing";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione per connettore SAAJ */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_SAAJ="IntegrazioneSincronaSAAJ";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione con WSAddressing per connettore SAAJ */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING_SAAJ="IntegrazioneSincronaWithWSAddressingSAAJ";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione per connettore HTTPCORE */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_HTTPCORE="IntegrazioneSincronaHTTPCORE";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione con WSAddressing per connettore HTTPCORE */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING_HTTPCORE="IntegrazioneSincronaWithWSAddressingHTTPCORE";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione url based */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_URL_BASED="IntegrazioneURLBased";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione url-form based  */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_URL_FORM_BASED="IntegrazioneURLFormBased";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione content based 1 */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED1="IntegrazioneContentBased1";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione content based 2 */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED2="IntegrazioneContentBased2";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione content based concat */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED_CONCAT="IntegrazioneContentBasedCONCAT";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione content based concat_openspcoop */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED_CONCAT_OPENSPCOOP="IntegrazioneContentBasedCONCATOpenSPCoop";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione content based concat errore identificazione */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED_CONCAT_ERRORE_IDENTIFICAZIONE="IntegrazioneContentBasedCONCAT_ERRORE";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione content based concat_openspcoop errore identificazione*/
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED_CONCAT_OPENSPCOOP_ERRORE_IDENTIFICAZIONE="IntegrazioneContentBasedCONCATOpenSPCoop_ERRORE";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione input based */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_INPUT_BASED="IntegrazioneInputBased";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con integrazione soap action based */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_SOAP_ACTION_BASED="IntegrazioneSOAPActionBased";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa url based */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED="CorrelazioneApplicativaUrlBased";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa url based senza riuso id egov */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED_SENZA_RIUSO_IDEGOV="CorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa url based, accetta se identificazione non riuscita */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED_ACCETTA_CON_IDENTIFICAZIONE_NON_RIUSCITA="CorrelazioneApplicativaUrlBased_accettaSeErrore";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa content based */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED="CorrelazioneApplicativaContentBased";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa content based senza riuso id egov */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_SENZA_RIUSO_IDEGOV="CorrelazioneApplicativaContentBasedSenzaRiusoIDEGov";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa content based, accetta se identificazione non riuscita */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_ACCETTA_CON_IDENTIFICAZIONE_NON_RIUSCITA="CorrelazioneApplicativaContentBased_accettaSeErrore";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa content based concat */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT="CorrelazioneApplicativaContentBasedCONCAT";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa content based concat senza riuso id egov*/
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_SENZA_RIUSO_IDEGOV="CorrelazioneApplicativaContentBasedCONCATSenzaRiusoIDEGov";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa content based concat_openspcoop */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP="CorrelazioneApplicativaContentBasedCONCATOpenSPCoop";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa content based concat_openspcoop senza riuso id egov*/
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP_SENZA_RIUSO_IDEGOV="CorrelazioneApplicativaContentBasedCONCATOpenSPCoopSenzaRiusoIDEGov";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa input based */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED="CorrelazioneApplicativaInputBased";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa input based senza riuso id egov*/
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_SENZA_RIUSO_IDEGOV="CorrelazioneApplicativaInputBasedSenzaRiusoIDEGov";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa input based, accetta se identificazione non riuscita */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_ACCETTA_CON_IDENTIFICAZIONE_NON_RIUSCITA="CorrelazioneApplicativaInputBased_accettaSeErrore";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa disabilitato */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_DISABILITATA="CorrelazioneApplicativaDisabilitata";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa (lista di correlazioni) */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI="CorrelazioneApplicativaSelezioneElemento";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa content based lato porta applicativa*/
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_LATO_PORTA_APPLICATIVA="CorrelazioneApplicativaContentBasedPortaApplicativa";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa nella risposta: content based soap */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS="CorrelazioneApplicativaContentBasedRisposta_ContentBased_SOAP_stateless";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa nella risposta: content based soap */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS_ERRORE_CONFIGURAZIONE="CorrelazioneApplicativaContentBasedRisposta_ContentBased_SOAP_stateless_erroreConfigurazioneVoluta";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa nella risposta: content based soap */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATEFUL="CorrelazioneApplicativaContentBasedRisposta_ContentBased_SOAP_stateful";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa nella risposta: input based trasporto */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_TRASPORTO="CorrelazioneApplicativaInputBasedRisposta_InputBased_TRASPORTO";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa nella risposta: input based soap */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_SOAP="CorrelazioneApplicativaInputBasedRisposta_InputBased_SOAP";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con correlazione applicativa nella risposta: input based wsa */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_WSA="CorrelazioneApplicativaInputBasedRisposta_InputBased_WSA";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con xml encoding test*/
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_XML_ENCODING="XMLEncoding";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con xml encoding test*/
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_XML_ENCODING_STATEFUL="XMLEncodingStateful";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con soap header vuoto o non presente test*/
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_SOAP_HEADER_EMPTY="SOAPHeaderEmpty";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono con soap header vuoto o non presente test*/
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_SOAP_HEADER_EMPTY_STATEFUL="SOAPHeaderEmptyStateful";
	/** Porte Delegate per il test dei profili di collaborazione: XMLErrato del body come risposta della PdD */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_XML_ERRATO_BODY_RISPOSTA_PDD="TestXMLErratoBodyRisposta";
	/** Porte Delegate per il test dei profili di collaborazione: XMLErrato dell'header come risposta della PdD */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_XML_ERRATO_HEADER_RISPOSTA_PDD="TestXMLErratoHeaderRisposta";
	/** Porte Delegate per il test dei profili di collaborazione: ContentTypeRisposta errato della PdD */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_CONTENT_TYPE_ERRATO_HEADER_RISPOSTA_PDD="TestContentTypeErratoRisposta";
	
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA="CooperazioneAsincronaAsimmetrica_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_ASINCRONA="CooperazioneAsincronaAsimmetricaCorrelata_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA="CooperazioneAsincronaAsimmetrica_richiestaSincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA="CooperazioneAsincronaAsimmetricaCorrelata_richiestaSincrona";
	
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATEFUL="CooperazioneAsincronaAsimmetrica_richiestaAsincrona_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_ASINCRONA_STATEFUL="CooperazioneAsincronaAsimmetricaCorrelata_richiestaAsincrona_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATEFUL="CooperazioneAsincronaAsimmetrica_richiestaSincrona_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA_STATEFUL="CooperazioneAsincronaAsimmetricaCorrelata_richiestaSincrona_Stateful";
		
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' asincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA="CooperazioneAsincronaSimmetrica_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' asincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_ASINCRONA="CooperazioneAsincronaSimmetricaCorrelata_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' sincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA="CooperazioneAsincronaSimmetrica_richiestaSincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' sincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA="CooperazioneAsincronaSimmetricaCorrelata_richiestaSincrona";
	
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' asincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATEFUL="CooperazioneAsincronaSimmetrica_richiestaAsincrona_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' asincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_ASINCRONA_STATEFUL="CooperazioneAsincronaSimmetricaCorrelata_richiestaAsincrona_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' sincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATEFUL="CooperazioneAsincronaSimmetrica_richiestaSincrona_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' sincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA_STATEFUL="CooperazioneAsincronaSimmetricaCorrelata_richiestaSincrona_Stateful";
	
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, richiesta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_MODALITA_ASINCRONA="CooperazioneAsincronaAsimmetrica_AzioneCorrelata_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, risposta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_AZIONE_CORRELATA_MODALITA_ASINCRONA="CooperazioneAsincronaAsimmetricaCorrelata_AzioneCorrelata_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, richiesta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_MODALITA_SINCRONA="CooperazioneAsincronaAsimmetrica_AzioneCorrelata_richiestaSincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, risposta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_AZIONE_CORRELATA_MODALITA_SINCRONA="CooperazioneAsincronaAsimmetricaCorrelata_AzioneCorrelata_richiestaSincrona";
	
	
	/** Porte Delegate per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_PORT_TYPE="PortTypeOneWay";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_LOOPBACK_PORT_TYPE="HelloWorldLoopbackPortType";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_PORT_TYPE="PortTypeSincrono";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_PORT_TYPE="PortTypeAsincronoAsimmetricoRichiesta_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_ASINCRONA_PORT_TYPE="PortTypeAsincronoAsimmetricoRichiestaStato_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_PORT_TYPE="PortTypeAsincronoAsimmetricoRichiesta_richiestaSincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA_PORT_TYPE="PortTypeAsincronoAsimmetricoRichiestaStato_richiestaSincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' asincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_PORT_TYPE="PortTypeAsincronoSimmetricoRichiesta_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' asincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_ASINCRONA_PORT_TYPE="PortTypeAsincronoSimmetricoRisposta_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' sincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_PORT_TYPE="PortTypeAsincronoSimmetricoRichiesta_richiestaSincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' sincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA_PORT_TYPE="PortTypeAsincronoSimmetricoRisposta_richiestaSincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, richiesta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_MODALITA_ASINCRONA_PORT_TYPE="PortTypeAsincronoAsimmetrico_AzioneCorrelata_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, risposta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_AZIONE_CORRELATA_MODALITA_ASINCRONA_PORT_TYPE="PortTypeAsincronoAsimmetricoRichiestaStato_AzioneCorrelata_richiestaAsincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, richiesta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_MODALITA_SINCRONA_PORT_TYPE="PortTypeAsincronoAsimmetrico_AzioneCorrelata_richiestaSincrona";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, risposta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_AZIONE_CORRELATA_MODALITA_SINCRONA_PORT_TYPE="PortTypeAsincronoAsimmetricoRichiestaStato_AzioneCorrelata_richiestaSincrona";
	
	
	/** Porte Delegate per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_LINEE_GUIDA="HelloWorld_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay (Stateless) */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_LINEE_GUIDA_STATELESS="HelloWorld_SPCMinisteroFruitoreLineeGuida_stateless";
	/** Porte Delegate per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_LOOPBACK_LINEE_GUIDA="HelloWorldLoopback_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_LINEE_GUIDA="CooperazioneSincrona_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: Sincrono */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_LINEE_GUIDA_STATEFUL="CooperazioneSincrona_SPCMinisteroFruitoreLineeGuida_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_LINEE_GUIDA="CooperazioneAsincronaAsimmetrica_richiestaAsincrona_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_ASINCRONA_LINEE_GUIDA="CooperazioneAsincronaAsimmetricaCorrelata_richiestaAsincrona_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_LINEE_GUIDA="CooperazioneAsincronaAsimmetrica_richiestaSincrona_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA_LINEE_GUIDA="CooperazioneAsincronaAsimmetricaCorrelata_richiestaSincrona_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_LINEE_GUIDA_STATEFUL="CooperazioneAsincronaAsimmetrica_richiestaAsincrona_SPCMinisteroFruitoreLineeGuida_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_ASINCRONA_LINEE_GUIDA_STATEFUL="CooperazioneAsincronaAsimmetricaCorrelata_richiestaAsincrona_SPCMinisteroFruitoreLineeGuida_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_LINEE_GUIDA_STATEFUL="CooperazioneAsincronaAsimmetrica_richiestaSincrona_SPCMinisteroFruitoreLineeGuida_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA_LINEE_GUIDA_STATEFUL="CooperazioneAsincronaAsimmetricaCorrelata_richiestaSincrona_SPCMinisteroFruitoreLineeGuida_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' asincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_LINEE_GUIDA="CooperazioneAsincronaSimmetrica_richiestaAsincrona_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' asincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_ASINCRONA_LINEE_GUIDA="CooperazioneAsincronaSimmetricaCorrelata_richiestaAsincrona_SPCMinisteroErogatoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' sincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_LINEE_GUIDA="CooperazioneAsincronaSimmetrica_richiestaSincrona_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' sincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA_LINEE_GUIDA="CooperazioneAsincronaSimmetricaCorrelata_richiestaSincrona_SPCMinisteroErogatoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' asincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_LINEE_GUIDA_STATEFUL="CooperazioneAsincronaSimmetrica_richiestaAsincrona_SPCMinisteroFruitoreLineeGuida_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' asincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_ASINCRONA_LINEE_GUIDA_STATEFUL="CooperazioneAsincronaSimmetricaCorrelata_richiestaAsincrona_SPCMinisteroErogatoreLineeGuida_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' sincrona, richiesta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_LINEE_GUIDA_STATEFUL="CooperazioneAsincronaSimmetrica_richiestaSincrona_SPCMinisteroFruitoreLineeGuida_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoSimmetrico modalita' sincrona, risposta */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA_LINEE_GUIDA_STATEFUL="CooperazioneAsincronaSimmetricaCorrelata_richiestaSincrona_SPCMinisteroErogatoreLineeGuida_Stateful";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, richiesta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_MODALITA_ASINCRONA_LINEE_GUIDA="CooperazioneAsincronaAsimmetrica_AzioneCorrelata_richiestaAsincrona_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' asincrona, risposta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_AZIONE_CORRELATA_MODALITA_ASINCRONA_LINEE_GUIDA="CooperazioneAsincronaAsimmetricaCorrelata_AzioneCorrelata_richiestaAsincrona_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, richiesta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_MODALITA_SINCRONA_LINEE_GUIDA="CooperazioneAsincronaAsimmetrica_AzioneCorrelata_richiestaSincrona_SPCMinisteroFruitoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: AsincronoAsimmetrico modalita' sincrona, risposta (azione correlata) */
	public static final String PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_AZIONE_CORRELATA_MODALITA_SINCRONA_LINEE_GUIDA="CooperazioneAsincronaAsimmetricaCorrelata_AzioneCorrelata_richiestaSincrona_SPCMinisteroFruitoreLineeGuida";
	
	/** Porte Delegate per le Funzionalita' E-Gov CooperazioneAffidabile */
	public static final String PORTA_DELEGATA_COOPERAZIONE_AFFIDABILE="CooperazioneAffidabile";
	/** Porte Delegate per le Funzionalita' E-Gov CooperazioneFiltroDuplicati */
	public static final String PORTA_DELEGATA_COOPERAZIONE_CON_FILTRO_DUPLICATI="FiltroDuplicati";
	/** Porte Delegate per le Funzionalita' E-Gov CooperazioneConFiltroDuplicato con Filtro duplicati */	
	public static final String PORTA_DELEGATA_COOPERAZIONE_AFFIDABILE_CON_FILTRO_DUPLICATI="CooperazioneAffidabileConFiltroDuplicati";
	/** Porte Delegate per le Funzionalita' E-Gov CooperazioneConFiltroDuplicato con Filtro duplicati */
    public static final String PORTA_DELEGATA_COOPERAZIONE_CON_ID_DI_COLLABORAZIONE="CooperazioneConIDCollaborazione";
	
	/** Porte Delegate per richieste applicative scorrette: HelloWorldAuthBasic */
	public static final String PORTA_DELEGATA_AUTENTICAZIONE_BASIC="HelloWorldAuthBasic";
	/** Porte Delegate per richieste applicative scorrette: HelloWorldAuthSSL */
	public static final String PORTA_DELEGATA_AUTENTICAZIONE_SSL="HelloWorldAuthSSL";
	/** Porte Delegate per richieste applicative scorrette: ServizioInesistente */
	public static final String PORTA_DELEGATA_SERVIZIO_INESISTENTE="ServizioSPCoopInesistente";
	
	/** Porte Delegate per WS-Security: WSSHelloWorld*/
	public static final String PORTA_DELEGATA_WSS_HELLO_WORLD="WSSHelloWorld";
	/** Porte Delegate per WS-Security: WSSHelloWorld AutorizzazioneKO*/
	public static final String PORTA_DELEGATA_WSS_HELLO_WORLD_AUTORIZZAZIONE_OK="WSSHelloWorldNonAutorizzato";
	/** Porte Delegate per WS-Security: WSSTimestamp*/
	public static final String PORTA_DELEGATA_WSS_TIMESTAMP="WSSTimestamp";
	/** Porte Delegate per WS-Security: WSSEncrypt*/
	public static final String PORTA_DELEGATA_WSS_ENCRYPT="WSSEncrypt";
	/** Porte Delegate per WS-Security: WSSEncrypt*/
	public static final String PORTA_DELEGATA_WSS_ENCRYPT_P12="WSSEncryptP12";
	/** Porte Delegate per WS-Security: WSSSignature*/
	public static final String PORTA_DELEGATA_WSS_SIGNATURE="WSSSignature";
	/** Porte Delegate per WS-Security: WSSSignature*/
	public static final String PORTA_DELEGATA_WSS_SIGNATURE_P12="WSSSignatureP12";
	/** Porte Delegate per WS-Security: WSSEncrypt, con configurazione errata. Deve essere tornato un codice EGOV_IT_200 */
	public static final String PORTA_DELEGATA_WSS_ENCRYPT_MESSAGGIO_ALTERATO="WSSEncryptError";
	/** Porte Delegate per WS-Security: WSSSignature, con configurazione errata. Deve essere tornato un codice EGOV_IT_202*/
	public static final String PORTA_DELEGATA_WSS_SIGNATURE_MESSAGGIO_ALTERATO="WSSSignatureError";
	/** Porte Delegate per WS-Security: WSS*/
	public static final String PORTA_DELEGATA_WSS_ALL_FUNCTION="WSS";
	/** Porte Delegate per WS-Security: CooperazioneAffidabileConFiltroDuplicatiWSS*/
	public static final String PORTA_DELEGATA_WSS_AFFIDABILE_CON_FILTRO_DUPLICATI=
		"CooperazioneAffidabileConFiltroDuplicatiWSS";
	/** Porte Delegate per WS-Security: CooperazioneAsincronaSimmetricaWSS*/
	public static final String PORTA_DELEGATA_WSS_ASINCRONA_SIMMETRICA=
		"CooperazioneAsincronaSimmetricaWSS";
	/** Porte Delegate per WS-Security: CooperazioneAsincronaSimmetricaCorrelataWSS*/
	public static final String PORTA_DELEGATA_WSS_ASINCRONA_SIMMETRICA_CORRELATA=
		"CooperazioneAsincronaSimmetricaCorrelataWSS";
	/** Porte Delegate per WS-Security: CooperazioneAsincronaAsimmetricaWSS*/
	public static final String PORTA_DELEGATA_WSS_ASINCRONA_ASIMMETRICA=
		"CooperazioneAsincronaAsimmetricaWSS";
	/** Porte Delegate per WS-Security: CooperazioneAsincronaAsimmetricaCorrelataWSS*/
	public static final String PORTA_DELEGATA_WSS_ASINCRONA_ASIMMETRICA_CORRELATA=
		"CooperazioneAsincronaAsimmetricaCorrelataWSS";
	/** Porte Delegate per WS-Security: WSS BUG18 WSSEncrypt_PdDMustUnderstand1*/
	public static final String PORTA_DELEGATA_WSS_MU_ENCRYPT="WSSEncrypt_PdDMustUnderstand1";
	/** Porte Delegate per WS-Security: WSS BUG18 WSSEncrypt_MustUnderstand0_ActorOpenSPCoop*/
	public static final String PORTA_DELEGATA_WSS_Actor_ENCRYPT="WSSEncrypt_MustUnderstand0_ActorOpenSPCoop";
	/** Porte Delegate per WS-Security: WSS BUG18 WSSSignature_PdDMustUnderstand1*/
	public static final String PORTA_DELEGATA_WSS_MU_SIGNATURE="WSSSignature_PdDMustUnderstand1";
	/** Porte Delegate per WS-Security: WSS BUG18 WSSSignature_MustUnderstand0_ActorOpenSPCoop*/
	public static final String PORTA_DELEGATA_WSS_Actor_SIGNATURE="WSSSignature_MustUnderstand0_ActorOpenSPCoop";
	/** Porte Delegate per WS-Security: WSS Annidato*/
	public static final String PORTA_DELEGATA_WSS_Annidamento="CooperazioneAsincronaSimmetricaWSSAnnidamento";
	
	/** Porte Delegate per WS-Security: SOAPBOX_CifrataFirmata */
	public static final String PORTA_DELEGATA_WSS_SOAPBOX_ENCRYPT_SIGNATURE="SOAPBOX_CifrataFirmata";
	/** Porte Delegate per WS-Security: SOAPBOX_FirmataCifrata */
	public static final String PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE_ENCRYPT="SOAPBOX_FirmataCifrata";
	/** Porte Delegate per WS-Security: SOAPBOX_Cifrata */
	public static final String PORTA_DELEGATA_WSS_SOAPBOX_ENCRYPT="SOAPBOX_Cifrata";
	/** Porte Delegate per WS-Security: SOAPBOX_Firmata */
	public static final String PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE="SOAPBOX_Firmata";
	
	/** Porte Delegate per WS-Security: SOAPBOX_CifrataFirmataAttachments */
	public static final String PORTA_DELEGATA_WSS_SOAPBOX_ENCRYPT_SIGNATURE_ATTACHMENTS="SOAPBOX_CifrataFirmataAttachments";
	/** Porte Delegate per WS-Security: SOAPBOX_FirmataCifrataAttachments */
	public static final String PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE_ENCRYPT_ATTACHMENTS="SOAPBOX_FirmataCifrataAttachments";
	/** Porte Delegate per WS-Security: SOAPBOX_CifrataAttachments */
	public static final String PORTA_DELEGATA_WSS_SOAPBOX_ENCRYPT_ATTACHMENTS="SOAPBOX_CifrataAttachments";
	/** Porte Delegate per WS-Security: SOAPBOX_FirmataAttachments */
	public static final String PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS="SOAPBOX_FirmataAttachments";

	/** Porte Delegate per WS-Security: SOAPBOX_FirmataAttachments_SignatureEngineSun */
	public static final String PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS_ENGINE_SUN="SOAPBOX_FirmataAttachments_SignatureEngineSun";
	/** Porte Delegate per WS-Security: SOAPBOX_FirmataAttachments SignatureEngineXmlSec */
	public static final String PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS_ENGINE_XMLSEC="SOAPBOX_FirmataAttachments_SignatureEngineXmlSec";
	
	/** Porte Delegate per Performance test: ConnettoreNULL con OneWay */
	public static final String PORTA_DELEGATA_PD_ONEWAY_CONNETTORE_NULL = "ConnettorePDOneWayNULL";
	/** Porte Delegate per Performance test: ConnettoreNULL con Sincrono */
	public static final String PORTA_DELEGATA_PD_SINCRONO_CONNETTORE_NULL = "ConnettorePDSincronoNULL";
	/** Porte Delegate per Performance test: ConnettoreNULL con AsincronoAsimmetrico modalita Asincrona*/
	public static final String PORTA_DELEGATA_PD_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_CONNETTORE_NULL = "ConnettorePDAsincronoSimmetrico_richiestaAsincronaNULL";
	/** Porte Delegate per Performance test: ConnettoreNULL con AsincronoAsimmetricoCorrelato modalita Asincrona*/
	public static final String PORTA_DELEGATA_PD_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_ASINCRONA_CONNETTORE_NULL = "ConnettorePDAsincronoSimmetricoCorrelato_richiestaAsincronaNULL";
	/** Porte Delegate per Performance test: ConnettoreNULL con AsincronoAsimmetrico modalita Sincrona*/
	public static final String PORTA_DELEGATA_PD_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_CONNETTORE_NULL = "ConnettorePDAsincronoSimmetrico_richiestaSincronaNULL";
	/** Porte Delegate per Performance test: ConnettoreNULL con AsincronoAsimmetricoCorrelato modalita Sincrona*/
	public static final String PORTA_DELEGATA_PD_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA_CONNETTORE_NULL = "ConnettorePDAsincronoSimmetricoCorrelato_richiestaSincronaNULL";
	/** Porte Delegate per Performance test: ConnettoreNULL con AsincronoAsimmetrico modalita Asincrona*/
	public static final String PORTA_DELEGATA_PD_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_CONNETTORE_NULL = "ConnettorePDAsincronoAsimmetrico_richiestaAsincronaNULL";
	/** Porte Delegate per Performance test: ConnettoreNULL con AsincronoAsimmetricoCorrelato modalita Asincrona*/
	public static final String PORTA_DELEGATA_PD_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_ASINCRONA_CONNETTORE_NULL = "ConnettorePDAsincronoAsimmetricoCorrelato_richiestaAsincronaNULL";
	/** Porte Delegate per Performance test: ConnettoreNULL con AsincronoAsimmetrico modalita Sincrona*/
	public static final String PORTA_DELEGATA_PD_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_CONNETTORE_NULL = "ConnettorePDAsincronoAsimmetrico_richiestaSincronaNULL";
	/** Porte Delegate per Performance test: ConnettoreNULL con AsincronoAsimmetricoCorrelato modalita Sincrona*/
	public static final String PORTA_DELEGATA_PD_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA_CONNETTORE_NULL = "ConnettorePDAsincronoAsimmetricoCorrelato_richiestaSincronaNULL";
	
	/** Porte Delegate per Performance test: test Overhead con OneWay*/
	public static final String PORTA_DELEGATA_PD_ONEWAY_OVERHEAD = "ConnettorePDOneWayOverhead";
	/** Porte Delegate per Performance test: test Overhead con Sincrono */
	public static final String PORTA_DELEGATA_PD_SINCRONO_OVERHEAD = "ConnettorePDSincronoOverhead";

	/** Porte Delegate per il test dei sul riconoscimento profilo (azione correlata): richiesta */
	public static final String PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RICHIESTA_MINISTERO_EROGATORE="Richiesta_testRiconoscimentoProfilo_MinisteroErogatore";
	/** Porte Delegate per il test dei sul riconoscimento profilo (azione correlata): risposta */
	public static final String PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RISPOSTA_MINISTERO_EROGATORE="Risposta_testRiconoscimentoProfilo_MinisteroErogatore";
	/** Porte Delegate per il test dei sul riconoscimento profilo (azione correlata): richiesta */
	public static final String PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RICHIESTA_MINISTERO_EROGATORE_LINEE_GUIDA="Richiesta_testRiconoscimentoProfilo_MinisteroErogatoreLineeGuida";
	/** Porte Delegate per il test dei sul riconoscimento profilo (azione correlata): risposta */
	public static final String PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RISPOSTA_MINISTERO_EROGATORE_LINEE_GUIDA="Risposta_testRiconoscimentoProfilo_MinisteroErogatoreLineeGuida";
	/** Porte Delegate per il test dei sul riconoscimento profilo (azione correlata): richiesta */
	public static final String PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RICHIESTA_MINISTERO_TMP="Richiesta_testRiconoscimentoProfilo_MinisteroTmp";
	/** Porte Delegate per il test dei sul riconoscimento profilo (azione correlata): risposta */
	public static final String PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RISPOSTA_MINISTERO_TMP="Risposta_testRiconoscimentoProfilo_MinisteroTmp";
	/** Porte Delegate per le Funzionalita' E-Gov CooperazioneAffidabile */
	public static final String PORTA_DELEGATA_COOPERAZIONE_AFFIDABILE_LINEE_GUIDA="CooperazioneAffidabile_MinisteroErogatoreLineeGuida";
	/** Porte Delegate per il test dei profili di collaborazione: Collaborazione */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_COLLABORAZIONE_LINEE_GUIDA="CooperazioneConIDCollaborazione_MinisteroErogatoreLineeGuida";
	
	/** Porte Delegate per IntegrationManagerMessageBox */
	public static final String PORTA_DELEGATA_MESSAGE_BOX="HelloWorldIntegrationManager";
	/** Porte Delegate per IntegrationManagerMessageBox */
	public static final String PORTA_DELEGATA_MESSAGE_BOX_GET_MESSAGE1="HelloWorldIntegrationManager2";
	/** Porte Delegate per IntegrationManagerMessageBox */
	public static final String PORTA_DELEGATA_MESSAGE_BOX_GET_MESSAGE2="HelloWorldIntegrationManager2";
	/** Porte Delegate per IntegrationManager Invocazione per riferimento */
	public static final String PORTA_DELEGATA_MESSAGE_BOX_INVOCAZIONE_PER_RIFERIMENTO="CooperazioneSincronaRiferimentoMessaggio";
	/** Porte Delegate per IntegrationManagerMessageBox con RicezioneRispostaAsincrona in profilo Asincrono */
	public static final String PORTA_DELEGATA_RICEZIONE_RISPOSTA_ASINCRONA="RicezioneRispostaAsincrona";
	
	
	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione WSDL */
	public static final String PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA="PDValWSDL";
	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione WSDL in warning only mode */
	public static final String PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA_WARNING_ONLY="PDValWSDL_WarningOnly";
	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione WSDL specifica per richiesta asincrona simmetrica */
	public static final String PORTA_DELEGATA_VALIDAZIONE_WSDL_RICHIESTA_ASINCRONA_SIMMETRICA="AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral";
	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione WSDL specifica per risposta asincrona simmetrica */
	public static final String PORTA_DELEGATA_VALIDAZIONE_WSDL_RISPOSTA_ASINCRONA_SIMMETRICA="EsitoAggiornamentoAsincronoSimmetricoWrappedDocumentLiteral";
	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione WSDL specifica per richiesta asincrona asimmetrica */
	public static final String PORTA_DELEGATA_VALIDAZIONE_WSDL_RICHIESTA_ASINCRONA_ASIMMETRICA="AggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral";
	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione WSDL specifica per richiesta-stato asincrona asimmetrica */
	public static final String PORTA_DELEGATA_VALIDAZIONE_WSDL_RICHIESTA_STATO_ASINCRONA_ASIMMETRICA="EsitoAggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral";

	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione OpenSPCoop */
	public static final String PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA="PDValOpenSPCoop";
	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione OpenSPCoop specifica per richiesta asincrona simmetrica */
	public static final String PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_RICHIESTA_ASINCRONA_SIMMETRICA="AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral_ValidazioneOpenSPCoop";
	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione OpenSPCoop specifica per risposta asincrona simmetrica */
	public static final String PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_RISPOSTA_ASINCRONA_SIMMETRICA="EsitoAggiornamentoAsincronoSimmetricoWrappedDocumentLiteral_ValidazioneOpenSPCoop";
	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione OpenSPCoop specifica per richiesta asincrona asimmetrica */
	public static final String PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_RICHIESTA_ASINCRONA_ASIMMETRICA="AggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral_ValidazioneOpenSPCoop";
	/** Porte Delegate per il test sulla validazione dei contenuti applicativi: Validazione OpenSPCoop specifica per richiesta-stato asincrona asimmetrica */
	public static final String PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_RICHIESTA_STATO_ASINCRONA_ASIMMETRICA="EsitoAggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral_ValidazioneOpenSPCoop";

	
	/** Porte Delegate per il test sulla gestione degli errori per il profilo oneway */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ONEWAY="ConnettorePdDErratoOneway";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo oneway con riscontri*/
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ONEWAY_RISCONTRI="ConnettorePdDErratoOnewayRiscontri";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo oneway statelsss */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ONEWAY_STATELESS="ConnettorePdDErratoOnewayStateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo oneway con riscontri*/
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ONEWAY_RISCONTRI_STATELESS="ConnettorePdDErratoOnewayRiscontriStateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo sincrono */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SINCRONO="ConnettorePdDErratoSincrono";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo sincrono stateless */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SINCRONO_STATELESS="ConnettorePdDErratoSincronoStateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoSimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA="ConnettorePdDErratoAsincronoSimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoSimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS="ConnettorePdDErratoAsincronoSimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoSimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA="ConnettorePdDErratoAsincronoSimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoSimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS="ConnettorePdDErratoAsincronoSimmetrico_richiestaSincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoAsimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA="ConnettorePdDErratoAsincronoAsimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoAsimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS="ConnettorePdDErratoAsincronoAsimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoAsimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA="ConnettorePdDErratoAsincronoAsimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoAsimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS="ConnettorePdDErratoAsincronoAsimmetrico_richiestaSincrona_Stateless";
	
	/** Porte Delegate per il test sulla gestione degli errori per il profilo oneway */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ONEWAY="ConnettoreSAErratoOneway";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo oneway */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ONEWAY_STATELESS="ConnettoreSAErratoOnewayStateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo sincrono */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO="ConnettoreSAErratoSincrono";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo sincrono */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS="ConnettoreSAErratoSincronoStateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoSimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA="ConnettoreSAErratoAsincronoSimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoSimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS="ConnettoreSAErratoAsincronoSimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoSimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA="ConnettoreSAErratoAsincronoSimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoSimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS="ConnettoreSAErratoAsincronoSimmetrico_richiestaSincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoAsimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA="ConnettoreSAErratoAsincronoAsimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoAsimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS="ConnettoreSAErratoAsincronoAsimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoAsimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA="ConnettoreSAErratoAsincronoAsimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo asincronoAsimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS="ConnettoreSAErratoAsincronoAsimmetrico_richiestaSincrona_Stateless";
		
	/** Porte Delegate per il test sulla gestione degli errori per il profilo sincrono stateless: connect timed out */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_PDD_SINCRONO_STATELESS_CONNECT_TIMED_OUT="ConnettorePdDErratoConnectTimedOut";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo sincrono stateless: connect timed out */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS_CONNECT_TIMED_OUT="ConnettoreSAErratoConnectTimedOut";
	
	/** Porte Delegate per il test sulla gestione degli errori per il profilo sincrono stateless: connection read timed out */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_PDD_SINCRONO_STATELESS_CONNECTION_READ_TIMEOUT="ConnettorePdDErratoReadTimeout";
	/** Porte Delegate per il test sulla gestione degli errori per il profilo sincrono stateless: connection read out */
	public static final String PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS_CONNECTION_READ_TIMEOUT="ConnettoreSAErratoReadTimeout";
	
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo oneway */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ONEWAY="testSOAPFaultApplicativoOneway";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo oneway stateless */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ONEWAY_STATELESS="testSOAPFaultApplicativoOnewayStateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo sincrono */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_SINCRONO="testSOAPFaultApplicativoSincrono";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo sincrono stateless */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_SINCRONO_STATELESS="testSOAPFaultApplicativoSincronoStateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo asincronoSimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA="testSOAPFaultApplicativoAsincronoSimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo asincronoSimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS="testSOAPFaultApplicativoAsincronoSimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo asincronoSimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA="testSOAPFaultApplicativoAsincronoSimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo asincronoSimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS="testSOAPFaultApplicativoAsincronoSimmetrico_richiestaSincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo asincronoAsimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA="testSOAPFaultApplicativoAsincronoAsimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo asincronoAsimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS="testSOAPFaultApplicativoAsincronoAsimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo asincronoAsimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA="testSOAPFaultApplicativoAsincronoAsimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dal servizio applicativo per il profilo asincronoAsimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS="testSOAPFaultApplicativoAsincronoAsimmetrico_richiestaSincrona_Stateless";

	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo oneway */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ONEWAY="testSOAPFaultServerOneway";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo oneway con riscontri */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ONEWAY_RISCONTRI="testSOAPFaultServerOnewayRiscontri";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo oneway stateless */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ONEWAY_STATELESS="testSOAPFaultServerOnewayStateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo oneway con riscontri trasmissione sincrona*/
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ONEWAY_RISCONTRI_STATELESS="testSOAPFaultServerOnewayRiscontriStateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo sincrono */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_SINCRONO="testSOAPFaultServerSincrono";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo sincrono stateless */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_SINCRONO_STATELESS="testSOAPFaultServerSincronoStateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo asincronoSimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA="testSOAPFaultServerAsincronoSimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo asincronoSimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS="testSOAPFaultServerAsincronoSimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo asincronoSimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA="testSOAPFaultServerAsincronoSimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo asincronoSimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS="testSOAPFaultServerAsincronoSimmetrico_richiestaSincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo asincronoAsimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA="testSOAPFaultServerAsincronoAsimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo asincronoAsimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS="testSOAPFaultServerAsincronoAsimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo asincronoAsimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA="testSOAPFaultServerAsincronoAsimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per SOAPFault provocato dalla PdD destinazione per il profilo asincronoAsimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS="testSOAPFaultServerAsincronoAsimmetrico_richiestaSincrona_Stateless";
	
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo oneway */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ONEWAY="testSPCoopErroreProcessamentoOneway";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo oneway  stateless */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ONEWAY_STATELESS="testSPCoopErroreProcessamentoOnewayStateless";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo sincrono */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_SINCRONO="testSPCoopErroreProcessamentoSincrono";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo sincrono stateless */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_SINCRONO_STATELESS="testSPCoopErroreProcessamentoSincronoStateless";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo asincronoSimmetrico richiesta asincrona  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA="testSPCoopErroreProcessamentoAsincronoSimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo asincronoSimmetrico richiesta asincrona  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS="testSPCoopErroreProcessamentoAsincronoSimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo asincronoSimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA="testSPCoopErroreProcessamentoAsincronoSimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo asincronoSimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS="testSPCoopErroreProcessamentoAsincronoSimmetrico_richiestaSincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo asincronoAsimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA="testSPCoopErroreProcessamentoAsincronoAsimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo asincronoAsimmetrico richiesta asincrona */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS="testSPCoopErroreProcessamentoAsincronoAsimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo asincronoAsimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA="testSPCoopErroreProcessamentoAsincronoAsimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di processamento per il profilo asincronoAsimmetrico richiesta sincrona */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS="testSPCoopErroreProcessamentoAsincronoAsimmetrico_richiestaSincrona_Stateless";
	
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo oneway */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ONEWAY="testSPCoopErroreValidazioneOneway";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo oneway stateless */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ONEWAY_STATELESS="testSPCoopErroreValidazioneOnewayStateless";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_SINCRONO="testSPCoopErroreValidazioneSincrono";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo  stateless */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_SINCRONO_STATELESS="testSPCoopErroreValidazioneSincronoStateless";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA="testSPCoopErroreValidazioneAsincronoSimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS="testSPCoopErroreValidazioneAsincronoSimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA="testSPCoopErroreValidazioneAsincronoSimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS="testSPCoopErroreValidazioneAsincronoSimmetrico_richiestaSincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA="testSPCoopErroreValidazioneAsincronoAsimmetrico_richiestaAsincrona";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS="testSPCoopErroreValidazioneAsincronoAsimmetrico_richiestaAsincrona_Stateless";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA="testSPCoopErroreValidazioneAsincronoAsimmetrico_richiestaSincrona";
	/** Porte Delegate per il test sulla gestione degli errori per buste egov con errori di validazione per il profilo  */
	public static final String PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS="testSPCoopErroreValidazioneAsincronoAsimmetrico_richiestaSincrona_Stateless";
	
	
	
	/** Porte Delegate per il test sul tunnel SOAP: TunnelSOAP */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP="TunnelSOAP";
	/** Porte Delegate per il test sul tunnel SOAP: TunnelSOAPMultipartRelatedMIME */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_MULTIPART_RELATED_MIME="TunnelSOAPMultipartRelatedMIME";
	/** Porte Delegate per il test sul tunnel SOAP: TunnelSOAPWithAttachmentApplicationOpenSPCoop */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP="TunnelSOAPWithAttachmentApplicationOpenSPCoop";
	/** Porte Delegate per il test sul tunnel SOAP: TunnelSOAPWithAttachmentCustomMimeType */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ATTACHMENT_CUSTOM_MIME_TYPE="TunnelSOAPWithAttachmentCustomMimeType";
	/** Porte Delegate per il test sul tunnel SOAP: NotificaTunnelSOAP */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP="NotificaTunnelSOAP";
	/** Porte Delegate per il test sul tunnel SOAP: NotificaTunnelSOAPMultipartRelatedMIME */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP_MULTIPART_RELATED_MIME="NotificaTunnelSOAPMultipartRelatedMIME";
	/** Porte Delegate per il test sul tunnel SOAP: NotificaTunnelSOAPWithAttachmentApplicationOpenSPCoop */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP="NotificaTunnelSOAPWithAttachmentApplicationOpenSPCoop";
	/** Porte Delegate per il test sul tunnel SOAP: NotificaTunnelSOAPWithAttachmentCustomMimeType */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP_CUSTOM_MIME_TYPE="NotificaTunnelSOAPWithAttachmentCustomMimeType";
	/** Porte Delegate per il test sulla gestione manifest egov disabilitata */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_GESTIONE_MANIFEST_DISABILITATA="GestioneManifestDisabilitata";
	/** Porte Delegate per il test sul tunnel SOAP: allega body */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ALLEGA_BODY="TunnelSOAPAllegaBody";
	/** Porte Delegate per il test sul tunnel SOAP: scarta body */
	public static final String PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_SCARTA_BODY="TunnelSOAPScartaBody";
	
	
	/** Porte Delegate per test consegna in ordine */
	public static final String PORTA_DELEGATA_CONSEGNA_IN_ORDINE="CooperazioneConConsegnaInOrdineTestConCongelamento";
	
	/** Porte Delegate per controllo versionamento accordi: SPC/MinisteroFruitore:ASComunicazioneVariazione:1.0  */
	public static final String PORTA_DELEGATA_VERSIONAMENTO_SOGGREFERENTE_NOME_VERSIONE="VersionamentoAccordo_SoggettoReferente_Nome_Versione";
	/** Porte Delegate per controllo versionamento accordi: SPC/MinisteroFruitore:ASComunicazioneVariazione*/
	public static final String PORTA_DELEGATA_VERSIONAMENTO_SOGGREFERENTE_NOME="VersionamentoAccordo_SoggettoReferente_Nome";
	/** Porte Delegate per controllo versionamento accordi: ASComunicazioneVariazione:1.0 */
	public static final String PORTA_DELEGATA_VERSIONAMENTO_NOME_VERSIONE="VersionamentoAccordo_Nome_Versione";
	
	/** Porte Delegate per il test https: https_with_client_auth */
	public static final String PORTA_DELEGATA_HTTPS_WITH_CLIENT_AUTH="https_with_client_auth";
	/** Porte Delegate per il test https: https_with_client_auth_identita2 */
	public static final String PORTA_DELEGATA_HTTPS_WITH_CLIENT_AUTH_IDENTITA2="https_with_client_auth_identita2";
	/** Porte Delegate per il test https: https_with_client_auth_error */
	public static final String PORTA_DELEGATA_HTTPS_WITH_CLIENT_AUTH_ERROR="https_with_client_auth_error";
	/** Porte Delegate per il test https: https_without_client_auth */
	public static final String PORTA_DELEGATA_HTTPS_WITHOUT_CLIENT_AUTH="https_without_client_auth";
	/** Porte Delegate per il test https: https_ca_non_presente */
	public static final String PORTA_DELEGATA_HTTPS_CA_NON_PRESENTE="https_ca_non_presente";
	/** Porte Delegate per il test https: https_hostname_verify */
	public static final String PORTA_DELEGATA_HTTPS_HOSTNAME_VERIFY="https_hostname_verify";
	/** Porte Delegate per il test https:  https_sil_verify*/
	public static final String PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL="https_sil_verify";
	/** Porte Delegate per il test https:  https_sil_verify*/
	public static final String PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL_2="https_sil_verify_test2";
	/** Porte Delegate per il test https:  https_sil_consegna*/
	public static final String PORTA_DELEGATA_HTTPS_SIL_CONSEGNA="https_sil_consegna_contenuti_applicativi";
	
	/** Porte Delegate per il test sull'autorizzazione spcoop:  funzionante*/
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_FUNZIONANTE="AutorizzazioneSPCoopSoggetto1";
	/** Porte Delegate per il test sull'autorizzazione spcoop: spoofing rilevato */
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_SPOOFING_RILEVATO="AutorizzazioneSPCoopSpoofingRilevato";
	/** Porte Delegate per il test sull'autorizzazione spcoop: disabilitata */
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_DISABILITATA="AutorizzazioneSPCoopDisabilitata";
	/** Porte Delegate per il test sull'autorizzazione spcoop: disabilitata tramite fruizione */
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_DISABILITATA_TRAMITE_FRUIZIONE="AutorizzazioneSPCoopDisabilitataTramiteFruizione";
	/** Porte Delegate per il test sull'autorizzazione spcoop: spoofing rilevato tramite fruizione */
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_SPOOFING_RILEVATO_TRAMITE_FRUIZIONE="AutorizzazioneSPCoopSpoofingRilevatoTramiteFruizione";
	/** Porte Delegate per il test sull'autorizzazione spcoop:  fruitore non presente nella lista dei fruitori del servizio*/
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_FRUITORE_NON_PRESENTE="AutorizzazioneSPCoopSoggetto1Fallita";

	/** Porte Delegate per il test sull'autorizzazione per contenuto */
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SINCRONO_OK="AutorizzazioneContenutoOK";
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SINCRONO_KO="AutorizzazioneContenutoKO";
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SPCOOP_SINCRONO_OK="AutorizzazioneContenutoSPCoopOK";
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SPCOOP_SINCRONO_KO="AutorizzazioneContenutoSPCoopKO";
	
	/** Porta delegata per verifica ErroreApplicativoCNIPA */
	public static final String PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA="ErroreApplicativoCNIPA";
	/** Porte Delegate: ErroreApplicativoCNIPASOAPFaultIM */
	public static final String PORTA_DELEGATA_ERRORE_APPLICATIVO_SOAP_FAULT_IM="ErroreApplicativoCNIPASOAPFaultIM";
	/** Porte Delegate: ErroreApplicativoCNIPASOAPFaultSenzaDetails */
	public static final String PORTA_DELEGATA_ERRORE_APPLICATIVO_SOAP_FAULT_SENZA_DETAILS="ErroreApplicativoCNIPASOAPFaultSenzaDetails";
	
	/** Porta delegata per verifica richieste applicative scorrette (403): identificazione content based */
	public static final String PORTA_DELEGATA_CONTENT_BASED_EXAMPLE1 = "HelloWorldContentBasedExample1";
	/** Porta delegata per verifica richieste applicative scorrette (403): identificazione url based */
	public static final String PORTA_DELEGATA_URL_BASED_EXAMPLE1 = "HelloWorldURLBased";
	/** Porta delegata per verifica richieste applicative scorrette (403): identificazione url based */
	public static final String PORTA_DELEGATA_URL_FORM_BASED_EXAMPLE1 = "HelloWorldURLBasedForm";
	/** Porta delegata per verifica richieste applicative scorrette (403): identificazione input based */
	public static final String PORTA_DELEGATA_INPUT_BASED_EXAMPLE1 = "HelloWorldInputBased";
	/** Porta delegata per verifica richieste applicative scorrette (404): autorizzazione */	
	public static final String PORTA_DELEGATA_AUTORIZZAZIONE_EXAMPLE = "HelloWorldAutorizzazione";
	/** Porta delegata per verifica richieste applicative scorrette (408): servizio correlato non esistente */	
	public static final String PORTA_DELEGATA_SERVIZIO_ASINCRONO_SIMMETRICO_CORRELATO_NON_ESISTENTE = "ServizioASCheNonPossiedeIlCorrelato";
	public static final String PORTA_DELEGATA_SERVIZIO_ASINCRONO_ASIMMETRICO_CORRELATO_NON_ESISTENTE = "ServizioAACheNonPossiedeIlCorrelato";
	/** Porta delegata per verifica richieste applicative scorrette (410): servizio asincrono simmetrico invocato tramite PD senza autenticazione */	
	public static final String PORTA_DELEGATA_SERVIZIO_ASINCRONO_SIMMETRICO_PD_SENZA_AUTENTICAZIONE = "ServizioAsincronoSimmetricoSenzaAutenticazione";
	/** Porta delegata per verifica richieste applicative scorrette (414): consegna in ordine con profilo sincrono */	
	public static final String PORTA_DELEGATA_CONSEGNA_IN_ORDINE_PROFILO_SINCRONO = "ConsegnaInOrdineProfiloDiversoOneway";
	/** Porta delegata per verifica richieste applicative scorrette (415): consegna in ordine con configurazione errata, confermaRicezione mancante */	
	public static final String PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_CONFERMA_RICEZIONE = "ConsegnaInOrdineConfErrataTest1";
	/** Porta delegata per verifica richieste applicative scorrette (415): consegna in ordine con configurazione errata, filtroDuplicati mancante */	
	public static final String PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_FILTRO_DUPLICATI = "ConsegnaInOrdineConfErrataTest2";
	/** Porta delegata per verifica richieste applicative scorrette (415): consegna in ordine con configurazione errata, idCollaborazione mancante */	
	public static final String PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_ID_COLLABORAZIONE = "ConsegnaInOrdineConfErrataTest3";
	/** Porta delegata per verifica richieste applicative scorrette (417): validazione applicativa senza xsd */	
	public static final String PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_XSD = "ValidazioneApplicativaSenzaXSD_xsd";
	/** Porta delegata per verifica richieste applicative scorrette (417): validazione applicativa senza xsd */	
	public static final String PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_WSDL = "ValidazioneApplicativaSenzaXSD_wsdl";
	/** Porta delegata per verifica richieste applicative scorrette (417): validazione applicativa senza xsd */	
	public static final String PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_OPENSPCOOP = "ValidazioneApplicativaSenzaXSD_openspcoop";
	/** Porta delegata per verifica richieste applicative scorrette (419): validazione applicativa risposta fallita */	
	public static final String PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_RISPOSTA_FALLITA = "ValidazioneApplicativaRispostaErrata";
	/** Porta delegata per verifica richieste applicative scorrette (423): servizio non invocabile senza azione */	
	public static final String PORTA_DELEGATA_SERVIZIO_NON_INVOCABILE_SENZA_AZIONE = "ServizioNonInvocabileSenzaAzione";
	
	
	/** Porta delegata per verifica connettore JMS: text su queue */
	public static final String PORTA_DELEGATA_JMS_TEXT_QUEUE = "sendTextOnQueueJMS";
	/** Porta delegata per verifica connettore JMS: bytes su queue */
	public static final String PORTA_DELEGATA_JMS_BYTES_QUEUE = "sendBytesOnQueueJMS";
	/** Porta delegata per verifica connettore JMS: text su topic */
	public static final String PORTA_DELEGATA_JMS_TEXT_TOPIC = "sendTextOnTopicJMS";
	/** Porta delegata per verifica connettore JMS: bytes su topic */
	public static final String PORTA_DELEGATA_JMS_BYTES_TOPIC = "sendBytesOnTopicJMS";
	/** Porta delegata per verifica connettore JMS: propagazione info egov su queue */
	public static final String PORTA_DELEGATA_JMS_INFO_EGOV_QUEUE = "PropagazioneInfoEGovOnQueue";
	/** Porta delegata per verifica connettore JMS: propagazione info egov su topic */
	public static final String PORTA_DELEGATA_JMS_INFO_EGOV_TOPIC = "PropagazioneInfoEGovOnTopic";
	/** Porta delegata per verifica connettore JMS: sbustamento soap su queue */
	public static final String PORTA_DELEGATA_JMS_SBUSTAMENTO_SOAP_QUEUE = "sendTextOnQueueJMS_Sbustato";
	/** Porta delegata per verifica connettore JMS: sbustamento soap su topic */
	public static final String PORTA_DELEGATA_JMS_SBUSTAMENTO_SOAP_TOPIC = "sendTextOnTopicJMS_Sbustato";
	
	/** Porta delegata per verifica connettore SAAJ: oneway */
	public static final String PORTA_DELEGATA_SAAJ_ONEWAY = "SAAJOneway";
	/** Porta delegata per verifica connettore SAAJ: sincrono */
	public static final String PORTA_DELEGATA_SAAJ_SINCRONO = "SAAJSincrono";
	
	/** Porta delegata per verifica connettore HTTPCORE: oneway */
	public static final String PORTA_DELEGATA_HTTPCORE_ONEWAY = "HTTPCOREOneway";
	/** Porta delegata per verifica connettore HTTPCORE: sincrono */
	public static final String PORTA_DELEGATA_HTTPCORE_SINCRONO = "HTTPCORESincrono";
	
	/** Porta delegata per verifica url prefix rewriter:  TestUrlPrefixRewriter1*/
	public static final String PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_1 = "TestUrlPrefixRewriter1";
	public static final String PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_2 = "TestUrlPrefixRewriter2";
	public static final String PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_3 = "TestUrlPrefixRewriter3";
	public static final String PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_4 = "TestUrlPrefixRewriter4";
	public static final String PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_5 = "TestUrlPrefixRewriter5";
	public static final String PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_6 = "TestUrlPrefixRewriter6";
	public static final String PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_7 = "TestUrlPrefixRewriter7";
	public static final String PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_8 = "TestUrlPrefixRewriter8";
	public static final String PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_9 = "TestUrlPrefixRewriter9";
	public static final String PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_10 = "TestUrlPrefixRewriter10";
	
	/** Porta delegata per verifica errore processamento 5XX: TestErroreProcessamento5XX */
	public static final String PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX = "TestErroreProcessamento5XX";
	
	/** Porte Delegate per LocalForward: Casi Ok */
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_ONEWAY_STATEFUL = "HelloWorld_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_ONEWAY_STATELESS = "HelloWorld_Stateless_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_SINCRONO = "CooperazioneSincrona_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_ONEWAY_STATELESS_PA_STATEFUL = "HelloWorld_Stateless2_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_ONEWAY_INTEGRATION_MANAGER = "HelloWorldIntegrationManager_LocalForward";
	/** Porte Delegate per LocalForward: Casi errore */
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_ASINCRONI = "CooperazioneAsincronaAsimmetrica_richiestaAsincrona_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_SINCRONO_STATEFUL = "CooperazioneSincrona_Stateful_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_SOGGETTO_NON_LOCALE = "SoggettoLocaleNonEsistente_LocalForward";
	/** Porte Delegate per LocalForward: WSS Encrypt */
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_WSS_ENCRYPT_REQUEST = "CooperazioneSincrona_WSSEncrypt_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_WSS_ENCRYPT_DECRYPT_REQUEST = "CooperazioneSincrona_WSSEncryptDecrypt_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_WSS_DECRYPT_REQUEST = "CooperazioneSincrona_WSSEncryptDecryptSenzaEncrypt_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_WSS_ENCRYPT_RESPONSE = "CooperazioneSincrona_WSSEncryptResponse_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_WSS_ENCRYPT_DECRYPT_RESPONSE = "CooperazioneSincrona_WSSEncryptResponseDecrypt_LocalForward";
	/** Porte Delegate per LocalForward: WSS Signature */
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_WSS_SIGNATURE_REQUEST = "CooperazioneSincrona_WSSSignature_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_WSS_SIGNATURE_VERIFY_REQUEST = "CooperazioneSincrona_WSSSignatureVerifica";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_WSS_VERIFY_REQUEST = "CooperazioneSincrona_WSSSignatureVerificaSenzaFirma_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_WSS_SIGNATURE_RESPONSE = "CooperazioneSincrona_WSSSignatureResponse_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_WSS_SIGNATURE_VERIFY_RESPONSE = "CooperazioneSincrona_WSSSignatureResponseVerificata_LocalForward";
	/** Porte Delegate per LocalForward: Casi Errore */
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_CONNETTORE_ERRATO_STATEFUL = "ConnettoreSAErratoOneway_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_CONNETTORE_ERRATO_STATELESS = "ConnettoreSAErratoOnewayStateless_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_SOAP_FAULT_STATEFUL = "testSOAPFaultApplicativoOneway_LocalForward";
	public static final String PORTA_DELEGATA_LOCAL_FORWARD_SOAP_FAULT_STATELESS = "testSOAPFaultApplicativoOnewayStateless_LocalForward";
	
	/** ENTITA SPCOOP: PortaDominio MinistroErogatoreSPCoopIT */
	public static final String SPCOOP_PORTA_DOMINIO="SPCoopIT";
	
	/** ENTITA SPCOOP: Tipo Soggetto Fruitore */
	public static final String SPCOOP_TIPO_SOGGETTO_FRUITORE="SPC";
	/** ENTITA SPCOOP: Nome Soggetto Fruitore */
	public static final String SPCOOP_NOME_SOGGETTO_FRUITORE="MinisteroFruitore";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String SPCOOP_IDPORTA_SOGGETTO_FRUITORE="MinisteroFruitoreSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_FRUITORE = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_FRUITORE);
		
	/** ENTITA SPCOOP: Tipo Soggetto Erogatore */
	public static final String SPCOOP_TIPO_SOGGETTO_EROGATORE="SPC";
	/** ENTITA SPCOOP: Nome Soggetto Erogatore */
	public static final String SPCOOP_NOME_SOGGETTO_EROGATORE="MinisteroErogatore";
	/** ENTITA SPCOOP: IdPorta Soggetto Erogatore */
	public static final String SPCOOP_IDPORTA_SOGGETTO_EROGATORE="MinisteroErogatoreSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_EROGATORE = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_EROGATORE);
		
	/** ENTITA SPCOOP: Tipo Soggetto Fruitore */
	public static final String SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11="SPC";
	/** ENTITA SPCOOP: Nome Soggetto Fruitore */
	public static final String SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11="MinisteroFruitoreLineeGuida";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String SPCOOP_IDPORTA_SOGGETTO_FRUITORE_LINEE_GUIDA_11="MinisteroFruitoreLineeGuidaSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_FRUITORE_LINEE_GUIDA_11);
	
	/** ENTITA SPCOOP: Tipo Soggetto Erogatore */
	public static final String SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11="SPC";
	/** ENTITA SPCOOP: Nome Soggetto Erogatore */
	public static final String SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11="MinisteroErogatoreLineeGuida";
	/** ENTITA SPCOOP: IdPorta Soggetto Erogatore */
	public static final String SPCOOP_IDPORTA_SOGGETTO_EROGATORE_LINEE_GUIDA_11="MinisteroErogatoreLineeGuidaSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_EROGATORE_LINEE_GUIDA_11);
	
	/** ENTITA SPCOOP: Tipo Soggetto Fruitore */
	public static final String SPCOOP_TIPO_SOGGETTO_FRUITORE_SOAPBOX="SPC";
	/** ENTITA SPCOOP: Nome Soggetto Fruitore */
	public static final String SPCOOP_NOME_SOGGETTO_FRUITORE_SOAPBOX="SoggettoEsempioSoapBoxFruitore";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String SPCOOP_IDPORTA_SOGGETTO_FRUITORE_SOAPBOX="SoggettoEsempioSoapBoxFruitoreSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_FRUITORE_SOAPBOX = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_SOAPBOX, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_SOAPBOX, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_FRUITORE_SOAPBOX);
	
	/** ENTITA SPCOOP: Tipo Soggetto Erogatore */
	public static final String SPCOOP_TIPO_SOGGETTO_EROGATORE_SOAPBOX="SPC";
	/** ENTITA SPCOOP: Nome Soggetto Erogatore */
	public static final String SPCOOP_NOME_SOGGETTO_EROGATORE_SOAPBOX="SoggettoEsempioSoapBoxErogatore";
	/** ENTITA SPCOOP: IdPorta Soggetto Erogatore */
	public static final String SPCOOP_IDPORTA_SOGGETTO_EROGATORE_SOAPBOX="SoggettoEsempioSoapBoxErogatoreSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_EROGATORE_SOAPBOX = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_SOAPBOX, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_SOAPBOX, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_EROGATORE_SOAPBOX);

	/** ENTITA SPCOOP: Tipo Soggetto TMP */
	public static final String SPCOOP_TIPO_SOGGETTO_TMP="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TMP */
	public static final String SPCOOP_NOME_SOGGETTO_TMP="MinisteroTmp";
	/** ENTITA SPCOOP: IdPorta Soggetto TMP */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TMP="MinisteroTmpSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TMP = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TMP, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TMP, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TMP);
	
	/** ENTITA SPCOOP: Tipo Soggetto Connettore Errato */
	public static final String SPCOOP_TIPO_SOGGETTO_CONNETTORE_ERRATO="SPC";
	/** ENTITA SPCOOP: Nome Soggetto Connettore Errato */
	public static final String SPCOOP_NOME_SOGGETTO_CONNETTORE_ERRATO="SoggettoConnettoreErrato";
	/** ENTITA SPCOOP: IdPorta Soggetto Connettore Errato */
	public static final String SPCOOP_IDPORTA_SOGGETTO_CONNETTORE_ERRATO="SoggettoConnettoreErratoSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_CONNETTORE_ERRATO = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_CONNETTORE_ERRATO, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_CONNETTORE_ERRATO, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_CONNETTORE_ERRATO);
	
	/** ENTITA SPCOOP: Tipo Soggetto PdD SoapFault  */
	public static final String SPCOOP_TIPO_SOGGETTO_SOAP_FAULT_PDD_DEST="SPC";
	/** ENTITA SPCOOP: Nome Soggetto PdD SoapFault */
	public static final String SPCOOP_NOME_SOGGETTO_SOAP_FAULT_PDD_DEST="SoggettoConnettoreSOAPFaultServer";
	/** ENTITA SPCOOP: IdPorta Soggetto PdD SoapFault */
	public static final String SPCOOP_IDPORTA_SOGGETTO_SOAP_FAULT_PDD_DEST="SoggettoConnettoreSOAPFaultServerSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_SOAP_FAULT_PDD_DEST = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_SOAP_FAULT_PDD_DEST, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_SOAP_FAULT_PDD_DEST, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_SOAP_FAULT_PDD_DEST);

	/** ENTITA SPCOOP: Tipo Soggetto Errore Processamento  */
	public static final String SPCOOP_TIPO_SOGGETTO_ERRORE_PROCESSAMENTO="SPC";
	/** ENTITA SPCOOP: Nome Soggetto Errore Processamento */
	public static final String SPCOOP_NOME_SOGGETTO_ERRORE_PROCESSAMENTO="SoggettoConfigurazionePdDErrata";
	/** ENTITA SPCOOP: IdPorta Soggetto Errore Processamento */
	public static final String SPCOOP_IDPORTA_SOGGETTO_ERRORE_PROCESSAMENTO="SoggettoConfigurazionePdDErrataSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_ERRORE_PROCESSAMENTO = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_ERRORE_PROCESSAMENTO, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_ERRORE_PROCESSAMENTO, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_ERRORE_PROCESSAMENTO);

	/** ENTITA SPCOOP: Tipo Soggetto test consegna in ordine  */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_CONSEGNA_IN_ORDINE="SPC";
	/** ENTITA SPCOOP: Nome Soggetto test consegna in ordine */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_CONSEGNA_IN_ORDINE="SoggettoTestConsegnaInOrdine";
	/** ENTITA SPCOOP: IdPorta Soggetto test consegna in ordine */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_CONSEGNA_IN_ORDINE="SoggettoTestConsegnaInOrdineSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_CONSEGNA_IN_ORDINE);

	/** ENTITA SPCOOP: Tipo Soggetto 1 */
	public static final String SPCOOP_TIPO_SOGGETTO_1="SPC";
	/** ENTITA SPCOOP: Nome Soggetto 1 */
	public static final String SPCOOP_NOME_SOGGETTO_1="Soggetto1";
	/** ENTITA SPCOOP: IdPorta Soggetto 1 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_1="Soggetto1SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_1 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_1, CostantiTestSuite.SPCOOP_NOME_SOGGETTO_1, 
			CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_1);

	/** ENTITA SPCOOP: Tipo Soggetto 2 */
	public static final String SPCOOP_TIPO_SOGGETTO_2="SPC";
	/** ENTITA SPCOOP: Nome Soggetto 2 */
	public static final String SPCOOP_NOME_SOGGETTO_2="Soggetto2";
	/** ENTITA SPCOOP: IdPorta Soggetto 2 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_2="Soggetto2SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_2 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_2, CostantiTestSuite.SPCOOP_NOME_SOGGETTO_2, 
			CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_2);

	/** ENTITA SPCOOP: Tipo Soggetto Non autenticato */
	public static final String SPCOOP_TIPO_SOGGETTO_NON_AUTENTICATO="SPC";
	/** ENTITA SPCOOP: Nome Soggetto Non autenticato */
	public static final String SPCOOP_NOME_SOGGETTO_NON_AUTENTICATO="SoggettoNonAutenticato";
	/** ENTITA SPCOOP: IdPorta Soggetto Non autenticato */
	public static final String SPCOOP_IDPORTA_SOGGETTO_NON_AUTENTICATO="SoggettoNonAutenticatoSPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_NON_AUTENTICATO = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_NON_AUTENTICATO, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_NON_AUTENTICATO, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_NON_AUTENTICATO);
	
	/** ENTITA SPCOOP: Tipo Soggetto TestUrlPrefixRewriter1 */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_1="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TestUrlPrefixRewriter1 */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_1="TestUrlPrefixRewriter1";
	/** ENTITA SPCOOP: IdPorta Soggetto TestUrlPrefixRewriter1 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_1="TestUrlPrefixRewriter1SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_1 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_1, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_1, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_1);
	
	/** ENTITA SPCOOP: Tipo Soggetto TestUrlPrefixRewriter2 */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_2="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TestUrlPrefixRewriter2 */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_2="TestUrlPrefixRewriter2";
	/** ENTITA SPCOOP: IdPorta Soggetto TestUrlPrefixRewriter2 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_2="TestUrlPrefixRewriter2SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_2 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_2, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_2, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_2);
	
	/** ENTITA SPCOOP: Tipo Soggetto TestUrlPrefixRewriter3 */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_3="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TestUrlPrefixRewriter3 */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_3="TestUrlPrefixRewriter3";
	/** ENTITA SPCOOP: IdPorta Soggetto TestUrlPrefixRewriter3 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_3="TestUrlPrefixRewriter3SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_3 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_3, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_3, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_3);
	
	/** ENTITA SPCOOP: Tipo Soggetto TestUrlPrefixRewriter4 */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_4="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TestUrlPrefixRewriter4 */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_4="TestUrlPrefixRewriter4";
	/** ENTITA SPCOOP: IdPorta Soggetto TestUrlPrefixRewriter4 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_4="TestUrlPrefixRewriter4SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_4 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_4, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_4, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_4);
		
	/** ENTITA SPCOOP: Tipo Soggetto TestUrlPrefixRewriter5 */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_5="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TestUrlPrefixRewriter5 */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_5="TestUrlPrefixRewriter5";
	/** ENTITA SPCOOP: IdPorta Soggetto TestUrlPrefixRewriter5 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_5="TestUrlPrefixRewriter5SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_5 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_5, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_5, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_5);
	
	/** ENTITA SPCOOP: Tipo Soggetto TestUrlPrefixRewriter6 */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_6="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TestUrlPrefixRewriter6 */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_6="TestUrlPrefixRewriter6";
	/** ENTITA SPCOOP: IdPorta Soggetto TestUrlPrefixRewriter6 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_6="TestUrlPrefixRewriter6SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_6 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_6, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_6, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_6);
	
	/** ENTITA SPCOOP: Tipo Soggetto TestUrlPrefixRewriter7 */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_7="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TestUrlPrefixRewriter7 */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_7="TestUrlPrefixRewriter7";
	/** ENTITA SPCOOP: IdPorta Soggetto TestUrlPrefixRewriter7 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_7="TestUrlPrefixRewriter7SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_7 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_7, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_7, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_7);
	
	/** ENTITA SPCOOP: Tipo Soggetto TestUrlPrefixRewriter8 */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_8="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TestUrlPrefixRewriter8 */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_8="TestUrlPrefixRewriter8";
	/** ENTITA SPCOOP: IdPorta Soggetto TestUrlPrefixRewriter8 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_8="TestUrlPrefixRewriter8SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_8 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_8, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_8, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_8);
	
	/** ENTITA SPCOOP: Tipo Soggetto TestUrlPrefixRewriter9 */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_9="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TestUrlPrefixRewriter9 */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_9="TestUrlPrefixRewriter9";
	/** ENTITA SPCOOP: IdPorta Soggetto TestUrlPrefixRewriter9 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_9="TestUrlPrefixRewriter9SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_9 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_9, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_9, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_9);
	
	/** ENTITA SPCOOP: Tipo Soggetto TestUrlPrefixRewriter10 */
	public static final String SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_10="SPC";
	/** ENTITA SPCOOP: Nome Soggetto TestUrlPrefixRewriter10 */
	public static final String SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_10="TestUrlPrefixRewriter10";
	/** ENTITA SPCOOP: IdPorta Soggetto TestUrlPrefixRewriter10 */
	public static final String SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_10="TestUrlPrefixRewriter10SPCoopIT";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_10 = new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_10, 
			CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_10, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_TEST_URL_PREFIX_REWRITER_10);
	
	
	/** ENTITA SPCOOP: Versione Servizio */
	public static final Integer SPCOOP_VERSIONE_SERVIZIO_DEFAULT=1;
	
	
	/** ENTITA SPCOOP: Tipo Servizio OneWay */
	public static final String SPCOOP_TIPO_SERVIZIO_ONEWAY="SPC";
	/** ENTITA SPCOOP: Nome Servizio OneWay */
	public static final String SPCOOP_NOME_SERVIZIO_ONEWAY="ComunicazioneVariazione";
	/** ENTITA SPCOOP: Nome Azione Affidabile del Servizio OneWay con Consegna Affidabile */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_AFFIDABILE="Affidabile";
	/** ENTITA SPCOOP: Nome Azione Filtro Duplicati del Servizio OneWay con Consegna Filtro Duplicati */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI="FiltroDuplicati";
	/** ENTITA SPCOOP: Nome Azione Affidabile con Filtro Duplicati del Servizio OneWay con Consegna Affidabile e Filtro Duplicati */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_AFFIDABILE_CON_FILTRO_DUPLICATI="AffidabileConFiltroDuplicati";
	/** ENTITA SPCOOP: Nome Azione Affidabile con Scadenza del Servizio OneWay Affidabile con Scadenza */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_AFFIDABILE_CON_SCADENZA="AffidabileConScadenza";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio OneWay con WSSecurity */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_WSS="WSS";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio OneWay con WSSecurity */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_WSS_AUTORIZZAZIONE_KO="WSSNonAutorizzato";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio OneWay con WSSecurity con affidabile e filtro duplicati */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_WSS_AFFIDABILE_CON_FILTRO_DUPLICATI=
    	"AffidabileConFiltroDuplicatiWSS";
    /** ENTITA SPCOOP: Nome Azione del Servizio OneWay con IntegrationManager */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER="GetMessage";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per SoapFault */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_SOAP_FAULT_SA="testSoapFaultApplicativo";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per connettore errato SA */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONNETTORE_ERRATO_SA="testConnettoreErratoApplicativo";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay stateless */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_STATELESS="stateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay con trasmissione sincrona affidabile*/
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_STATELESS_AFFIDABILE="statelessConRiscontri";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per connettore errato SA modalita trasmissione sincrona */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONNETTORE_ERRATO_SA_TRASMISSIONE_SINCRONA="testConnettoreErratoApplicativoModalitaTrasmissioneSincrona";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per connettore errato SA stateless*/
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONNETTORE_ERRATO_SA_STATELESS="testConnettoreErratoApplicativoStateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per SoapFault modalita trasmissione sincrona */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_SOAP_FAULT_SA_TRASMISSIONE_SINCRONA="testSoapFaultApplicativoModalitaTrasmissioneSincrona";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per SoapFault stateless*/
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_SOAP_FAULT_SA_STATELESS="testSoapFaultApplicativoStateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay con Integrazione */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE="testIntegrazione";
    /** ENTITA SPCOOP: Nome Azione del Servizio OneWay con Integrazione con WSAddressing */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING="testIntegrazioneWithWSAddressing";
    /** ENTITA SPCOOP: Nome Azione del Servizio OneWay con Integrazione per connettore SAAJ */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_SAAJ="testIntegrazioneSAAJ";
    /** ENTITA SPCOOP: Nome Azione del Servizio OneWay con Integrazione con WSAddressing per connettore SAAJ  */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ="testIntegrazioneWithWSAddressingSAAJ";
    /** ENTITA SPCOOP: Nome Azione del Servizio OneWay con Integrazione per connettore HTTPCORE */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_HTTPCORE="testIntegrazioneHTTPCORE";
    /** ENTITA SPCOOP: Nome Azione del Servizio OneWay con Integrazione con WSAddressing per connettore HTTPCORE  */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE="testIntegrazioneWithWSAddressingHTTPCORE";
    /** ENTITA SPCOOP: Nome Azione del Servizio OneWay con Integrazione per funzione concat */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_CONCAT="BEGIN-ID_testIntegrazione_END-ID";
    /** ENTITA SPCOOP: Nome Azione del Servizio OneWay con ConsegnaInOrdine */
    public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONSEGNA_IN_ORDINE="ConsegnaInOrdine";
    /** ENTITA SPCOOP: Nome Azione del Servizio OneWay per test Filtro Duplicati stateless con riscontro */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATELESS_CON_RISCONTRO="FiltroDuplicatiConRiscontroStateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per test Filtro Duplicati stateful con riscontro */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATEFUL_CON_RISCONTRO="FiltroDuplicatiConRiscontroStateful";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per test Filtro Duplicati stateless */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATELESS="FiltroDuplicatiStateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per test Filtro Duplicati stateful */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATEFUL="FiltroDuplicatiStateful";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per verifica connettore JMS: text su queue */
	public static final String SPCOOP_SERVIZIO_ONEWAY_JMS_TEXT_QUEUE = "sendTextOnQueueJMS";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per verifica connettore JMS: bytes su queue */
	public static final String SPCOOP_SERVIZIO_ONEWAY_JMS_BYTES_QUEUE = "sendBytesOnQueueJMS";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per verifica connettore JMS: text su topic */
	public static final String SPCOOP_SERVIZIO_ONEWAY_JMS_TEXT_TOPIC = "sendTextOnTopicJMS";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per verifica connettore JMS: bytes su topic */
	public static final String SPCOOP_SERVIZIO_ONEWAY_JMS_BYTES_TOPIC = "sendBytesOnTopicJMS";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per verifica connettore JMS: propagazione info egov su queue */
	public static final String SPCOOP_SERVIZIO_ONEWAY_JMS_INFO_EGOV_QUEUE = "PropagazioneInfoEGovOnQueue";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per verifica connettore JMS: propagazione info egov su topic */
	public static final String SPCOOP_SERVIZIO_ONEWAY_JMS_INFO_EGOV_TOPIC = "PropagazioneInfoEGovOnTopic";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per verifica connettore JMS: sbustamento soap su queue */
	public static final String SPCOOP_SERVIZIO_ONEWAY_JMS_SBUSTAMENTO_SOAP_QUEUE = "sendTextOnQueueJMS_Sbustato";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per verifica connettore JMS: sbustamento soap su topic */
	public static final String SPCOOP_SERVIZIO_ONEWAY_JMS_SBUSTAMENTO_SOAP_TOPIC = "sendTextOnTopicJMS_Sbustato";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per verifica connettore SAAJ */
	public static final String SPCOOP_SERVIZIO_ONEWAY_SAAJ = "saaj";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay per verifica connettore HTTPCORE */
	public static final String SPCOOP_SERVIZIO_ONEWAY_HTTPCORE = "httpCore";
	/** ENTITA SPCOOP: Nome Azione del Servizio OneWay risposta vuota */
	public static final String SPCOOP_SERVIZIO_ONEWAY_AZIONE_RISPOSTA_VUOTA="OnewayCompletamenteNullRisposta";
	
	
	/** ENTITA SPCOOP: Tipo Servizio Sincrono */
	public static final String SPCOOP_TIPO_SERVIZIO_SINCRONO="SPC";
	/** ENTITA SPCOOP: Nome Servizio Sincrono */
	public static final String SPCOOP_NOME_SERVIZIO_SINCRONO="RichiestaStatoAvanzamento";
    /** ENTITA SPCOOP: Nome Azione Collaborazione del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_COLLABORAZIONE="Collaborazione";
    /** ENTITA SPCOOP: Nome Azione WSSTimestamp del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_TIMESTAMP="WSSTimestamp";
    /** ENTITA SPCOOP: Nome Azione WSSEncrypt del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_ENCRYPT="WSSEncrypt";
    /** ENTITA SPCOOP: Nome Azione WSSEncryptP12 del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_ENCRYPT_P12="WSSEncryptP12";
    /** ENTITA SPCOOP: Nome Azione WSSSignature del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SIGNATURE="WSSSignature";
    /** ENTITA SPCOOP: Nome Azione WSSSignatureP12 del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SIGNATURE_P12="WSSSignatureP12";
    /** ENTITA SPCOOP: Nome Azione WSSEncrypt del Servizio RichiestaStatoAvanzamento, con configurazione errata. Deve essere tornato un codice EGOV_IT_200 */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_ENCRYPT_MESSAGGIO_ALTERATO="WSSEncryptError";
    /** ENTITA SPCOOP: Nome Azione WSSSignature del Servizio RichiestaStatoAvanzamento, con configurazione errata. Deve essere tornato un codice EGOV_IT_202 */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SIGNATURE_MESSAGGIO_ALTERATO="WSSSignatureError";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS="WSS";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSEncrypt_ClientMustUnderstand1_PdDActorMustUnderstand1 */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActorMU_Encrypt="WSSEncrypt_ClientMustUnderstand1_PdDActorMustUnderstand1";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSEncrypt_ClientMustUnderstand1_PdDActorOpenSPCoop */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActor_Encrypt="WSSEncrypt_ClientMustUnderstand1_PdDActorOpenSPCoop";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSEncrypt_ClientActorClient_PdDMustUnderstand1 */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDMU_Encrypt="WSSEncrypt_ClientActorClient_PdDMustUnderstand1";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSEncrypt_ClientActorClient_PdDActorOpenSPCoop */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDActor_Encrypt="WSSEncrypt_ClientActorClient_PdDActorOpenSPCoop";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSEncrypt_ClientMustUnderstand1eActorClient_PdDMustUnderstand1 */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDMU_Encrypt="WSSEncrypt_ClientMustUnderstand1eActorClient_PdDMustUnderstand1";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSEncrypt_ClientMustUnderstand1eActorClient_PdDActorOpenSPCoop */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDActor_Encrypt="WSSEncrypt_ClientMustUnderstand1eActorClient_PdDActorOpenSPCoop";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSSignature_ClientMustUnderstand1_PdDActorMustUnderstand1 */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActorMU_Signature="WSSSignature_ClientMustUnderstand1_PdDActorMustUnderstand1";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSSignature_ClientMustUnderstand1_PdDActorOpenSPCoop */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActor_Signature="WSSSignature_ClientMustUnderstand1_PdDActorOpenSPCoop";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSSignature_ClientActorClient_PdDMustUnderstand1 */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDMU_Signature="WSSSignature_ClientActorClient_PdDMustUnderstand1";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSSignature_ClientActorClient_PdDActorOpenSPCoop */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDActor_Signature="WSSSignature_ClientActorClient_PdDActorOpenSPCoop";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSSignature_ClientMustUnderstand1eActorClient_PdDMustUnderstand1 */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDMU_Signature="WSSSignature_ClientMustUnderstand1eActorClient_PdDMustUnderstand1";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento BUG18 WSSSignature_ClientMustUnderstand1eActorClient_PdDActorOpenSPCoop */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDActor_Signature="WSSSignature_ClientMustUnderstand1eActorClient_PdDActorOpenSPCoop";
    /** ENTITA SPCOOP: Nome Azione WSS del Servizio RichiestaStatoAvanzamento WSS Annidamento*/
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_ANNIDAMENTO="WSSAnnidamento";
    /** ENTITA SPCOOP: Nome Azione WSSSoapBoxCifrataFirmata del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_ENCRYPT_SIGNATURE="WSSSoapBoxCifrataFirmata";
    /** ENTITA SPCOOP: Nome Azione WSSSoapBoxFirmataCifrata del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE_ENCRYPT="WSSSoapBoxFirmataCifrata";
    /** ENTITA SPCOOP: Nome Azione WSSSoapBoxCifrata del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_ENCRYPT="WSSSoapBoxCifrata";
    /** ENTITA SPCOOP: Nome Azione WSSSoapBoxFirmata del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE="WSSSoapBoxFirmata";    
    /** ENTITA SPCOOP: Nome Azione WSSSoapBoxCifrataFirmataAttachments del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_ENCRYPT_SIGNATURE_ATTACHMENTS="WSSSoapBoxCifrataFirmataAttachments";
    /** ENTITA SPCOOP: Nome Azione WSSSoapBoxFirmataCifrataAttachments del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE_ENCRYPT_ATTACHMENTS="WSSSoapBoxFirmataCifrataAttachments";
    /** ENTITA SPCOOP: Nome Azione WSSSoapBoxCifrataAttachments del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_ENCRYPT_ATTACHMENTS="WSSSoapBoxCifrataAttachments";
    /** ENTITA SPCOOP: Nome Azione WSSSoapBoxFirmataAttachments del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS="WSSSoapBoxFirmataAttachments";    
    /** ENTITA SPCOOP: Nome Azione WSSSoapBoxFirmataAttachmentsSignatureEngineSun del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS_SIGNATURE_ENGINE_SUN="WSSSoapBoxFirmataAttachmentsSignatureEngineSun";
    /** ENTITA SPCOOP: Nome Azione WSSSoapBoxFirmataAttachmentsSignatureEngineXmlSec del Servizio RichiestaStatoAvanzamento */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS_SIGNATURE_ENGINE_XMLSEC="WSSSoapBoxFirmataAttachmentsSignatureEngineXmlSec";
    /** ENTITA SPCOOP: Nome Azione del Servizio sincrono per SoapFault */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_FAULT_SA="testSoapFaultApplicativo";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per connettore errato SA */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_CONNETTORE_ERRATO_SA="testConnettoreErratoApplicativo";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono stateful */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_STATEFUL="stateful";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per connettore errato SA stateless*/
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_CONNETTORE_ERRATO_SA_STATELESS="testConnettoreErratoApplicativoStateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per SoapFault stateless*/
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_FAULT_SA_STATELESS="testSoapFaultApplicativoStateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Integrazione */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE="testIntegrazione";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Integrazione con WSAddressing */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING="testIntegrazioneWithWSAddressing";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Integrazione per connettore SAAJ */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_SAAJ="testIntegrazioneSAAJ";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Integrazione con WSAddressing per connettore SAAJ */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ="testIntegrazioneWithWSAddressingSAAJ";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Integrazione per connettore HTTPCORE */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_HTTPCORE="testIntegrazioneHTTPCORE";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Integrazione con WSAddressing per connettore HTTPCORE */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE="testIntegrazioneWithWSAddressingHTTPCORE";
    /** ENTITA SPCOOP: Nome Azione del Servizio OneWay con Integrazione PA */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_LATO_PA="testIntegrazionePA";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con GestioneManifestDisabilitata */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_GESTIONE_MANIFEST_DISABILITATA="gestioneManifestDisabilitata";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con https_with_client_auth */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH="https_with_client_auth";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con https_with_client_auth_identita2 */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH_IDENTITA2="https_with_client_auth_identita2";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con https_with_client_auth_error */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH_ERROR="https_with_client_auth_error";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con https_without_client_auth */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITHOUT_CLIENT_AUTH="https_without_client_auth";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con https_ca_non_presente */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_CA_NON_PRESENTE="https_ca_non_presente";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con https_hostname_verify */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_HOSTNAME_VERIFY="https_hostname_verify";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con https_sil_consegna */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_SIL_CONSEGNA="https_sil_consegna";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per test Filtro Duplicati stateless */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_FILTRO_DUPLICATI_STATELESS="FiltroDuplicatiStateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per test Filtro Duplicati stateful */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_FILTRO_DUPLICATI_STATEFUL="FiltroDuplicatiStateful";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per test autorizzazione contenuto OK */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_AUTORIZZAZIONE_CONTENUTO_OK="autorizzazioneContenutoOK";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per test autorizzazione contenuto KO */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_AUTORIZZAZIONE_CONTENUTO_KO="autorizzazioneContenutoKO";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per test XML Encoding */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_XML_ENCODING="xmlEncoding";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per test XML Encoding */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_XML_ENCODING_STATEFUL="xmlEncodingStateful";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per test soap header non presente o vuoto */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_HEADER_EMTPY="soapHeaderEmpty";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per test soap header non presente o vuoto */
	public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_HEADER_EMTPY_STATEFUL="soapHeaderEmptyStateful";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per verifica connettore SAAJ */
	public static final String SPCOOP_SERVIZIO_SINCRONO_SAAJ = "saaj";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per verifica connettore HTTPCORE */
	public static final String SPCOOP_SERVIZIO_SINCRONO_HTTPCORE = "httpCore";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per verifica messaggio malformati nell'header egov */
	public static final String SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_HEADER_EGOV = "malformazioneXMLHeaderEGov";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono per verifica messaggio malformati nel body */
	public static final String SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_BODY = "malformazioneXMLBody_Body";
	public static final String SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_BODYFIRSTCHILD = "malformazioneXMLBody_BodyFirstChild";
	public static final String SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_INSIDEBODY = "malformazioneXMLBody_InsideBody";
	public static final String SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_BODY_STATEFUL = "malformazioneXMLBody_Body_Stateful";
	public static final String SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_BODYFIRSTCHILD_STATEFUL = "malformazioneXMLBody_BodyFirstChild_Stateful";
	public static final String SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_INSIDEBODY_STATEFUL = "malformazioneXMLBody_InsideBody_Stateful";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Correlazione Applicativa risposta: content based */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS="testCorrelazioneApplicativaRisposta_ContentBased_SOAP_stateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Correlazione Applicativa risposta: content based */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATEFUL="testCorrelazioneApplicativaRisposta_ContentBased_SOAP_stateful";
	/** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Correlazione Applicativa risposta: input based trasporto */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_TRASPORTO="testCorrelazioneApplicativaRisposta_InputBased_TRASPORTO";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Correlazione Applicativa risposta: input based soap */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_SOAP="testCorrelazioneApplicativaRisposta_InputBased_SOAP";
    /** ENTITA SPCOOP: Nome Azione del Servizio Sincrono con Correlazione Applicativa risposta: input based wsa */
    public static final String SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_WSA="testCorrelazioneApplicativaRisposta_InputBased_WSA";
   
	/** ENTITA SPCOOP: Tipo Servizio Asincrono Simmetrico */
	public static final String SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO="SPC";
	/** ENTITA SPCOOP: Nome Servizio Asincrono Simmetrico */
	public static final String SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO="RichiestaStatoAvanzamentoAsincronoSimmetrico";
	/** ENTITA SPCOOP: Tipo Servizio Correlato Asincrono Simmetrico */
	public static final String SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO="SPC";
	/** ENTITA SPCOOP: Nome Servizio Correlato Asincrono Simmetrico */
	public static final String SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO="RichiestaStatoAvanzamentoAsincronoSimmetricoCorrelato";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona del Servizio Asincrono Simmetrico modalita Asincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA="richiestaAsincrona";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona del Servizio Asincrono Simmetrico modalita Sincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA="richiestaSincrona";
	/** ENTITA SPCOOP: Nome Azione WSS del Servizio Asincrono Simmetrico modalita Sincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_WSS_MODALITA_SINCRONA="WSS";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Simmetrico per SoapFault */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA="testSoapFaultApplicativo_richiestaAsincrona";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Simmetrico per SoapFault */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA="testSoapFaultApplicativo_richiestaSincrona";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Simmetrico per connettore errato SA */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA="testConnettoreErratoApplicativo_richiestaAsincrona";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Simmetrico per connettore errato SA */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA="testConnettoreErratoApplicativo_richiestaSincrona";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Simmetrico per SoapFault */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS="testSoapFaultApplicativo_richiestaAsincrona_Stateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Simmetrico per SoapFault */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS="testSoapFaultApplicativo_richiestaSincrona_Stateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Simmetrico per connettore errato SA */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS="testConnettoreErratoApplicativo_richiestaAsincrona_Stateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Simmetrico per connettore errato SA */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS="testConnettoreErratoApplicativo_richiestaSincrona_Stateless";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona del Servizio Asincrono Simmetrico modalita Asincrona (Stateful) */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_STATEFUL="richiestaAsincronaStateful";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona del Servizio Asincrono Simmetrico modalita Sincrona (Stateful) */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_STATEFUL="richiestaSincronaStateful";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona del Servizio Asincrono Simmetrico per test Filtro Duplicati stateless */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS="FiltroDuplicatiStateless_ricevutaAbilitata";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona del Servizio Asincrono Simmetrico per test Filtro Duplicati stateful */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL="FiltroDuplicatiStateful_ricevutaAbilitata";
	/** ENTITA SPCOOP: Nome Azione Richiesta Sincrona del Servizio Asincrono Simmetrico per test Filtro Duplicati stateless */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS="FiltroDuplicatiStateless_ricevutaDisabilitata";
	/** ENTITA SPCOOP: Nome Azione Richiesta Sincrona del Servizio Asincrono Simmetrico per test Filtro Duplicati stateful */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL="FiltroDuplicatiStateful_ricevutaDisabilitata";
	
	
	/** ENTITA SPCOOP: Tipo Servizio Asincrono Asimmetrico */
	public static final String SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO="SPC";
	/** ENTITA SPCOOP: Nome Servizio Asincrono Asimmetrico */
	public static final String SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO="RichiestaStatoAvanzamentoAsincronoAsimmetrico";
	/** ENTITA SPCOOP: Tipo Servizio Correlato Asincrono Asimmetrico */
	public static final String SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO="SPC";
	/** ENTITA SPCOOP: Nome Servizio Correlato Asincrono Asimmetrico */
	public static final String SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO="RichiestaStatoAvanzamentoAsincronoAsimmetricoCorrelato";
	/** ENTITA SPCOOP: Nome Azione richiesta Asincrona del Servizio Asincrono Asimmetrico modalita Asincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA="richiestaAsincrona";
	/** ENTITA SPCOOP: Nome Azione richiesta Sincrona del Servizio Asincrono Asimmetrico modalita Sincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA="richiestaSincrona";
	/** ENTITA SPCOOP: Nome Azione WSS del Servizio Asincrono Asimmetrico modalita Sincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_WSS_MODALITA_SINCRONA="WSS";
	/** ENTITA SPCOOP: Nome Azione richiesta Asincrona del Servizio Asincrono Asimmetrico modalita Asincrona (Azione Correlata) */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_AZIONE_CORRELATA_MODALITA_ASINCRONA="AzioneCorrelata_richiestaAsincrona";
	/** ENTITA SPCOOP: Nome Azione richiesta Sincrona del Servizio Asincrono Asimmetrico modalita Sincrona (Azione Correlata) */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_AZIONE_CORRELATA_MODALITA_SINCRONA="AzioneCorrelata_richiestaSincrona";
	/** ENTITA SPCOOP: Nome Azione richiesta Asincrona del Servizio Asincrono Asimmetrico modalita Asincrona (Azione Correlata) */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_AZIONE_CORRELATA_MODALITA_ASINCRONA="AzioneCorrelata_richiestaAsincronaCorrelata";
	/** ENTITA SPCOOP: Nome Azione richiesta Sincrona del Servizio Asincrono Asimmetrico modalita Sincrona (Azione Correlata) */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_AZIONE_CORRELATA_MODALITA_SINCRONA="AzioneCorrelata_richiestaSincronaCorrelata";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Asimmetrico per SoapFault */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA="testSoapFaultApplicativo_richiestaAsincrona";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Asimmetrico per SoapFault */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA="testSoapFaultApplicativo_richiestaSincrona";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Asimmetrico per connettore errato SA */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA="testConnettoreErratoApplicativo_richiestaAsincrona";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Asimmetrico per connettore errato SA */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA="testConnettoreErratoApplicativo_richiestaSincrona";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Asimmetrico per SoapFault */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS="testSoapFaultApplicativo_richiestaAsincrona_Stateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Asimmetrico per SoapFault */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS="testSoapFaultApplicativo_richiestaSincrona_Stateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Asimmetrico per connettore errato SA */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS="testConnettoreErratoApplicativo_richiestaAsincrona_Stateless";
	/** ENTITA SPCOOP: Nome Azione del Servizio Asincrono Asimmetrico per connettore errato SA */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS="testConnettoreErratoApplicativo_richiestaSincrona_Stateless";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona del Servizio Asincrono Asimmetrico modalita Asincrona (Stateful) */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_STATEFUL="richiestaAsincronaStateful";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona del Servizio Asincrono Asimmetrico modalita Sincrona (Stateful) */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_STATEFUL="richiestaSincronaStateful";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona del Servizio Asincrono Asimmetrico per test Filtro Duplicati stateless */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS="FiltroDuplicatiStateless_ricevutaAbilitata";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona del Servizio Asincrono Asimmetrico per test Filtro Duplicati stateful */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL="FiltroDuplicatiStateful_ricevutaAbilitata";
	/** ENTITA SPCOOP: Nome Azione Richiesta Sincrona del Servizio Asincrono Asimmetrico per test Filtro Duplicati stateless */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS="FiltroDuplicatiStateless_ricevutaDisabilitata";
	/** ENTITA SPCOOP: Nome Azione Richiesta Sincrona del Servizio Asincrono Asimmetrico per test Filtro Duplicati stateful */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL="FiltroDuplicatiStateful_ricevutaDisabilitata";
	
	
	
	/** ENTITA SPCOOP: PortType Tipo Servizio OneWay */
	public static final String SPCOOP_TIPO_SERVIZIO_ONEWAY_PORT_TYPE="SPC";
	/** ENTITA SPCOOP: PortType Nome Servizio OneWay */
	public static final String SPCOOP_NOME_SERVIZIO_ONEWAY_PORT_TYPE="PortTypeOneWay";
	/** ENTITA SPCOOP: PortType Servizio OneWay, azione di test */
	public static final String SPCOOP_SERVIZIO_ONEWAY_PORT_TYPE_AZIONE_TEST="testOneWay";
	
	
	/** ENTITA SPCOOP: PortType Tipo Servizio Sincrono */
	public static final String SPCOOP_TIPO_SERVIZIO_SINCRONO_PORT_TYPE="SPC";
	/** ENTITA SPCOOP: PortType Nome Servizio Sincrono */
	public static final String SPCOOP_NOME_SERVIZIO_SINCRONO_PORT_TYPE="PortTypeSincrono";
	/** ENTITA SPCOOP: PortType Servizio Sincrono, azione di test */
	public static final String SPCOOP_SERVIZIO_SINCRONO_PORT_TYPE_AZIONE_TEST="testSincrono";
	
	/** ENTITA SPCOOP: PortType Tipo Servizio Asincrono Simmetrico */
	public static final String SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO_PORT_TYPE="SPC";
	/** ENTITA SPCOOP: PortType Nome Servizio Asincrono Simmetrico */
	public static final String SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO_PORT_TYPE="PortTypeAsincronoSimmetricoRichiesta";
	/** ENTITA SPCOOP: PortType Tipo Servizio Correlato Asincrono Simmetrico */
	public static final String SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO_PORT_TYPE="SPC";
	/** ENTITA SPCOOP: PortType Nome Servizio Correlato Asincrono Simmetrico */
	public static final String SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO_PORT_TYPE="PortTypeAsincronoSimmetricoRisposta";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona PortType del Servizio Asincrono Simmetrico modalita Asincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_RICHIESTA_MODALITA_ASINCRONA_PORT_TYPE="testAsincronoSimmetrico_richiestaAsincrona";
	/** ENTITA SPCOOP: Nome Azione Richiesta Asincrona PortType del Servizio Asincrono Simmetrico modalita Sincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_RICHIESTA_MODALITA_SINCRONA_PORT_TYPE="testAsincronoSimmetrico_richiestaSincrona";
	/** ENTITA SPCOOP: Nome Azione Risposta Asincrona PortType del Servizio Asincrono Simmetrico modalita Asincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_RISPOSTA_MODALITA_ASINCRONA_PORT_TYPE="testAsincronoSimmetrico_rispostaAsincrona";
	/** ENTITA SPCOOP: Nome Azione Risposta Asincrona PortType del Servizio Asincrono Simmetrico modalita Sincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_RISPOSTA_MODALITA_SINCRONA_PORT_TYPE="testAsincronoSimmetrico_rispostaSincrona";


	/** ENTITA SPCOOP: PortType Tipo Servizio Asincrono Asimmetrico */
	public static final String SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE="SPC";
	/** ENTITA SPCOOP: PortType Nome Servizio Asincrono Asimmetrico */
	public static final String SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE="PortTypeAsincronoAsimmetricoRichiesta";
	/** ENTITA SPCOOP: PortType Tipo Servizio Correlato Asincrono Asimmetrico */
	public static final String SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO_PORT_TYPE="SPC";
	/** ENTITA SPCOOP: PortType Nome Servizio Correlato Asincrono Asimmetrico */
	public static final String SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO_PORT_TYPE="PortTypeAsincronoAsimmetricoRichiestaStato";
	/** ENTITA SPCOOP: Nome Azione richiesta Asincrona PortType del Servizio Asincrono Asimmetrico modalita Asincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_MODALITA_ASINCRONA_PORT_TYPE="testAsincronoAsimmetrico_richiestaAsincrona";
	/** ENTITA SPCOOP: Nome Azione richiesta Sincrona PortType del Servizio Asincrono Asimmetrico modalita Sincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_MODALITA_SINCRONA_PORT_TYPE="testAsincronoAsimmetrico_richiestaSincrona";
	/** ENTITA SPCOOP: Nome Azione richiesta Asincrona PortType del Servizio Asincrono Asimmetrico modalita Asincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_STATO_MODALITA_ASINCRONA_PORT_TYPE="testAsincronoAsimmetrico_richiestaStatoAsincrona";
	/** ENTITA SPCOOP: Nome Azione richiesta Sincrona PortType del Servizio Asincrono Asimmetrico modalita Sincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_STATO_MODALITA_SINCRONA_PORT_TYPE="testAsincronoAsimmetrico_richiestaStatoSincrona";
	
	/** ENTITA SPCOOP: PortType e operation Correlata Tipo Servizio Asincrono Asimmetrico */
	public static final String SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA="SPC";
	/** ENTITA SPCOOP: PortType e operation Correlata Nome Servizio Asincrono Asimmetrico */
	public static final String SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA="PortTypeAsincronoAsimmetrico";
	/** ENTITA SPCOOP: Nome Azione richiesta Asincrona PortType e operation Correlata del Servizio Asincrono Asimmetrico modalita Asincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_MODALITA_ASINCRONA_PORT_TYPE_OPERATION_CORRELATA="testAsincronoAsimmetrico_richiestaAsincrona";
	/** ENTITA SPCOOP: Nome Azione richiesta Sincrona PortType e operation Correlata del Servizio Asincrono Asimmetrico modalita Sincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_MODALITA_SINCRONA_PORT_TYPE_OPERATION_CORRELATA="testAsincronoAsimmetrico_richiestaSincrona";
	/** ENTITA SPCOOP: Nome Azione richiesta Asincrona PortType e operation Correlata del Servizio Asincrono Asimmetrico modalita Asincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_STATO_MODALITA_ASINCRONA_PORT_TYPE_OPERATION_CORRELATA="testAsincronoAsimmetrico_richiestaStatoAsincrona";
	/** ENTITA SPCOOP: Nome Azione richiesta Sincrona PortType e operation Correlata del Servizio Asincrono Asimmetrico modalita Sincrona */
	public static final String SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_STATO_MODALITA_SINCRONA_PORT_TYPE_OPERATION_CORRELATA="testAsincronoAsimmetrico_richiestaStatoSincrona";

	/** ENTITA SPCOOP: Tipo Servizio Asincrono Asimmetrico per test profilo */
	public static final String SPCOOP_TIPO_SERVIZIO_TEST_PROFILO="SPC";
	/** ENTITA SPCOOP: Nome Servizio Asincrono Asimmetrico */
	public static final String SPCOOP_NOME_SERVIZIO_TEST_PROFILO="TestProfiloLineeGuida";
	/** ENTITA SPCOOP: Servizio Asincrono Asimmetrico, richiesta */
	public static final String SPCOOP_NOME_SERVIZIO_TEST_PROFILO_AZIONE="test";
	/** ENTITA SPCOOP: Servizio Asincrono Asimmetrico, risposta */
	public static final String SPCOOP_NOME_SERVIZIO_TEST_PROFILO_AZIONE_CORRELATA="testCorrelata";
	
	
	/** ENTITA SPCOOP: Tipo Servizio OneWay Message BOX */
	public static final String SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX="SPC";
	/** ENTITA SPCOOP: Nome Servizio OneWay Message BOX */
	public static final String SPCOOP_NOME_SERVIZIO_MESSAGE_BOX="MessageBox";
	/** ENTITA SPCOOP: Nome Azione GetMessage del Servizio OneWay MessageBox con IntegrationManager */
	public static final String SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER="GetMessage";
	/** ENTITA SPCOOP: Nome Azione GetMessage2 del Servizio OneWay MessageBox con IntegrationManager */
	public static final String SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE2_INTEGRATION_MANAGER="GetMessage2";
	
	
	/** ENTITA SPCOOP: Tipo Servizio  */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP="SPC";
	
	/** ENTITA SPCOOP: Nome Servizio  EsitoAggiornamentoAsincronoSimmetricoWrappedDocumentLiteral per Validazione WSDL*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_ESITO_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL="EsitoAggiornamentoAsincronoSimmetricoWrappedDocumentLiteral";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiWrappedDocumentLiteral per Validazione WSDL */
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL="GestioneUtentiWrappedDocumentLiteral";
	/** ENTITA SPCOOP: Nome Servizio AggiornamentoUtentiWrappedDocumentLiteral per Validazione WSDL*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_AGGIORNAMENTO_UTENTI_WDL="AggiornamentoUtentiWrappedDocumentLiteral";
	/** ENTITA SPCOOP: Nome Servizio AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral per Validazione WSDL*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL="AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral";
	/** ENTITA SPCOOP: Nome Servizio AggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral per Validazione WSDL*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL="AggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiDocumentLiteral per Validazione WSDL*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_DL="GestioneUtentiDocumentLiteral";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiRPCLiteral per Validazione WSDL*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_RPCL="GestioneUtentiRPCLiteral";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiRPCEncoded per Validazione WSDL*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_RPCE="GestioneUtentiRPCEncoded";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiOverloadedOperations per Validazione WSDL*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_OVERLOADED_OPERATIONS="GestioneUtentiOverloadedOperations";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiStileIbrido per Validazione WSDL*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_STILE_IBRIDO="GestioneUtentiStileIbrido";
	/** ENTITA SPCOOP: Nome Servizio EsitoAggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral per Validazione WSDL*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_WSDL_ESITO_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL="EsitoAggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral";
	
	/** ENTITA SPCOOP: Nome Servizio  EsitoAggiornamentoAsincronoSimmetricoWrappedDocumentLiteral per Validazione OpenSPCoop*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_ESITO_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL="EsitoAggiornamentoAsincronoSimmetricoWrappedDocumentLiteral_ValidazioneOpenSPCoop";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiWrappedDocumentLiteral per Validazione OpenSPCoop */
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_WDL="GestioneUtentiWrappedDocumentLiteral_ValidazioneOpenSPCoop";
	/** ENTITA SPCOOP: Nome Servizio AggiornamentoUtentiWrappedDocumentLiteral per Validazione OpenSPCoop*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_AGGIORNAMENTO_UTENTI_WDL="AggiornamentoUtentiWrappedDocumentLiteral_ValidazioneOpenSPCoop";
	/** ENTITA SPCOOP: Nome Servizio AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral per Validazione OpenSPCoop*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL="AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral_ValidazioneOpenSPCoop";
	/** ENTITA SPCOOP: Nome Servizio AggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral per Validazione OpenSPCoop*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL="AggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral_ValidazioneOpenSPCoop";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiDocumentLiteral per Validazione OpenSPCoop*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_DL="GestioneUtentiDocumentLiteral_ValidazioneOpenSPCoop";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiRPCLiteral per Validazione OpenSPCoop*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_RPCL="GestioneUtentiRPCLiteral_ValidazioneOpenSPCoop";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiRPCEncoded per Validazione OpenSPCoop*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_RPCE="GestioneUtentiRPCEncoded_ValidazioneOpenSPCoop";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiOverloadedOperations per Validazione OpenSPCoop*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_OVERLOADED_OPERATIONS="GestioneUtentiOverloadedOperations_ValidazioneOpenSPCoop";
	/** ENTITA SPCOOP: Nome Servizio GestioneUtentiStileIbrido per Validazione OpenSPCoop*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_STILE_IBRIDO="GestioneUtentiStileIbrido_ValidazioneOpenSPCoop";
	/** ENTITA SPCOOP: Nome Servizio EsitoAggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral per Validazione OpenSPCoop*/
	public static final String SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_ESITO_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL="EsitoAggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral_ValidazioneOpenSPCoop";

	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione registrazioneUtenteWDL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL="registrazioneUtenteWDL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione eliminazioneUtenteWDL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_WDL="eliminazioneUtenteWDL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione notificaAggiornamentoUtenteWDL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_NOTIFICA_AGGIORNAMENTO_UTENTE_WDL="notificaAggiornamentoUtenteWDL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione aggiornamentoUtenteWDL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_AGGIORNAMENTO_UTENTE_WDL="aggiornamentoUtenteWDL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione richiestaAggiornamentoUtenteAsincronoSimmetricoWDL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_RICHIESTA_AGGIORNAMENTO_UTENTE_ASINCRONO_SIMMETRICO_WDL="richiestaAggiornamentoUtenteAsincronoSimmetricoWDL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_RICHIESTA_AGGIORNAMENTO_UTENTE_ASINCRONO_ASIMMETRICO_WDL="richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione esitoAggiornamentoUtenteAsincronoSimmetricoWDL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ESITO_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL="esitoAggiornamentoUtenteAsincronoSimmetricoWDL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione esitoAggiornamentoUtenteAsincronoAsimmetricoWDL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ESITO_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL="esitoAggiornamentoUtenteAsincronoAsimmetricoWDL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione registrazioneUtenteDL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL="registrazioneUtenteDL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione eliminazioneUtenteDL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_DL="eliminazioneUtenteDL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione registrazioneUtenteRPCL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCL="registrazioneUtenteRPCL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione eliminazioneUtenteRPCL */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_RPCL="eliminazioneUtenteRPCL";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione registrazioneUtenteRPCE */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCE="registrazioneUtenteRPCE";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione eliminazioneUtenteRPCE */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_RPCE="eliminazioneUtenteRPCE";
	/** ENTITA SPCOOP: Tipo Servizio Validazione WSDL/OpenSPCoop azione registrazioneUtenteOverloadedOperations */
	public static final String SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_OVERLOADED="registrazioneUtenteOverloadedOperations";

	
	
	
	/** ENTITA SPCOOP: Tipo Servizio per tunnel SOAP*/
	public static final String SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP="SPC";
	/** ENTITA SPCOOP: Nome Servizio per tunnel SOAP */
	public static final String SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP="RichiestaBando";
    /** ENTITA SPCOOP: Nome Azione del Servizio per tunnel SOAP */
    public static final String SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP="TunnelSOAP";
    /** ENTITA SPCOOP: Nome Azione del Servizio per tunnel SOAP */
    public static final String SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_MULTIPART_RELATED_MIME="TunnelSOAPMultipartRelatedMIME";
    /** ENTITA SPCOOP: Nome Azione del Servizio per tunnel SOAP */
    public static final String SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP="TunnelSOAPWithAttachmentApplicationOpenSPCoop";
    /** ENTITA SPCOOP: Nome Azione del Servizio per tunnel SOAP */
    public static final String SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_ATTACHMENT_CUSTOM_MIME_TYPE="TunnelSOAPWithAttachmentCustomMimeType";
    /** ENTITA SPCOOP: Nome Azione del Servizio per tunnel SOAP */
    public static final String SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_NOTIFICA="NotificaTunnelSOAP";
    /** ENTITA SPCOOP: Nome Azione del Servizio per tunnel SOAP */
    public static final String SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_MULTIPART_RELATED_MIME_NOTIFICA="NotificaTunnelSOAPMultipartRelatedMIME";
    /** ENTITA SPCOOP: Nome Azione del Servizio per tunnel SOAP */
    public static final String SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP_NOTIFICA="NotificaTunnelSOAPWithAttachmentApplicationOpenSPCoop";
    /** ENTITA SPCOOP: Nome Azione del Servizio per tunnel SOAP */
    public static final String SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_ATTACHMENT_CUSTOM_MIME_TYPE_NOTIFICA="NotificaTunnelSOAPWithAttachmentCustomMimeType";
    
    
    
    /** ENTITA SPCOOP: Tipo Servizio per Versionamento Accordi  */
	public static final String SPCOOP_TIPO_SERVIZIO_VERSIONAMENTO_ACCORDI="SPC";
	/** ENTITA SPCOOP: Nome Servizio per Versionamento Accordi SPC/MinisteroFruitore:ASComunicazioneVariazione:1.0 */
	public static final String SPCOOP_NOME_SERVIZIO_VERSIONAMENTO_ACCORDI_SOGGREFERENTE_NOME_VERSIONE="SoggettoReferente_Nome_Versione";
	/** ENTITA SPCOOP: Nome Azione per Versionamento Accordi SPC/MinisteroFruitore:ASComunicazioneVariazione:1.0 */
	public static final String SPCOOP_NOME_AZIONE_VERSIONAMENTO_ACCORDI_SOGGREFERENTE_NOME_VERSIONE="soggettoReferente_nome_versione";
	/** ENTITA SPCOOP: Nome Servizio per Versionamento Accordi SPC/MinisteroFruitore:ASComunicazioneVariazione */
	public static final String SPCOOP_NOME_SERVIZIO_VERSIONAMENTO_ACCORDI_SOGGREFERENTE_NOME="SoggettoReferente_Nome";
	/** ENTITA SPCOOP: Nome Azione per Versionamento Accordi SPC/MinisteroFruitore:ASComunicazioneVariazione */
	public static final String SPCOOP_NOME_AZIONE_VERSIONAMENTO_ACCORDI_SOGGREFERENTE_NOME="soggettoReferente_nome";
	/** ENTITA SPCOOP: Nome Servizio per Versionamento Accordi ASComunicazioneVariazione:1.0 */
	public static final String SPCOOP_NOME_SERVIZIO_VERSIONAMENTO_ACCORDI_NOME_VERSIONE="Nome_Versione";
	/** ENTITA SPCOOP: Nome Azione per Versionamento Accordi ASComunicazioneVariazione:1.0 */
	public static final String SPCOOP_NOME_AZIONE_VERSIONAMENTO_ACCORDI_NOME_VERSIONE="nome_versione";
	
	
	/** Tipo interfaccia GUI: pdd  */
	public static final String INTERFACCIA_GUI_PDD="pdd";
	/** Tipo interfaccia GUI: regserv  */
	public static final String INTERFACCIA_GUI_REGSERV="regserv";
	/** Tipo interfaccia GUI: pddConsole  */
	public static final String INTERFACCIA_GUI_CONTROL_STATION="pddConsole";
	/** Tipo interfaccia GUI: pddConsoleSinglePdD  */
	public static final String INTERFACCIA_GUI_CONTROL_STATION_SINGLE_PDD="pddConsoleSinglePdD";
	
}
