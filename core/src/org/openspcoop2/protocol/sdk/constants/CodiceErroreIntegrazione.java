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



package org.openspcoop2.protocol.sdk.constants;

import java.io.Serializable;

/**
 * CodiceErroreIntegrazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum CodiceErroreIntegrazione implements CodiceErrore, Serializable{
   

    /* ********  F I E L D S    S T A T I C    P U B L I C  ******** */
	UNKNOWN(0),
    /** String che contiene un codice di errore OpenSPCoop: Errore di Processamento Generale, 500*/
    CODICE_500_ERRORE_INTERNO(500),
    /** String che contiene un codice di errore OpenSPCoop: OpenSPCoop non inizializzato, 501*/
    CODICE_501_PDD_NON_INIZIALIZZATA(501),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante l'individuazione della PortaDelegata, 502*/
    CODICE_502_IDENTIFICAZIONE_PD(502),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante il processo di autenticazione, 503*/
    CODICE_503_AUTENTICAZIONE(503), 
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante il processo di autorizzazione, 504*/
    CODICE_504_AUTORIZZAZIONE(504),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante la get di una connessione al DB, 505*/
    CODICE_505_GET_DB_CONNECTION(505),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante il CommitJDBC, 506*/
    CODICE_506_COMMIT_JDBC(506),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante la costruzione di un ID, 507*/
    CODICE_507_COSTRUZIONE_IDENTIFICATIVO(507), 
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante il salvataggio della richiesta applicativa, 508*/
    CODICE_508_SAVE_REQUEST_MSG(508),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante la lettura della richiesta applicativa, 509*/
    CODICE_509_READ_REQUEST_MSG(509),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante il salvataggio della risposta applicativa, 510*/
    CODICE_510_SAVE_RESPONSE_MSG(510),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante la lettura della risposta applicativa, 511*/
    CODICE_511_READ_RESPONSE_MSG(511),
    /** String che contiene un codice di errore OpenSPCoop: Errore durante l'inoltro messaggio via JMS al successivo modulo, 512*/
    CODICE_512_SEND(512),
    /** String che contiene un codice di errore OpenSPCoop: Errore durante la ricezione di un messaggio dal precedente modulo, 513*/
    CODICE_513_RECEIVE(513),
    /** String che contiene un codice di errore OpenSPCoop: Configurazione del routing errata: connettore per forward non trovato, 514*/
    CODICE_514_ROUTING_CONFIGURATION_ERROR(514),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Connettore non registrato, 515*/
    CODICE_515_CONNETTORE_NON_REGISTRATO(515),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Errore durante l'utilizzo del Connettore, 516*/
    CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE(516),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Risposta non presente, 517*/
    CODICE_517_RISPOSTA_RICHIESTA_NON_RITORNATA(517),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Risposta non presente, ma SOAP Fault presente, 518*/
    CODICE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT(518),
    /** String che contiene un codice di errore OpenSPCoop: IntegrationManager configurato senza autenticazione, 519*/
    CODICE_519_INTEGRATION_MANAGER_CONFIGURATION_ERROR(519),
     /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante la lettura di un msg del IntegrationManager, 520*/
    CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER(520),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante il save di un msg del IntegrationManager, 521*/
    CODICE_521_SAVE_MSG_FROM_INTEGRATION_MANAGER(521),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante l'eliminazione di un msg (IntegrationManager) 522*/
    CODICE_522_DELETE_MSG_FROM_INTEGRATION_MANAGER(522),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante la creazione di un Message 523*/
    CODICE_523_CREAZIONE_PROTOCOL_MESSAGE(523),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante la creazione di un Message 524*/
    CODICE_524_CREAZIONE_PROTOCOL_EXCEPTION(524),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante il salvataggio di informazioni 525*/
    CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO(525),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante l'imbustamento 526*/
    CODICE_526_GESTIONE_IMBUSTAMENTO(526),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante lo sbustamento 527*/
    CODICE_527_GESTIONE_SBUSTAMENTO(527),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Risposta non valida, 528*/
    CODICE_528_RISPOSTA_RICHIESTA_NON_VALIDA(528),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Correlazione Applicativa non riuscita, 529*/
    CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA(529),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Costruzione WSDL non riuscita, 530*/
    CODICE_530_COSTRUZIONE_WSDL_FALLITA(530),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Validazione WSDL non riuscita, 531*/
    CODICE_531_VALIDAZIONE_WSDL_FALLITA(531),
    /** String che contiene un codice di errore OpenSPCoop: Risorse non disponibili , 532*/
    CODICE_532_RISORSE_NON_DISPONIBILI(532),
    /** String che contiene un codice di errore OpenSPCoop: Risorse non disponibili , 533*/
    CODICE_533_RISORSE_DISPONIBILI_LIVELLO_CRITICO(533),
    /** String che contiene un codice di errore OpenSPCoop: Risorse non disponibili , 534*/
    CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE(534),
    /** String che contiene un codice di errore OpenSPCoop: BustaSPcoop senza Eccezioni anche se consegna con errore , 535*/
    CODICE_535_BUSTA_SENZA_ECCEZIONI_CON_UTILIZZO_CONNETTORE_CON_ERRORE(535),
    /** String che contiene un codice di errore OpenSPCoop: Risorsa Configurazione non disponibili , 536*/
    CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE(536),
    /** String che contiene un codice di errore OpenSPCoop: BustaSPCoopRicevutaPrecedentemente,537 */
    CODICE_537_BUSTA_GIA_RICEVUTA(537),
    /** String che contiene un codice di errore OpenSPCoop: BustaSPCoopRichiestaAsincronaAncoraInProcessamento,538 */
    CODICE_538_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO(538),
    /** String che contiene un codice di errore OpenSPCoop: BustaSPCoopRicevutaRichiestaAsincronaAncoraInProcessamento,539 */
    CODICE_539_RICEVUTA_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO(539),
    /** String che contiene un codice di errore OpenSPCoop: Risorse non disponibili , 540*/
    CODICE_540_REGISTRO_SERVIZI_MAL_CONFIGURATO(540),
    /** String che contiene un codice di errore OpenSPCoop: Gestione header integrazione , 541*/
    CODICE_541_GESTIONE_HEADER_INTEGRAZIONE(541), 
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante il processo di autorizzazione per contenuto, 542*/
    CODICE_542_AUTORIZZAZIONE_CONTENUTO(542),   
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante l'invocazione dell'handler di inoltro richiesta*/
    CODICE_543_HANDLER_OUT_REQUEST(543),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante l'invocazione dell'handler di ricezione risposta*/
    CODICE_544_HANDLER_IN_RESPONSE(544),   
    /** String che contiene un codice di errore OpenSPCoop: Risorse non disponibili , 545*/
    CODICE_545_TRACCIATURA_NON_FUNZIONANTE(545),
    /** String che contiene un codice di errore OpenSPCoop: Risorse non disponibili , 546*/
    CODICE_546_DIAGNOSTICA_NON_FUNZIONANTE(546),
    /** String che contiene un codice di errore OpenSPCoop: Risorse non disponibili , 547*/
    CODICE_547_DUMP_CONTENUTI_APPLICATIVI_NON_FUNZIONANTE(547),
    /** String che contiene un codice di errore OpenSPCoop: gestore credenziali , 548*/
    CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE(548),
    /** String che contiene un codice di errore OpenSPCoop: security info reader error , 549*/
    CODICE_549_SECURITY_INFO_READER_ERROR(549),
    /** String che contiene un codice di errore OpenSPCoop2: PDService non attivo, 550*/
    CODICE_550_PD_SERVICE_NOT_ACTIVE(550),
    /** String che contiene un codice di errore OpenSPCoop2: PAService non attivo, 551*/
    CODICE_551_PA_SERVICE_NOT_ACTIVE(551),
    /** String che contiene un codice di errore OpenSPCoop2: IMService non attivo, 552*/
    CODICE_552_IM_SERVICE_NOT_ACTIVE(552),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Correlazione Applicativa non riuscita, 553*/
    CODICE_553_CORRELAZIONE_APPLICATIVA_RISPOSTA_NON_RIUSCITA(553),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Configurazione LocalForward non utilizzabile, 554*/
    CODICE_554_LOCAL_FORWARD_ERROR(554),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Configurazione LocalForward errore durante il processamento del messaggio di richiesta, 555*/
    CODICE_555_LOCAL_FORWARD_PROCESS_REQUEST_ERROR(555),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Configurazione LocalForward errore durante il processamento del messaggio di risposta, 556*/
    CODICE_556_LOCAL_FORWARD_PROCESS_RESPONSE_ERROR(556),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Gestione MTOM errore durante il processamento del messaggio, 557*/
    CODICE_557_MTOM_PROCESSOR_ERROR(557),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento durante l'invocazione dell'handler di inoltro richiesta con info di protocollo, 558*/
    CODICE_558_HANDLER_IN_PROTOCOL_REQUEST(558),
    /** String che contiene un codice di errore OpenSPCoop: ErroreProcessamento situazione anomala di messaggio senza fault ricevuto insieme ad un errore di trasporto, 559*/
    CODICE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO(559),
    

    /* ******** MESSAGGI E CODICI 4XX  ******** */

    /** String che contiene un codice di errore OpenSPCoop: PortaDelegataInesistente,401 */
    CODICE_401_PD_INESISTENTE(401),
    /** String che contiene un codice di errore OpenSPCoop: AutenticazioneFallita, 402*/
    CODICE_402_AUTENTICAZIONE_FALLITA(402),
    /** String che contiene un codice di errore OpenSPCoop: PatternRicercaPortaDelegata,403 */
    CODICE_403_PD_PATTERN_NON_VALIDO(403),
    /** String che contiene un codice di errore OpenSPCoop: AutorizzazioneFallita, 404*/
    CODICE_404_AUTORIZZAZIONE_FALLITA(404),
    /** String che contiene un codice di errore OpenSPCoop: ServizioSPCoopNonTrovato, 405*/
    CODICE_405_SERVIZIO_NON_TROVATO(405),
    /** String che contiene un codice di errore OpenSPCoop: Messaggi per il servizio applicativo non trovati, 406*/
    CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI(406),
    /** String che contiene un codice di errore OpenSPCoop: Messaggio richiesto non trovato, 407*/
    CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO(407),
    /** String che contiene un codice di errore OpenSPCoop: ServizioCorrelatoNonTrovato, 408*/
    CODICE_408_SERVIZIO_CORRELATO_NON_TROVATO(408),
    /** String che contiene un codice di errore OpenSPCoop: ServizioCorrelatoNonTrovato, 409*/
    CODICE_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA(409),
     /** String che contiene un codice di errore OpenSPCoop: AutenticazioneFallita, 410*/
    CODICE_410_AUTENTICAZIONE_RICHIESTA(410),
     /** String che contiene un codice di errore OpenSPCoop: RicezioneContenutiAsincroniRichiesta, 411*/
    CODICE_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA(411),
     /** String che contiene un codice di errore OpenSPCoop: PortaDelegata invocabile solo per riferimento, 412*/
    CODICE_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO(412),
     /** String che contiene un codice di errore OpenSPCoop: PortaDelegata invocabile solo senza riferimento, 413*/
    CODICE_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO(413),
     /** String che contiene un codice di errore OpenSPCoop: Funzionalità di consegna in ordine utilizzabile solo con profilo oneway, 414*/
    CODICE_414_CONSEGNA_IN_ORDINE_CON_PROFILO_NO_ONEWAY(414),
     /** String che contiene un codice di errore OpenSPCoop: Funzionalità di consegna in ordine non utilizzabile senza id collaborazione, 415*/
    CODICE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI(415),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Correlazione Applicativa errore, 416*/
    CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE(416),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Costruzione XSD non riuscita, 417*/
    CODICE_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA(417),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Validazione XSD non riuscita, 418*/
    CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA(418),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Validazione XSD non riuscita, 419*/
    CODICE_419_VALIDAZIONE_WSDL_RISPOSTA_FALLITA(419),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Busta presente in una richiesta applicativa, 420*/
    CODICE_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA(420),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Busta soap non presente in una richiesta applicativa, 421*/
    CODICE_421_MSG_SOAP_NON_PRESENTE_RICHIESTA_APPLICATIVA(421),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Imbustamento non riuscito in una richiesta applicativa, 422*/
    CODICE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA(422),
    /** String che contiene un codice di errore OpenSPCoop: ServizioSPCoopNonTrovato, 423*/
    CODICE_423_SERVIZIO_CON_AZIONE_SCORRETTA(423),
    /** String che contiene un codice di errore OpenSPCoop: AllegaBody non riuscito, 424*/
    CODICE_424_ALLEGA_BODY(424),
    /** String che contiene un codice di errore OpenSPCoop: ScartaBody non riuscito, 425*/
    CODICE_425_SCARTA_BODY(425),
    /** String che contiene un codice di errore OpenSPCoop: Errore della Servlet, 426*/
    CODICE_426_SERVLET_ERROR(426),
    /** String che contiene un codice di errore OpenSPCoop: MustUnderstand Error, 427*/
    CODICE_427_MUSTUNDERSTAND_ERROR(427),
    /** String che contiene un codice di errore OpenSPCoop: AutorizzazioneFallita, 428*/
    CODICE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA(428),
    /** String che contiene un codice di errore OpenSPCoop: 429*/
    CODICE_429_CONTENT_TYPE_NON_SUPPORTATO(429),
    /** String che contiene un codice di errore OpenSPCoop: SOPAEnvelope namespace errato, 430*/
    CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR(430),
    /** String che contiene un codice di errore OpenSPCoop: Errore generato dal gestore delle credenziali, 431*/
    CODICE_431_GESTORE_CREDENZIALI_ERROR(431),
    /** String che contiene un codice di errore OpenSPCoop: Errore avvenuto durante il parsing della richiesta, 432*/
    CODICE_432_PARSING_EXCEPTION_RICHIESTA(432),
    /** String che contiene un codice di errore OpenSPCoop: 433*/
    CODICE_433_CONTENT_TYPE_NON_PRESENTE(433),
    /** String che contiene un codice di errore OpenSPCoop: Errore, Correlazione Applicativa errore, 434*/
    CODICE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE(434),
    /** String che contiene un codice di errore OpenSPCoop: Errore, LocalForward, 435*/
    CODICE_435_LOCAL_FORWARD_CONFIG_ERROR(435),
    /** String che contiene un codice di errore OpenSPCoop: Errore, TipoSoggettoFruitoreNotSupported, 436*/
    CODICE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL(436),
    /** String che contiene un codice di errore OpenSPCoop: Errore, TipoSoggettoErogatoreNotSupported, 437*/
    CODICE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL(437),
    /** String che contiene un codice di errore OpenSPCoop: Errore, TipoServizioNotSupported, 438*/
    CODICE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL(438),
    /** String che contiene un codice di errore OpenSPCoop: Errore, FunzionalitaNotSupported, 439*/
    CODICE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL(439),
    /** String che contiene un codice di errore OpenSPCoop: Errore avvenuto durante il parsing della risposta, 432*/
    CODICE_440_PARSING_EXCEPTION_RISPOSTA(440),
    
    
    /* ---- errori spediti in buste errore ---- */
    
    /** String che contiene un codice di errore OpenSPCoop: PortaApplicativaInesistente,450 */
    CODICE_450_PA_INESISTENTE(450),
    /** String che contiene un codice di errore OpenSPCoop: SoggettoInesistente,451 */
    CODICE_451_SOGGETTO_INESISTENTE(451),
    /** String che contiene un codice di errore OpenSPCoop: BustaSPCoopRicevutaPrecedentemente,452 */
    CODICE_452_BUSTA_GIA_RICEVUTA(452),
    /** String che contiene un codice di errore OpenSPCoop: PortaApplicativaInesistente,453 */
    CODICE_453_SA_INESISTENTE(453),
	/** String che contiene un codice di errore OpenSPCoop: Messaggio di risposta con busta nell'header,454 */
	CODICE_454_BUSTA_PRESENTE_RISPOSTA_APPLICATIVA(454);
    
    private final int codice;
    
    private CodiceErroreIntegrazione(int codice) {
		this.codice = codice;
	}
   
    @Override
	public int getCodice() {
		return this.codice;
	}
    
    @Override
    public String toString() {
    	throw new RuntimeException("Not Implemented");
    }
    
	public boolean equals(CodiceErroreIntegrazione codice){
		if(codice==null){
			return false;
		}
		return this.getCodice() == codice.getCodice();
	}
    
//	public static CodiceErroreIntegrazione toCodiceErroreIntegrazione(String codiceString) {
//		 int codice = Integer.parseInt(codiceString.substring(codiceString.length() - 3));
//		 return toCodiceErroreIntegrazione(codice);
//	} USARE IL TRADUTTORE!!!
    public static CodiceErroreIntegrazione toCodiceErroreIntegrazione(int codice) {
    	// recupero il codice
    	try{
	    	switch (codice) {
				case 401: return CodiceErroreIntegrazione.CODICE_401_PD_INESISTENTE;
				case 402: return CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA;
				case 403: return CodiceErroreIntegrazione.CODICE_403_PD_PATTERN_NON_VALIDO;
				case 404: return CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA;
				case 405: return CodiceErroreIntegrazione.CODICE_405_SERVIZIO_NON_TROVATO;
				case 406: return CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI;
				case 407: return CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO;
				case 408: return CodiceErroreIntegrazione.CODICE_408_SERVIZIO_CORRELATO_NON_TROVATO;
				case 409: return CodiceErroreIntegrazione.CODICE_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA;
				case 410: return CodiceErroreIntegrazione.CODICE_410_AUTENTICAZIONE_RICHIESTA;
				case 411: return CodiceErroreIntegrazione.CODICE_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA;
				case 412: return CodiceErroreIntegrazione.CODICE_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO;
				case 413: return CodiceErroreIntegrazione.CODICE_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO;
				case 414: return CodiceErroreIntegrazione.CODICE_414_CONSEGNA_IN_ORDINE_CON_PROFILO_NO_ONEWAY;
				case 415: return CodiceErroreIntegrazione.CODICE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI;
				case 416: return CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE;
				case 417: return CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA;
				case 418: return CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA;
				case 419: return CodiceErroreIntegrazione.CODICE_419_VALIDAZIONE_WSDL_RISPOSTA_FALLITA;
				case 420: return CodiceErroreIntegrazione.CODICE_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA;
				case 421: return CodiceErroreIntegrazione.CODICE_421_MSG_SOAP_NON_PRESENTE_RICHIESTA_APPLICATIVA;
				case 422: return CodiceErroreIntegrazione.CODICE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA;
				case 423: return CodiceErroreIntegrazione.CODICE_423_SERVIZIO_CON_AZIONE_SCORRETTA;
				case 424: return CodiceErroreIntegrazione.CODICE_424_ALLEGA_BODY;
				case 425: return CodiceErroreIntegrazione.CODICE_425_SCARTA_BODY;
				case 426: return CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR;
				case 427: return CodiceErroreIntegrazione.CODICE_427_MUSTUNDERSTAND_ERROR;
				case 428: return CodiceErroreIntegrazione.CODICE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA;
				case 429: return CodiceErroreIntegrazione.CODICE_429_CONTENT_TYPE_NON_SUPPORTATO;
				case 430: return CodiceErroreIntegrazione.CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR;
				case 431: return CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR;
				case 432: return CodiceErroreIntegrazione.CODICE_432_PARSING_EXCEPTION_RICHIESTA;
				case 433: return CodiceErroreIntegrazione.CODICE_433_CONTENT_TYPE_NON_PRESENTE;
				case 434: return CodiceErroreIntegrazione.CODICE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE;
				case 435: return CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR;
				case 436: return CodiceErroreIntegrazione.CODICE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL;
				case 437: return CodiceErroreIntegrazione.CODICE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL;
				case 438: return CodiceErroreIntegrazione.CODICE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL;
				case 439: return CodiceErroreIntegrazione.CODICE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL;
				case 440: return CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA;
				case 450: return CodiceErroreIntegrazione.CODICE_450_PA_INESISTENTE;
				case 451: return CodiceErroreIntegrazione.CODICE_451_SOGGETTO_INESISTENTE;
				case 452: return CodiceErroreIntegrazione.CODICE_452_BUSTA_GIA_RICEVUTA;
				case 453: return CodiceErroreIntegrazione.CODICE_453_SA_INESISTENTE;
				case 500: return CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO;
				case 501: return CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA;
				case 502: return CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PD;
				case 503: return CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE;
				case 504: return CodiceErroreIntegrazione.CODICE_504_AUTORIZZAZIONE;
				case 505: return CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION;
				case 506: return CodiceErroreIntegrazione.CODICE_506_COMMIT_JDBC;
				case 507: return CodiceErroreIntegrazione.CODICE_507_COSTRUZIONE_IDENTIFICATIVO;
				case 508: return CodiceErroreIntegrazione.CODICE_508_SAVE_REQUEST_MSG;
				case 509: return CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG;
				case 510: return CodiceErroreIntegrazione.CODICE_510_SAVE_RESPONSE_MSG;
				case 511: return CodiceErroreIntegrazione.CODICE_511_READ_RESPONSE_MSG;
				case 512: return CodiceErroreIntegrazione.CODICE_512_SEND;
				case 513: return CodiceErroreIntegrazione.CODICE_513_RECEIVE;
				case 514: return CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR;
				case 515: return CodiceErroreIntegrazione.CODICE_515_CONNETTORE_NON_REGISTRATO;
				case 516: return CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE;
				case 517: return CodiceErroreIntegrazione.CODICE_517_RISPOSTA_RICHIESTA_NON_RITORNATA;
				case 518: return CodiceErroreIntegrazione.CODICE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT;
				case 519: return CodiceErroreIntegrazione.CODICE_519_INTEGRATION_MANAGER_CONFIGURATION_ERROR;
				case 520: return CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER;
				case 521: return CodiceErroreIntegrazione.CODICE_521_SAVE_MSG_FROM_INTEGRATION_MANAGER;
				case 522: return CodiceErroreIntegrazione.CODICE_522_DELETE_MSG_FROM_INTEGRATION_MANAGER;
				case 523: return CodiceErroreIntegrazione.CODICE_523_CREAZIONE_PROTOCOL_MESSAGE;
				case 524: return CodiceErroreIntegrazione.CODICE_524_CREAZIONE_PROTOCOL_EXCEPTION;
				case 525: return CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO;
				case 526: return CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO;
				case 527: return CodiceErroreIntegrazione.CODICE_527_GESTIONE_SBUSTAMENTO;
				case 528: return CodiceErroreIntegrazione.CODICE_528_RISPOSTA_RICHIESTA_NON_VALIDA;
				case 529: return CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA;
				case 530: return CodiceErroreIntegrazione.CODICE_530_COSTRUZIONE_WSDL_FALLITA;
				case 531: return CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_WSDL_FALLITA;
				case 532: return CodiceErroreIntegrazione.CODICE_532_RISORSE_NON_DISPONIBILI;
				case 533: return CodiceErroreIntegrazione.CODICE_533_RISORSE_DISPONIBILI_LIVELLO_CRITICO;
				case 534: return CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE;
				case 535: return CodiceErroreIntegrazione.CODICE_535_BUSTA_SENZA_ECCEZIONI_CON_UTILIZZO_CONNETTORE_CON_ERRORE;
				case 536: return CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE;
				case 537: return CodiceErroreIntegrazione.CODICE_537_BUSTA_GIA_RICEVUTA;
				case 538: return CodiceErroreIntegrazione.CODICE_538_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO;
				case 539: return CodiceErroreIntegrazione.CODICE_539_RICEVUTA_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO;
				case 540: return CodiceErroreIntegrazione.CODICE_540_REGISTRO_SERVIZI_MAL_CONFIGURATO;
				case 541: return CodiceErroreIntegrazione.CODICE_541_GESTIONE_HEADER_INTEGRAZIONE;
				case 542: return CodiceErroreIntegrazione.CODICE_542_AUTORIZZAZIONE_CONTENUTO;
				case 543: return CodiceErroreIntegrazione.CODICE_543_HANDLER_OUT_REQUEST;
				case 544: return CodiceErroreIntegrazione.CODICE_544_HANDLER_IN_RESPONSE;
				case 545: return CodiceErroreIntegrazione.CODICE_545_TRACCIATURA_NON_FUNZIONANTE;
				case 546: return CodiceErroreIntegrazione.CODICE_546_DIAGNOSTICA_NON_FUNZIONANTE;
				case 547: return CodiceErroreIntegrazione.CODICE_547_DUMP_CONTENUTI_APPLICATIVI_NON_FUNZIONANTE;
				case 548: return CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE;
				case 549: return CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR;
				case 550: return CodiceErroreIntegrazione.CODICE_550_PD_SERVICE_NOT_ACTIVE;
				case 551: return CodiceErroreIntegrazione.CODICE_551_PA_SERVICE_NOT_ACTIVE;
				case 552: return CodiceErroreIntegrazione.CODICE_552_IM_SERVICE_NOT_ACTIVE;
				case 553: return CodiceErroreIntegrazione.CODICE_553_CORRELAZIONE_APPLICATIVA_RISPOSTA_NON_RIUSCITA;
				case 554: return CodiceErroreIntegrazione.CODICE_554_LOCAL_FORWARD_ERROR;
				case 555: return CodiceErroreIntegrazione.CODICE_555_LOCAL_FORWARD_PROCESS_REQUEST_ERROR;
				case 556: return CodiceErroreIntegrazione.CODICE_556_LOCAL_FORWARD_PROCESS_RESPONSE_ERROR;
				case 557: return CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR;
				case 558: return CodiceErroreIntegrazione.CODICE_558_HANDLER_IN_PROTOCOL_REQUEST;
				case 559: return CodiceErroreIntegrazione.CODICE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO;
			default:
				return CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO;
			}
    	} catch (Exception e) {
    		return UNKNOWN;
    	}
    }
}





