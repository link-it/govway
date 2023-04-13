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
package org.openspcoop2.protocol.spcoop.testsuite.core;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;

/**
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class CostantiErroriIntegrazione {


	/* ******** MESSAGGI  di PROCESSAMENTO  ******** */

	/** String che rappresenta il messaggio per un qualsiasi errore di processamento: SistemaNonDisponibile*/
	public static final String MSG_5XX_SISTEMA_NON_DISPONIBILE = CostantiProtocollo.SISTEMA_NON_DISPONIBILE;
	/** String che rappresenta il messaggio per un qualsiasi errore di processamento: PdD non disponibile*/
	public static final String MSG_516_PDD_NON_DISPONIBILE = CostantiProtocollo.PDD_NON_DISPONIBILE;
	/** String che rappresenta il messaggio per un qualsiasi errore di processamento: Servizio Applicativo non disponibile*/
	public static final String MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE = "Servizio Applicativo non disponibile";

	/** String che contiene un codice di errore OpenSPCoop2: BustaRicevutaPrecedentemente in msg*/
	public static final String MSG_537_BUSTA_GIA_RICEVUTA = 
			"La richiesta assegnata alla busta con ID=XXX è già stata ricevuta e risulta ancora in processamento";


	/** String che contiene un codice di errore OpenSPCoop2: BustaRichiestaAsincronaAncoraInProcessamento,538 */
	public static final String MSG_538_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO = 
			"Busta asincrona non gestibile poichè risulta ancora in gestione nella porta la precedente richiesta.";
	/** String che contiene un codice di errore OpenSPCoop2: BustaRicevutaRichiestaAsincronaAncoraInProcessamento,539 */
	public static final String MSG_539_RICEVUTA_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO = 
			"Busta asincrona non gestibile poichè risulta ancora in gestione nella porta la precedente ricevuta alla richiesta.";



	/* ******** MESSAGGI 4XX  ******** */

	/** String che contiene un codice di errore OpenSPCoop2: PortaDelegataInesistente in msg*/
	public static final String MSG_401_PD_INESISTENTE = "La porta invocata non esiste";

	/** String che contiene un codice di errore OpenSPCoop2: AutenticazioneFallita in msg*/
	public static final String MSG_402_AUTENTICAZIONE_FALLITA = "Autenticazione fallita, credenziali fornite non corrette";
	/** String che contiene un codice di errore OpenSPCoop2: AutenticazioneFallita in msg*/
	public static final String MSG_402_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE = "Autenticazione fallita, credenziali non fornite";
	/** String che contiene un codice di errore OpenSPCoop2: AutenticazioneFallita in msg*/
	public static final String MSG_402_AUTENTICAZIONE_FALLITA_IDENTITA_SERVIZIO_APPLICATIVO_ERRATA = "Autenticazione fallita, L'identità del servizio applicativo fornita [SERVIZIO_APPLICATIVO] non esiste nella configurazione"; 
	
	/** String che contiene un codice di errore OpenSPCoop2: Pattern di ricerca PD non valido in msg*/
	public static final String MSG_403_PD_PATTERN_AZIONE_NON_VALIDA = "Identificazione dinamica dell'operazione fallita";
	

	/** String che contiene un codice di errore OpenSPCoop2: AutorizzazioneFallita in msg*/
	public static final String MSG_404_AUTORIZZAZIONE_FALLITA = 
			"Il servizio applicativo SERVIZIO_APPLICATIVO non risulta autorizzato a fruire del servizio richiesto";

	/** String che contiene un codice di errore OpenSPCoop2: ServizioNonTrovato in msg*/
	public static final String MSG_405_SERVIZIO_NON_TROVATO =  
			"Servizio richiesto con la porta delegata non trovato nel Registro dei Servizi";

	/** String che contiene un codice di errore OpenSPCoop2: Messaggi per il servizio applicativo non trovati*/
	public static final String MSG_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI=  
			"Non sono stati rilevati messaggi per il servizio applicativo";

	/** String che contiene un codice di errore OpenSPCoop2: Messaggio richiesto non trovato*/
	public static final String MSG_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO=  
			"Non è stato rilevato il messaggio richiesto dal servizio applicativo";

	/** String che contiene un codice di errore OpenSPCoop2: ServizioNonTrovato in msg*/
	public static final String MSG_408_SERVIZIO_CORRELATO_NON_TROVATO =  
			"Servizio correlato (o azione correlata), associato al servizio richiesto con la porta delegata, non trovato nel Registro dei Servizi";
	
	/** String che contiene un codice di errore OpenSPCoop2: RispostaAsincronaSenzaRichiesta in msg*/
	public static final String MSG_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA =  
			"Risposta/RichiestaStato non generabile poichè non associata ad una precedente busta di richiesta asincrona";
	
	/** String che contiene un codice di errore OpenSPCoop2: AutenticazioneRichiesta in msg*/
	public static final String MSG_410_AUTENTICAZIONE_RICHIESTA = "Autenticazione necessaria per invocare il servizio richiesto";
	
	/** String che contiene un codice di errore OpenSPCoop2: RicezioneContenutiAsincroniRichiesta in msg*/
	public static final String MSG_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA = 
			"Parametri di consegna della risposta asincrona non presenti nella configurazione del servizio applicativo fruitore";
	
	/** String che contiene un codice di errore OpenSPCoop2: PortaDelegata invocabile solo per riferimento*/
	public static final String MSG_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO = 
			"Il servizio applicativo è autorizzato ad invocare la porta delegata solo attraverso una invocazione per riferimento, effettuabile tramite il servizio IntegrationManager";
	
	/** String che contiene un codice di errore OpenSPCoop2: PortaDelegata invocabile solo senza riferimento*/
	public static final String MSG_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO = 
			"Il servizio applicativo non è autorizzato ad invocare la porta delegata tramite una invocazione per riferimento effettuata attraverso il servizio IntegrationManager";
	
	/** String che contiene un codice di errore OpenSPCoop2: Funzionalità di consegna in ordine utilizzabile solo con profilo oneway*/
	public static final String MSG_414_CONSEGNA_IN_ORDINE_CON_PROFILO_NO_ONEWAY = 
			"Richiesta funzionalità di consegna in ordine con un profilo diverso da OneWay";
	
	/** String che contiene un codice di errore OpenSPCoop2: Funzionalità di consegna in ordine non utilizzabile senza id collaborazione*/
	public static final String MSG_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI = 
			"Richiesta funzionalità di consegna in ordine che non rispetta i vincoli richiesti; è possibile che la richiesta non sia accompagnata dalla funzionalità di gestione dei riscontri, un filtro duplicati o un id di collaborazione";
	
	/** String che contiene un codice di errore OpenSPCoop: Correlazione Applicativa non riuscita*/
	public static final String MSG_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE = 
			"La gestione della funzionalità di correlazione applicativa, per il messaggio di richiesta, ha generato un errore: ";
	
	public static final String WSDL_TYPE = "TIPO_WSDL";

	/** String che contiene un codice di errore OpenSPCoop2: Costruzione XSD non riuscita*/
	public static final String MSG_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA = 
			WSDL_TYPE+" del servizio non definito (o definito non correttamente) nel Registro dei Servizi";
	
	/** String che contiene un codice di errore OpenSPCoop2: Validazione xsd non riuscita*/
	public static final String MSG_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA = 
			"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio ("+WSDL_TYPE+") definito nel Registro";
	
	/** String che contiene un codice di errore OpenSPCoop2: Validazione xsd non riuscita*/
	public static final String MSG_419_VALIDAZIONE_WSDL_RISPOSTA_FALLITA = 
			"Il contenuto applicativo del messaggio di risposta non rispetta l'accordo di servizio ("+WSDL_TYPE+") definito nel Registro";
	
	/** String che contiene un codice di errore OpenSPCoop2: Busta presente in una richiesta applicativa */
	public static final String MSG_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA = 
			"Il messaggio inviato al servizio di ricezione contenuti applicativi presenta nell'header una busta";
	
	/** String che contiene un codice di errore OpenSPCoop2: MessaggioSOAP non presente in una richiesta applicativa (senza imbustamento richiesto) */
	public static final String MSG_421_MSG_SOAP_NON_PRESENTE_RICHIESTA_APPLICATIVA = 
			"I bytes inviati al servizio di ricezione contenuti applicativi non rappresentano un messaggio SOAP:";
	
	/** String che contiene un codice di errore OpenSPCoop2: MessaggioSOAP non presente in una richiesta applicativa (senza imbustamento richiesto) */
	public static final String MSG_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA = 
			"I bytes inviati al servizio di ricezione contenuti applicativi non sono utilizzabili per formare un messaggio SOAP tramite la funzionalità di imbustamento SOAP:";
	
	/** String che contiene un codice di errore OpenSPCoop2: ServizioNonInvocabile con l'azione specificata in msg*/
	public static final String MSG_423_SERVIZIO_CON_AZIONE_NON_CORRETTA_PREFIX =  
			"Azione richiesta non corretta: ";
	public static final String MSG_423_SERVIZIO_CON_AZIONE_NON_CORRETTA_API_TEMPLATE =  
			"NOME_API_INVOCATA";
	public static final String MSG_423_SERVIZIO_CON_AZIONE_SCORRETTA =  
			MSG_423_SERVIZIO_CON_AZIONE_NON_CORRETTA_PREFIX+"La richiesta effettuata non è associabile a nessuna azione dell'API "+MSG_423_SERVIZIO_CON_AZIONE_NON_CORRETTA_API_TEMPLATE+" (invocazione senza la definizione di una azione non permessa)";
	
	
	/** String che contiene un codice di errore OpenSPCoop2: ServizioNonInvocabile con l'azione specificata in msg*/
	public static final String MSG_424_ALLEGA_BODY =  
			"La funzionalità 'allega body' non è riuscita ad utilizzare il messaggio ricevuto";
	
	/** String che contiene un codice di errore OpenSPCoop2: ServizioNonInvocabile con l'azione specificata in msg*/
	public static final String MSG_425_SCARTA_BODY =  
			"La funzionalità 'scarta body' non è riuscita ad utilizzare il messaggio ricevuto";
	
	/** String che contiene un codice di errore OpenSPCoop2: Messaggio malformato e impossibile convertire in Message*/
	public static final String MSG_426_SERVLET_REQUEST_ERROR =  
			"Errore durante il processamento del messaggio di richiesta da parte del SOAPEngine: ";
	public static final String MSG_426_SERVLET_RESPONSE_ERROR =  
			"Errore durante il processamento del messaggio di risposta da parte del SOAPEngine: ";
	
	/** String che contiene un codice di errore OpenSPCoop2: Header senza Actor con MustUnderstand="1" non bypassabile*/
	public static final String MSG_427_MUSTUNDERSTAND_ERROR = 
			"Riscontrati header(s) \"MustUnderstand\" non processabili:"; 
	
	/** String che contiene un codice di errore OpenSPCoop2: AutorizzazioneFallita in msg*/
	public static final String MSG_428_AUTORIZZAZIONE_CONTENUTO_FALLITA = 
			"Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo ";
	
	/** String che contiene un codice di errore OpenSPCoop2: Content Type non supportato*/
	public static final String MSG_429_CONTENT_TYPE_KEY = "#MSG_429_CONTENT_TYPE_KEY#";
	public static final String MSG_429_CONTENT_TYPE_NON_SUPPORTATO = 
			"Il valore dell'header HTTP Content-Type ("+MSG_429_CONTENT_TYPE_KEY+") non rientra tra quelli supportati dal protocollo (text/xml)";
				
	/** String che contiene un codice di errore OpenSPCoop2: soap envelope namespace error */
	public static final String MSG_430_NAMESPACE_KEY = "#MSG_430_NAMESPACE_KEY#";
	public static final String MSG_430_SOAP_ENVELOPE_NAMESPACE_ERROR =  
			"SOAP Envelope contiene un namespace ("+MSG_430_NAMESPACE_KEY+") diverso da quello atteso per messaggi Soap-1.1 ("+Costanti.SOAP_ENVELOPE_NAMESPACE+")";
	
	/** String che contiene un codice di errore OpenSPCoop2: gestore credenziali error */
	public static final String MSG_431_TIPO_GESTORE_CREDENZIALI_KEY = "#MSG_431_GESTORE_KEY#";
	public static final String MSG_431_GESTORE_CREDENZIALI_ERROR =  
			"Riscontrato errore durante la gestione delle credenziali effettuata tramite il gestore ["+MSG_431_TIPO_GESTORE_CREDENZIALI_KEY+"]: ";
	
	/** String che contiene un codice di errore per messaggi malformati */
	public static final String MSG_432_MESSAGGIO_XML_MALFORMATO_RICHIESTA =  
			"Il contenuto applicativo della richiesta ricevuta non è processabile:";
	
	/** String che contiene un codice di errore OpenSPCoop2: Content Type non presente*/
	public static final String MSG_433_CONTENT_TYPE_NON_PRESENTE = 
			"Il messaggio non contiene l'header HTTP Content-Type richiesto dalla specifica SOAP (valori ammessi: ";
						//+SOAPVersion.SOAP11.getContentTypesAsString()+")"; TODO
	
	/** String che contiene un codice di errore OpenSPCoop: Correlazione Applicativa Risposta non riuscita*/
	public static final String MSG_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE = 
			"La gestione della funzionalità di correlazione applicativa, per il messaggio di risposta, ha generato un errore: ";
	
	/** String che contiene un codice di errore OpenSPCoop: Correlazione Applicativa Risposta non riuscita*/
	public static final String MSG_435_LOCAL_FORWARD_CONFIG_ERRORE = 
			"La funzionalità local-forward non è utilizzabile nella configurazione richiesta: ";



	/* ---- errori spediti in buste errore ---- */

	/** String che contiene un codice di errore OpenSPCoop2: PortaApplicativaInesistente in msg*/
	public static final String MSG_450_PA_INESISTENTE = 
			"La porta applicativa richiesta dalla busta non esiste";
	
	/** String che contiene un codice di errore OpenSPCoop2: SoggettoInesistente in msg*/
	public static final String MSG_451_SOGGETTO_INESISTENTE = 
			"Il soggetto richiesto dalla busta non è gestito dalla PdD";
	
	/** String che contiene un codice di errore OpenSPCoop2:BustaRicevutaPrecedentemente  in msg*/
	public static final String MSG_452_BUSTA_GIA_RICEVUTA = 
			"La busta è già stata ricevuta";
	
	/** String che contiene un codice di errore OpenSPCoop2: ServizioApplicativoInesistente in msg*/
	public static final String MSG_453_SA_INESISTENTE = 
			"Il servizio applicativo associato alla porta applicativa richiesta dalla busta non esiste";
	
	/** String che contiene un codice di errore OpenSPCoop2: Busta presente in una richiesta applicativa */
	public static final String MSG_454_BUSTA_PRESENTE_RISPOSTA_APPLICATIVA = 
			"Il messaggio Soap inviato al servizio di consegna contenuti applicativi presenta nell'header una busta";
	

}
