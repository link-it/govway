/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.sdk.constants;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SOAPFaultCode;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.Utilities;



/**
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public enum ErroriIntegrazione {

	/* 5XX */
	
	ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO(CostantiProtocollo.SISTEMA_NON_DISPONIBILE,
			CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),
			
	ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE),
			
	ERRORE_517_RISPOSTA_RICHIESTA_NON_RITORNATA(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_517_RISPOSTA_RICHIESTA_NON_RITORNATA),
	
	ERRORE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT),
		
	ERRORE_537_BUSTA_GIA_RICEVUTA("La richiesta assegnata alla busta con ID="+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_ID_BUSTA+" è già stata ricevuta e risulta ancora in processamento",
			CodiceErroreIntegrazione.CODICE_537_BUSTA_GIA_RICEVUTA),
			
	ERRORE_538_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO("Busta asincrona non gestibile poichè risulta ancora in gestione nella porta la precedente richiesta.",
			CodiceErroreIntegrazione.CODICE_538_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO),
			
	ERRORE_539_RICEVUTA_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO("Busta asincrona non gestibile poichè risulta ancora in gestione nella porta la precedente ricevuta alla richiesta.",
			CodiceErroreIntegrazione.CODICE_539_RICEVUTA_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO),
	
	ERRORE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO),
	
	ERRORE_5XX_CUSTOM(CostantiProtocollo.KEY_ERRORE_CUSTOM,CodiceErroreIntegrazione.CODICE_5XX_CUSTOM),
	
	
	/* 4XX */
	
	ERRORE_401_PORTA_INESISTENTE("La porta invocata non esiste"+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_PARAMETRI+": "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_401_PORTA_INESISTENTE),
	
	ERRORE_402_AUTENTICAZIONE_FALLITA(CostantiProtocollo.PREFISSO_AUTENTICAZIONE_FALLITA+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA),
			
	ERRORE_403_AZIONE_NON_IDENTIFICATA("Identificazione dinamica dell'operazione fallita",
			CodiceErroreIntegrazione.CODICE_403_AZIONE_NON_IDENTIFICATA),
			
	ERRORE_404_AUTORIZZAZIONE_FALLITA_SA("Il servizio applicativo "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_IDENTITA_SERVIZIO_APPLICATIVO+
			" non risulta autorizzato a fruire del servizio richiesto"+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA),
	
	ERRORE_404_AUTORIZZAZIONE_FALLITA_SA_ANONIMO("Il chiamante non risulta autorizzato a fruire del servizio richiesto"+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA),
			
	ERRORE_405_SERVIZIO_NON_TROVATO("Servizio richiesto con la porta delegata non trovato nel Registro dei Servizi",
			CodiceErroreIntegrazione.CODICE_405_SERVIZIO_NON_TROVATO),	
			
	ERRORE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI("Non sono stati rilevati messaggi per il servizio applicativo",
			CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI),	
			
	ERRORE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO("Non è stato rilevato il messaggio richiesto dal servizio applicativo",
			CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO),
		
	ERRORE_408_SERVIZIO_CORRELATO_NON_TROVATO("Servizio correlato (o azione correlata), associato al servizio richiesto con la porta delegata, non trovato nel Registro dei Servizi",
			CodiceErroreIntegrazione.CODICE_408_SERVIZIO_CORRELATO_NON_TROVATO),
			
	ERRORE_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA("Risposta/RichiestaStato non generabile poichè non associata ad una precedente busta di richiesta asincrona",
			CodiceErroreIntegrazione.CODICE_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA),
	
	ERRORE_410_AUTENTICAZIONE_RICHIESTA("Autenticazione necessaria per invocare il servizio richiesto",
			CodiceErroreIntegrazione.CODICE_410_AUTENTICAZIONE_RICHIESTA),
	
	ERRORE_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA("Parametri di consegna della risposta asincrona non presenti nella configurazione del servizio applicativo fruitore",
			CodiceErroreIntegrazione.CODICE_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA),
		
	ERRORE_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO("Il servizio applicativo è autorizzato ad invocare la porta delegata solo attraverso una invocazione per riferimento, effettuabile tramite il servizio IntegrationManager",
			CodiceErroreIntegrazione.CODICE_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO),
			
	ERRORE_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO("Il servizio applicativo non è autorizzato ad invocare la porta delegata tramite una invocazione per riferimento effettuata attraverso il servizio IntegrationManager",
			CodiceErroreIntegrazione.CODICE_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO),
	
	ERRORE_414_CONSEGNA_IN_ORDINE_CON_PROFILO_NO_ONEWAY("Richiesta funzionalità di consegna in ordine con un profilo diverso da OneWay",
			CodiceErroreIntegrazione.CODICE_414_CONSEGNA_IN_ORDINE_CON_PROFILO_NO_ONEWAY),
	
	ERRORE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI("Richiesta funzionalità di consegna in ordine che non rispetta i vincoli richiesti; è possibile che la richiesta non sia accompagnata dalla funzionalità di gestione dei riscontri, un filtro duplicati o un id di collaborazione",
			CodiceErroreIntegrazione.CODICE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI),
			
	ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE("La gestione della funzionalità di correlazione applicativa, per il messaggio di richiesta, ha generato un errore: "+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE),
			
	ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_INTERFACCIA +" del servizio non definito (o definito non correttamente) nel Registro dei Servizi",
			CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA),
			
	ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA("Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio ("+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_INTERFACCIA+") definito nel Registro"+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_VALIDAZIONE_ERROR_MSG,
			CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA),
			
	ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA("Il contenuto applicativo del messaggio di risposta non rispetta l'accordo di servizio ("+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_INTERFACCIA+") definito nel Registro"+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_VALIDAZIONE_ERROR_MSG,
			CodiceErroreIntegrazione.CODICE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA),
			
	ERRORE_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA("Il messaggio inviato al servizio di ricezione contenuti applicativi presenta nell'header una busta",
			CodiceErroreIntegrazione.CODICE_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA),
			
	ERRORE_421_MSG_SOAP_NON_COSTRUIBILE_TRAMITE_RICHIESTA_APPLICATIVA("I bytes inviati al servizio di ricezione contenuti applicativi non rappresentano un messaggio SOAP: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_421_MSG_SOAP_NON_PRESENTE_RICHIESTA_APPLICATIVA),
			
	ERRORE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA("I bytes inviati al servizio di ricezione contenuti applicativi non sono utilizzabili per formare un messaggio SOAP tramite la funzionalità di imbustamento SOAP: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA),
			
	ERRORE_423_SERVIZIO_CON_AZIONE_SCORRETTA("Azione richiesta non corretta: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_423_SERVIZIO_CON_AZIONE_SCORRETTA),
			
	ERRORE_424_ALLEGA_BODY("La funzionalità 'allega body' non è riuscita ad utilizzare il messaggio ricevuto: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_424_ALLEGA_BODY),
			
	ERRORE_425_SCARTA_BODY("La funzionalità 'scarta body' non è riuscita ad utilizzare il messaggio ricevuto: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_425_SCARTA_BODY),
			
	ERRORE_426_SERVLET_ERROR("Errore durante il processamento del messaggio di "+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_MESSAGGIO+" da parte del SOAPEngine: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR),
			
	ERRORE_427_MUSTUNDERSTAND_ERROR("Riscontrati header(s) \"MustUnderstand\" non processabili: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MUST_UNDERSTAND_HEADERS,
			CodiceErroreIntegrazione.CODICE_427_MUSTUNDERSTAND_ERROR),
			
	ERRORE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA("Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_IDENTITA_SERVIZIO_APPLICATIVO,
			CodiceErroreIntegrazione.CODICE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA),
			
	ERRORE_429_CONTENT_TYPE_NON_SUPPORTATO("Il valore dell'header HTTP Content-Type ("+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_CONTENT_TYPE_TROVATO+") non rientra tra quelli supportati dal protocollo ("+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_CONTENT_TYPE_SUPPORTATI+")",
			CodiceErroreIntegrazione.CODICE_429_CONTENT_TYPE_NON_SUPPORTATO),
			
	ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR("SOAP Envelope contiene un namespace ("+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_TROVATO
			+") diverso da quello atteso per messaggi "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_SOAP_VERSION+" ("+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_SUPPORTATI+")",
			CodiceErroreIntegrazione.CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR),
			
	ERRORE_431_GESTORE_CREDENZIALI_ERROR("Riscontrato errore durante la gestione delle credenziali effettuata tramite il gestore ["+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_GESTORE_CREDENZIALI+"]: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR),

	ERRORE_432_PARSING_EXCEPTION_RICHIESTA("Il contenuto applicativo della richiesta ricevuta non è processabile: "+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_432_PARSING_EXCEPTION_RICHIESTA),
			
	ERRORE_433_CONTENT_TYPE_NON_PRESENTE("Il messaggio non contiene l'header HTTP Content-Type richiesto dalla specifica SOAP (valori ammessi: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_CONTENT_TYPE_SUPPORTATI+")",
			CodiceErroreIntegrazione.CODICE_433_CONTENT_TYPE_NON_PRESENTE),			
	
	ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE("La gestione della funzionalità di correlazione applicativa, per il messaggio di risposta, ha generato un errore: "+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE),
			
	ERRORE_435_LOCAL_FORWARD_CONFIG_NON_VALIDA("La funzionalità local-forward non è utilizzabile nella configurazione richiesta: "+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR),
			
	ERRORE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL("Il tipo "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO+
			" associato al soggetto fruitore "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_NOME+
			" non è tra i tipi supportati dal protocollo "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PROTOCOL+" (tipi supportati: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI,
			CodiceErroreIntegrazione.CODICE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL),
			
	ERRORE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL("Il tipo "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO+
			" associato al soggetto erogatore "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_NOME+
			" non è tra i tipi supportati dal protocollo "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PROTOCOL+" (tipi supportati: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI,
			CodiceErroreIntegrazione.CODICE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL),
			
	ERRORE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL("Il tipo "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO+
			" associato al servizio"+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_NOME+
			" versione "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_VERSIONE+
			" non è tra i tipi supportati dal protocollo "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PROTOCOL+" (tipi supportati: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI,
			CodiceErroreIntegrazione.CODICE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL),
			
	ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL("Il servizio richiede una funzionalità non supportata dal protocollo "+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PROTOCOL+": "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL),
	
	ERRORE_440_PARSING_EXCEPTION_RISPOSTA("Il contenuto applicativo della risposta ricevuta non è processabile: "+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,
			CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA),
	
	ERRORE_441_PORTA_NON_INVOCABILE_DIRETTAMENTE("La porta utilizzata"+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_PARAMETRI+" non è invocabile direttamente",
			CodiceErroreIntegrazione.CODICE_441_PORTA_NON_INVOCABILE_DIRETTAMENTE),
	
	ERRORE_442_RIFERIMENTO_ID_MESSAGGIO("Tra le informazioni di integrazione non è stato rilevato il riferimento ad un identificativo di un messaggio precedente",
			CodiceErroreIntegrazione.CODICE_442_RIFERIMENTO_ID_MESSAGGIO),
	
	ERRORE_443_TOKEN_NON_PRESENTE("Token non presente",
			CodiceErroreIntegrazione.CODICE_443_TOKEN_NON_PRESENTE),
	
	ERRORE_444_TOKEN_NON_VALIDO("Token non valido",
			CodiceErroreIntegrazione.CODICE_444_TOKEN_NON_VALIDO),
	
	ERRORE_445_TOKEN_AUTORIZZAZIONE_FALLITA("La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
			CodiceErroreIntegrazione.CODICE_445_TOKEN_AUTORIZZAZIONE_FALLITA),

	ERRORE_446_PORTA_SOSPESA("Porta disabilitata",
			CodiceErroreIntegrazione.CODICE_446_SUSPEND),
	
	ERRORE_447_API_NON_INVOCABILE_CONTESTO_UTILIZZATO("L'API invocata possiede un service binding non abilitato sul contesto utilizzato",
			CodiceErroreIntegrazione.CODICE_447_API_NON_INVOCABILE_CONTESTO_UTILIZZATO),
	
	ERRORE_448_API_NON_INVOCABILE_TIPO_SERVIZIO_UTILIZZATO("L'API invocata possiede un service binding non abilitato per il tipo di servizio utilizzato",
			CodiceErroreIntegrazione.CODICE_448_API_NON_INVOCABILE_TIPO_SERVIZIO_UTILIZZATO),
	
	ERRORE_449_TIPO_SOGGETTO_APPLICATIVO_TOKEN_NOT_SUPPORTED_BY_PROTOCOL("Il tipo "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO+
			" del soggetto appartenente all'applicativo token "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_NOME+
			" non è tra i tipi supportati dal protocollo "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PROTOCOL+" (tipi supportati: "+CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI,
			CodiceErroreIntegrazione.CODICE_449_TIPO_SOGGETTO_APPLICATIVO_TOKEN_NOT_SUPPORTED_BY_PROTOCOL),
	
	// errori spediti in buste errore
	
	ERRORE_450_PA_INESISTENTE("La porta applicativa richiesta dalla busta non esiste",
			CodiceErroreIntegrazione.CODICE_450_PA_INESISTENTE),
	
	ERRORE_451_SOGGETTO_INESISTENTE("Il soggetto richiesto dalla busta non è gestito dalla PdD",
			CodiceErroreIntegrazione.CODICE_451_SOGGETTO_INESISTENTE),
			
	ERRORE_452_BUSTA_GIA_RICEVUTA("La busta è già stata ricevuta",
			CodiceErroreIntegrazione.CODICE_452_BUSTA_GIA_RICEVUTA),
	
	ERRORE_453_SA_INESISTENTE("Il servizio applicativo associato alla porta applicativa richiesta dalla busta non esiste",
			CodiceErroreIntegrazione.CODICE_453_SA_INESISTENTE),
			
	ERRORE_454_BUSTA_PRESENTE_RISPOSTA_APPLICATIVA("Il messaggio inviato al servizio di consegna contenuti applicativi presenta nell'header una busta",
			CodiceErroreIntegrazione.CODICE_454_BUSTA_PRESENTE_RISPOSTA_APPLICATIVA),
	
	ERRORE_455_DATI_BUSTA_DIFFERENTI_PA_INVOCATA( (CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_OGGETTO_DIVERSO_TRA_BUSTA_E_PA+" ("+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_DATO_BUSTA+") presente nel messaggio di protocollo ricevuto differente da quello definito ("+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_DATO_PA+") nella porta applicativa invocata ("+
			CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_LOCATION+")"),
			CodiceErroreIntegrazione.CODICE_455_DATI_BUSTA_DIFFERENTI_PA_INVOCATA),

	ERRORE_4XX_CUSTOM(CostantiProtocollo.KEY_ERRORE_CUSTOM,CodiceErroreIntegrazione.CODICE_4XX_CUSTOM);

	
	
	private final String descrizione;
	private final CodiceErroreIntegrazione codiceErrore;
	
	ErroriIntegrazione(String descrizione,CodiceErroreIntegrazione codiceErrore){
		this.descrizione = descrizione;
		this.codiceErrore = codiceErrore;
	}
	
	@Override
	public String toString() {
		return newErroreIntegrazione().toString();
	}
	
	private ErroreIntegrazione newErroreIntegrazione(SOAPFaultCode faultCode, KeyValueObject ... keyValueObjects){
		return newErroreIntegrazione(this.descrizione, this.codiceErrore, faultCode, keyValueObjects);
	}
	
	private ErroreIntegrazione newErroreIntegrazione(KeyValueObject ... keyValueObjects){
		return newErroreIntegrazione(this.descrizione, this.codiceErrore, keyValueObjects);
	}
	
	@SuppressWarnings("unused")
	private ErroreIntegrazione newErroreIntegrazione(String descrizioneParam, KeyValueObject ... keyValueObjects){
		return newErroreIntegrazione(descrizioneParam, this.codiceErrore, keyValueObjects);
	}
	@SuppressWarnings("unused")
	private ErroreIntegrazione newErroreIntegrazione(CodiceErroreIntegrazione codiceParam, KeyValueObject ... keyValueObjects){
		return newErroreIntegrazione(this.descrizione, codiceParam, keyValueObjects);
	}
	private ErroreIntegrazione newErroreIntegrazione(String descrizioneParam,CodiceErroreIntegrazione codiceParam, KeyValueObject ... keyValueObjects){
		String newDescrizione = new String(descrizioneParam!=null ? descrizioneParam : "Internal Error");
		return new ErroreIntegrazione(newDescrizione, codiceParam, keyValueObjects);
	}
	
	private ErroreIntegrazione newErroreIntegrazione(String descrizioneParam,CodiceErroreIntegrazione codiceParam, SOAPFaultCode faultCode, KeyValueObject ... keyValueObjects){
		String newDescrizione = new String(descrizioneParam!=null ? descrizioneParam : "Internal Error");
		return new ErroreIntegrazione(newDescrizione, codiceParam, faultCode, keyValueObjects);
	}
	
	public ErroreIntegrazione getErroreIntegrazione() {
		if( this.equals(ERRORE_401_PORTA_INESISTENTE) ||
			this.equals(ERRORE_402_AUTENTICAZIONE_FALLITA) ||
			this.equals(ERRORE_404_AUTORIZZAZIONE_FALLITA_SA) ||
			this.equals(ERRORE_404_AUTORIZZAZIONE_FALLITA_SA_ANONIMO) ||
			this.equals(ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE) ||
			this.equals(ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA) ||
			this.equals(ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA) ||
			this.equals(ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA) ||
			this.equals(ERRORE_421_MSG_SOAP_NON_COSTRUIBILE_TRAMITE_RICHIESTA_APPLICATIVA) ||
			this.equals(ERRORE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA) ||
			this.equals(ERRORE_423_SERVIZIO_CON_AZIONE_SCORRETTA) ||
			this.equals(ERRORE_424_ALLEGA_BODY) ||
			this.equals(ERRORE_425_SCARTA_BODY) ||
			this.equals(ERRORE_426_SERVLET_ERROR) ||
			this.equals(ERRORE_427_MUSTUNDERSTAND_ERROR) ||
			this.equals(ERRORE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA) ||
			this.equals(ERRORE_429_CONTENT_TYPE_NON_SUPPORTATO) ||
			this.equals(ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR) ||
			this.equals(ERRORE_431_GESTORE_CREDENZIALI_ERROR) ||
			this.equals(ERRORE_432_PARSING_EXCEPTION_RICHIESTA) ||
			this.equals(ERRORE_433_CONTENT_TYPE_NON_PRESENTE) ||
			this.equals(ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE) ||
			this.equals(ERRORE_435_LOCAL_FORWARD_CONFIG_NON_VALIDA) ||
			this.equals(ERRORE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL) ||
			this.equals(ERRORE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL) ||
			this.equals(ERRORE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL) ||
			this.equals(ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL) ||
			this.equals(ERRORE_441_PORTA_NON_INVOCABILE_DIRETTAMENTE) ||
			this.equals(ERRORE_455_DATI_BUSTA_DIFFERENTI_PA_INVOCATA) ||
			this.equals(ERRORE_4XX_CUSTOM) ||
			this.equals(ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE) ||
			this.equals(ERRORE_517_RISPOSTA_RICHIESTA_NON_RITORNATA) ||
			this.equals(ERRORE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT) ||
			this.equals(ERRORE_537_BUSTA_GIA_RICEVUTA) ||
			this.equals(ERRORE_5XX_CUSTOM)
			){
			throw new RuntimeException("Il metodo non può essere utilizzato con il messaggio "+this.name());
		}
		return newErroreIntegrazione();
	}
	
	
	/* 5XX */
		
	public ErroreIntegrazione get5XX_ErroreProcessamento(Exception e) {
		return get5XX_ErroreProcessamento(Utilities.readFirstErrorValidMessageFromException(e), ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.codiceErrore);
	}
	public ErroreIntegrazione get5XX_ErroreProcessamento(String descrizione) {
		return get5XX_ErroreProcessamento(descrizione, ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.codiceErrore);
	}
	public ErroreIntegrazione get5XX_ErroreProcessamento(CodiceErroreIntegrazione codiceErroreIntegrazione) {
		return get5XX_ErroreProcessamento(ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.descrizione, codiceErroreIntegrazione);
	}
	public ErroreIntegrazione get5XX_ErroreProcessamento(Throwable e,CodiceErroreIntegrazione codiceErroreIntegrazione) {
		return get5XX_ErroreProcessamento(Utilities.readFirstErrorValidMessageFromException(e), codiceErroreIntegrazione);
	}
	public ErroreIntegrazione get5XX_ErroreProcessamento(String descrizione,CodiceErroreIntegrazione codiceErroreIntegrazione) {
		if(this.equals(ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO)){
			if(codiceErroreIntegrazione.getCodice()<500){
				throw new RuntimeException("Il metodo può essere utilizzato solo con un codice maggiore o uguale a 500");	
			}
			return newErroreIntegrazione(descrizione, codiceErroreIntegrazione);
		}else{
			throw new RuntimeException("Il metodo può essere utilizzato solo con il messaggio "+ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.name());
		}
	}
	
	public ErroreIntegrazione get516_ServizioApplicativoNonDisponibile() {
		return get516_ConnettoreUtilizzatoConErrore(CostantiProtocollo.SERVIZIO_APPLICATIVO_NON_DISPONIBILE, null);
	}
	public ErroreIntegrazione get516_PortaDiDominioNonDisponibile(String nomePdd) {
		return get516_ConnettoreUtilizzatoConErrore(CostantiProtocollo.PDD_NON_DISPONIBILE, nomePdd);
	}
	private ErroreIntegrazione get516_ConnettoreUtilizzatoConErrore(String msgErrore,String nomePdd) {
		if(!this.equals(ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		if(nomePdd!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_CONNETTORE_ERRORE_PDD,nomePdd));
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione get517_RispostaRichiestaNonRitornata() {
		return get517_RispostaRichiestaNonRitornata(CostantiProtocollo.SERVIZIO_APPLICATIVO_NON_DISPONIBILE, null);
	}
	public ErroreIntegrazione get517_RispostaRichiestaNonRitornata(String nomePdd) {
		return get517_RispostaRichiestaNonRitornata(CostantiProtocollo.PDD_NON_DISPONIBILE, nomePdd);
	}
	private ErroreIntegrazione get517_RispostaRichiestaNonRitornata(String msgErrore,String nomePdd) {
		if(!this.equals(ERRORE_517_RISPOSTA_RICHIESTA_NON_RITORNATA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_517_RISPOSTA_RICHIESTA_NON_RITORNATA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		if(nomePdd!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_CONNETTORE_ERRORE_PDD,nomePdd));
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione get518_RispostaRichiestaRitornataComeFault() {
		return get518_RispostaRichiestaRitornataComeFault(CostantiProtocollo.SERVIZIO_APPLICATIVO_NON_DISPONIBILE, null);
	}
	public ErroreIntegrazione get518_RispostaRichiestaRitornataComeFault(String nomePdd) {
		return get518_RispostaRichiestaRitornataComeFault(CostantiProtocollo.PDD_NON_DISPONIBILE, nomePdd);
	}
	private ErroreIntegrazione get518_RispostaRichiestaRitornataComeFault(String msgErrore,String nomePdd) {
		if(!this.equals(ERRORE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		if(nomePdd!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_CONNETTORE_ERRORE_PDD,nomePdd));
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione get537_BustaGiaRicevuta(String id) {
		if(!this.equals(ERRORE_537_BUSTA_GIA_RICEVUTA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_537_BUSTA_GIA_RICEVUTA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_ID_BUSTA,id));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione get559_RicevutaRispostaConErroreTrasporto(String msgErrore) {
		if(!this.equals(ERRORE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione get5XX_Custom(String descrizione,String codiceErroreIntegrazione) {
		if(this.equals(ERRORE_5XX_CUSTOM)){
			if(codiceErroreIntegrazione==null){
				throw new RuntimeException("Il metodo può essere utilizzato senza fornire un codice errore di integrazione personalizzato");	
			}
			ErroreIntegrazione e = newErroreIntegrazione(descrizione, CodiceErroreIntegrazione.CODICE_5XX_CUSTOM);
			e.setCodiceCustom(codiceErroreIntegrazione);
			return e;
		}else{
			throw new RuntimeException("Il metodo può essere utilizzato solo con il messaggio "+ERRORE_5XX_CUSTOM.name());
		}
	}
	
	
	
	/* 4XX */
	
	public ErroreIntegrazione getErrore401_PortaInesistente(String motivoErroreInvocazione) {
		return getErrore401_PortaInesistente(motivoErroreInvocazione, null, null,null);
	}
	public ErroreIntegrazione getErrore401_PortaInesistente(String motivoErroreInvocazione,String location,String urlInvocazione) {
		return getErrore401_PortaInesistente(motivoErroreInvocazione, location, urlInvocazione,null);
	}
	public ErroreIntegrazione getErrore401_PortaInesistente(String motivoErroreInvocazione,String servizioApplicativo) {
		return getErrore401_PortaInesistente(motivoErroreInvocazione, null, null,servizioApplicativo);
	}
	public ErroreIntegrazione getErrore401_PortaInesistente(String motivoErroreInvocazione,String location,String urlInvocazione,String servizioApplicativo) {
		if(!this.equals(ERRORE_401_PORTA_INESISTENTE)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_401_PORTA_INESISTENTE.name());
		}
		StringBuilder bf = new StringBuilder();
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		if(location!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_LOCATION,location));
			bf.append(" porta["+location+"]");
		}
		if(urlInvocazione!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_URL_INVOCAZIONE,urlInvocazione));
			bf.append(" urlInvocazione["+urlInvocazione+"]");
		}
		if(servizioApplicativo!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_SERVIZIO_APPLICATIVO,servizioApplicativo));
			bf.append(" servizioApplicativo["+servizioApplicativo+"]");
		}
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_PARAMETRI,bf.toString()));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,motivoErroreInvocazione));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore402_AutenticazioneFallita(String msgErrore) {
		return getErrore402_AutenticazioneFallita(msgErrore, null, null, null);
	}
	public ErroreIntegrazione getErrore402_AutenticazioneFallitaBasic(String msgErrore,String username,String password) {
		return getErrore402_AutenticazioneFallita(msgErrore, username, password, null);
	}
	public ErroreIntegrazione getErrore402_AutenticazioneFallitaSsl(String msgErrore,String subject) {
		return getErrore402_AutenticazioneFallita(msgErrore, null, null, subject);
	}
	public ErroreIntegrazione getErrore402_AutenticazioneFallitaPrincipal(String msgErrore,String userId) {
		return getErrore402_AutenticazioneFallita(msgErrore, userId, null, null);
	}
	public ErroreIntegrazione getErrore402_AutenticazioneFallitaToken(String msgErrore,String clientId) {
		return getErrore402_AutenticazioneFallita(msgErrore, clientId, null, null);
	}
	public ErroreIntegrazione getErrore402_AutenticazioneFallita(String msgErrore,String username,String password,String subject) {
		if(!this.equals(ERRORE_402_AUTENTICAZIONE_FALLITA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_402_AUTENTICAZIONE_FALLITA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		if(username!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_USERNAME,username));
		}
		if(password!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_PASSWORD,password));
		}
		if(subject!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_SUBJECT,subject));
		}
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
		
	public ErroreIntegrazione getErrore404_AutorizzazioneFallitaServizioApplicativo(String servizioApplicativo) {
		return getErrore404_AutorizzazioneFallitaServizioApplicativo(servizioApplicativo, null);
	}
	public ErroreIntegrazione getErrore404_AutorizzazioneFallitaServizioApplicativo(String servizioApplicativo, String msgErrore) {
		if(!this.equals(ERRORE_404_AUTORIZZAZIONE_FALLITA_SA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_IDENTITA_SERVIZIO_APPLICATIVO,servizioApplicativo));
		if(msgErrore!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,": "+msgErrore));
		}
		else{
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,""));
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore404_AutorizzazioneFallitaServizioApplicativoAnonimo() {
		return getErrore404_AutorizzazioneFallitaServizioApplicativoAnonimo(null);
	}
	public ErroreIntegrazione getErrore404_AutorizzazioneFallitaServizioApplicativoAnonimo(String msgErrore) {
		if(!this.equals(ERRORE_404_AUTORIZZAZIONE_FALLITA_SA_ANONIMO)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_404_AUTORIZZAZIONE_FALLITA_SA_ANONIMO.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		if(msgErrore!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE," "+msgErrore));
		}
		else{
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,""));
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore416_CorrelazioneApplicativaRichiesta(String msgErrore) {
		if(!this.equals(ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore417_CostruzioneValidatoreTramiteInterfacciaFallita(String tipoInterfaccia) {
		if(!this.equals(ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_INTERFACCIA,tipoInterfaccia));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(String tipoInterfaccia, String errorMsg, boolean overwriteMessageError) {
		
		if(overwriteMessageError) {
			return new ErroreIntegrazione(errorMsg, this.codiceErrore);
		}
		
		if(!this.equals(ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_INTERFACCIA,tipoInterfaccia));
		
		String error = ""; 
		if(errorMsg!=null && !"".equals(errorMsg) && !"null".equals(errorMsg)) {
			error = ": "+errorMsg;
		}
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_VALIDAZIONE_ERROR_MSG,error));
			
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(String tipoInterfaccia, String errorMsg, boolean overwriteMessageError) {
		
		if(overwriteMessageError) {
			return new ErroreIntegrazione(errorMsg, this.codiceErrore);
		}
		
		if(!this.equals(ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_INTERFACCIA,tipoInterfaccia));
		
		String error = ""; 
		if(errorMsg!=null && !"".equals(errorMsg) && !"null".equals(errorMsg)) {
			error = ": "+errorMsg;
		}
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_VALIDAZIONE_ERROR_MSG,error));
		
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}

	public ErroreIntegrazione getErrore421_MessaggioSOAPNonGenerabile(String msgErrore) {
		if(!this.equals(ERRORE_421_MSG_SOAP_NON_COSTRUIBILE_TRAMITE_RICHIESTA_APPLICATIVA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_421_MSG_SOAP_NON_COSTRUIBILE_TRAMITE_RICHIESTA_APPLICATIVA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore422_MessaggioSOAPNonGenerabileTramiteImbustamentoSOAP(String msgErrore) {
		if(!this.equals(ERRORE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore423_ServizioConAzioneScorretta(String msgErrore) {
		if(!this.equals(ERRORE_423_SERVIZIO_CON_AZIONE_SCORRETTA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_423_SERVIZIO_CON_AZIONE_SCORRETTA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore424_AllegaBody(String msgErrore) {
		if(!this.equals(ERRORE_424_ALLEGA_BODY)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_424_ALLEGA_BODY.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore425_ScartaBody(String msgErrore) {
		if(!this.equals(ERRORE_425_SCARTA_BODY)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_425_SCARTA_BODY.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore426_ServletError(boolean isRichiesta,Throwable eProcessamento) {
		return getErrore426_ServletError(isRichiesta, null, eProcessamento);
	}
	public ErroreIntegrazione getErrore426_ServletError(boolean isRichiesta,String error) {
		return getErrore426_ServletError(isRichiesta, error, null);
	}
	public ErroreIntegrazione getErrore426_ServletError(boolean isRichiesta,String error,Throwable eProcessamento) {
		if(!this.equals(ERRORE_426_SERVLET_ERROR)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_426_SERVLET_ERROR.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		if(isRichiesta){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_MESSAGGIO,"richiesta"));
		}
		else{
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_MESSAGGIO,"risposta"));
		}
		String msgErrore = null;
		if(error!=null){
			msgErrore = new String(error);
		}
		if(msgErrore==null){
			if(eProcessamento!=null){
				if(eProcessamento.getMessage()!=null)
					msgErrore = "ErroreProcessamento: "+eProcessamento.getMessage();
				else
					msgErrore ="ErroreProcessamento: "+eProcessamento.toString();
			}
		}else{
			if(eProcessamento!=null){
				if(eProcessamento.getMessage()!=null)
					msgErrore = msgErrore+" "+eProcessamento.getMessage();
				else
					msgErrore = msgErrore+" "+eProcessamento.toString();
			}
		}
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore427_MustUnderstandHeaders(String headers) {
		if(!this.equals(ERRORE_427_MUSTUNDERSTAND_ERROR)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_427_MUSTUNDERSTAND_ERROR.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MUST_UNDERSTAND_HEADERS,headers));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore428_AutorizzazioneContenutoFallita(String servizioApplicativo) {
		if(!this.equals(ERRORE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_IDENTITA_SERVIZIO_APPLICATIVO,servizioApplicativo));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore429_ContentTypeNonSupportato(String contentTypeTrovato, List<String> contentTypesSupportati) {
		if(!this.equals(ERRORE_429_CONTENT_TYPE_NON_SUPPORTATO)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_429_CONTENT_TYPE_NON_SUPPORTATO.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_CONTENT_TYPE_TROVATO,contentTypeTrovato));
		if(contentTypesSupportati!=null && contentTypesSupportati.size()>0){
			StringBuilder bf = new StringBuilder();
			for (int i = 0; i < contentTypesSupportati.size(); i++) {
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append(contentTypesSupportati.get(i));
			}
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_CONTENT_TYPE_SUPPORTATI,bf.toString()));
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore430_SoapNamespaceNonSupportato(MessageType messageType, String namespaceTrovato) {
		if(!this.equals(ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_SOAP_VERSION,messageType.getMessageVersionAsString()));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_TROVATO,namespaceTrovato));
		try{
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_SUPPORTATI,SoapUtils.getSoapEnvelopeNS(messageType)));
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		
		return newErroreIntegrazione(SOAPFaultCode.VersionMismatch, lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore431_ErroreGestoreCredenziali(String tipoGestore,Exception e) {
		if(!this.equals(ERRORE_431_GESTORE_CREDENZIALI_ERROR)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_431_GESTORE_CREDENZIALI_ERROR.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO_GESTORE_CREDENZIALI,tipoGestore));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,e.getMessage()));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore432_MessaggioRichiestaMalformato(Throwable e) {
		if(!this.equals(ERRORE_432_PARSING_EXCEPTION_RICHIESTA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_432_PARSING_EXCEPTION_RICHIESTA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		if(e.getMessage()!=null)
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,e.getMessage()));
		else
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,e.toString()));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore433_ContentTypeNonPresente(List<String> contentTypesSupportati) {
		if(!this.equals(ERRORE_433_CONTENT_TYPE_NON_PRESENTE)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_433_CONTENT_TYPE_NON_PRESENTE.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		if(contentTypesSupportati!=null && contentTypesSupportati.size()>0){
			StringBuilder bf = new StringBuilder();
			for (int i = 0; i < contentTypesSupportati.size(); i++) {
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append(contentTypesSupportati.get(i));
			}
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_CONTENT_TYPE_SUPPORTATI,bf.toString()));
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore434_CorrelazioneApplicativaRisposta(String msgErrore) {
		if(!this.equals(ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore435_LocalForwardConfigNonValida(String msgErrore) {
		if(!this.equals(ERRORE_435_LOCAL_FORWARD_CONFIG_NON_VALIDA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_435_LOCAL_FORWARD_CONFIG_NON_VALIDA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore436_TipoSoggettoFruitoreNotSupportedByProtocol(IDSoggetto fruitore,IProtocolFactory<?> protocolFactory) {
		if(!this.equals(ERRORE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO,fruitore.getTipo()));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_NOME,fruitore.getNome()));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PROTOCOL,protocolFactory.getProtocol()));
		try{
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI,protocolFactory.createProtocolConfiguration().getTipiSoggetti().toString()));
		}catch(Exception e){
			// ignore
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore437_TipoSoggettoErogatoreNotSupportedByProtocol(IDSoggetto erogatore,IProtocolFactory<?> protocolFactory) {
		if(!this.equals(ERRORE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO,erogatore.getTipo()));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_NOME,erogatore.getNome()));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PROTOCOL,protocolFactory.getProtocol()));
		try{
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI,protocolFactory.createProtocolConfiguration().getTipiSoggetti().toString()));
		}catch(Exception e){
			// ignore
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore438_TipoServizioNotSupportedByProtocol(ServiceBinding serviceBinding, IDServizio servizio,IProtocolFactory<?> protocolFactory) {
		if(!this.equals(ERRORE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO,servizio.getTipo()));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_NOME,servizio.getNome()));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_VERSIONE,servizio.getVersione().intValue()+""));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PROTOCOL,protocolFactory.getProtocol()));
		try{
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI,protocolFactory.createProtocolConfiguration().getTipiServizi(serviceBinding).toString()));
		}catch(Exception e){
			// ignore
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore439_FunzionalitaNotSupportedByProtocol(String msgErrore,IProtocolFactory<?> protocolFactory) {
		if(!this.equals(ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,msgErrore));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PROTOCOL,protocolFactory.getProtocol()));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore440_MessaggioRispostaMalformato(Throwable e) {
		if(!this.equals(ERRORE_440_PARSING_EXCEPTION_RISPOSTA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_440_PARSING_EXCEPTION_RISPOSTA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		if(e.getMessage()!=null)
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,e.getMessage()));
		else
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE,e.toString()));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore441_PortaNonInvocabileDirettamente(String location,String urlInvocazione) {
		return getErrore441_PortaNonInvocabileDirettamente(location, urlInvocazione,null);
	}
	public ErroreIntegrazione getErrore441_PortaNonInvocabileDirettamente(String servizioApplicativo) {
		return getErrore441_PortaNonInvocabileDirettamente(null, null,servizioApplicativo);
	}
	public ErroreIntegrazione getErrore441_PortaNonInvocabileDirettamente(String location,String urlInvocazione,String servizioApplicativo) {
		if(!this.equals(ERRORE_441_PORTA_NON_INVOCABILE_DIRETTAMENTE)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_401_PORTA_INESISTENTE.name());
		}
		StringBuilder bf = new StringBuilder();
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		if(location!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_LOCATION,location));
			bf.append(" porta["+location+"]");
		}
		if(urlInvocazione!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_URL_INVOCAZIONE,urlInvocazione));
			bf.append(" urlInvocazione["+urlInvocazione+"]");
		}
		if(servizioApplicativo!=null){
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_SERVIZIO_APPLICATIVO,servizioApplicativo));
			bf.append(" servizioApplicativo["+servizioApplicativo+"]");
		}
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_PARAMETRI,bf.toString()));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore449_TipoSoggettoApplicativoTokenNotSupportedByProtocol(IDServizioApplicativo idApplicativoToken,IProtocolFactory<?> protocolFactory) {
		if(!this.equals(ERRORE_449_TIPO_SOGGETTO_APPLICATIVO_TOKEN_NOT_SUPPORTED_BY_PROTOCOL)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_449_TIPO_SOGGETTO_APPLICATIVO_TOKEN_NOT_SUPPORTED_BY_PROTOCOL.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPO,idApplicativoToken.getIdSoggettoProprietario().getTipo()));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_NOME,idApplicativoToken.getNome()+"@"+idApplicativoToken.getIdSoggettoProprietario().toString()));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PROTOCOL,protocolFactory.getProtocol()));
		try{
			lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI,protocolFactory.createProtocolConfiguration().getTipiSoggetti().toString()));
		}catch(Exception e){
			// ignore
		}
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}
	
	public ErroreIntegrazione getErrore455DatiBustaDifferentiDatiPAInvocata(String oggetto,String datoBusta, String datoPA, String locationPA) {
		if(!this.equals(ERRORE_455_DATI_BUSTA_DIFFERENTI_PA_INVOCATA)){
			throw new RuntimeException("Il seguente metodo può solo essere utilizzato con il messaggio "+ERRORE_455_DATI_BUSTA_DIFFERENTI_PA_INVOCATA.name());
		}
		List<KeyValueObject> lista = new ArrayList<KeyValueObject>();
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_OGGETTO_DIVERSO_TRA_BUSTA_E_PA,oggetto));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_DATO_BUSTA,datoBusta));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_DATO_PA,datoPA));
		lista.add(new KeyValueObject(CostantiProtocollo.KEY_ERRORE_INTEGRAZIONE_PORTA_LOCATION,locationPA));
		return newErroreIntegrazione(lista.toArray(new KeyValueObject[lista.size()]));
	}

	public ErroreIntegrazione get4XX_Custom(String descrizione,String codiceErroreIntegrazione) {
		if(this.equals(ERRORE_4XX_CUSTOM)){
			if(codiceErroreIntegrazione==null){
				throw new RuntimeException("Il metodo può essere utilizzato senza fornire un codice errore di integrazione personalizzato");	
			}
			ErroreIntegrazione e = newErroreIntegrazione(descrizione, CodiceErroreIntegrazione.CODICE_4XX_CUSTOM);
			e.setCodiceCustom(codiceErroreIntegrazione);
			return e;
		}else{
			throw new RuntimeException("Il metodo può essere utilizzato solo con il messaggio "+ERRORE_4XX_CUSTOM.name());
		}
	}
	
}
