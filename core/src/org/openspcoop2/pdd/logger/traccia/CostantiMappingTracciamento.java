/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.logger.traccia;

import org.openspcoop2.pdd.logger.record.CostantiDati;
import org.openspcoop2.pdd.logger.record.InfoDato;

/**     
 * CostantiMappingTracciamento
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiMappingTracciamento {
	
	private CostantiMappingTracciamento() {}

	public static final char NON_PRESENTE = CostantiDati.NON_PRESENTE.charAt(0);
	
	public static final String SEPARATOR = CostantiDati.SEPARATOR;
	
	public static final char TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP_LOCALE = 'L';
	public static final char TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP_SINCRONIZZATO = 'S';
	public static final char TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP_SCONOSCIUTO = 'U';
	
	
	/**
	 * Posizione 1 (1 carattere):
  		- (-) Non presente
  		- (N) indica che la transazione ha emesso una traccia non ricostruibile
  		- (R) indica che la transazione ha emesso una traccia ricostruibile
	 **/
	public static final InfoDato TRACCIA_EMESSA = new InfoDato(0, 
			"Indicazione se la transazione ha emesso una traccia ricostruibile (R), non ricostruibile (N) o non ha emesso alcuna traccia (-)");
	public static final char TRACCIA_EMESSA_RICOSTRUIBILE = 'R';
	public static final char TRACCIA_EMESSA_NON_RICOSTRUIBILE = 'N';
	
	public static final InfoDato TRACCIA_EMESSA_NON_RICOSTRUIBILE_MOTIVO = new InfoDato(1, 
			"Motivo che indica come mai la traccia non e' ricostruibile");
	
	/**
	 * 	Posizione 2 (17 caratteri o '-')
	  - Indica la data di registrazione della traccia (gdo) nel formato AAAAMMDDHHMMSSsss
	 */
	public static final InfoDato TRACCIA_DATA_REGISTRAZIONE = new InfoDato(1, 
			"Indica la data di registrazione della traccia (gdo) nel formato AAAAMMDDHHMMSSsss");
	
	/**
	 * Posizione 3 (17 caratteri o '-')
	  - Indica l'ora di registrazione presente nel protocollo nel formato AAAAMMDDHHMMSSsss
	 */
	public static final InfoDato TRACCIA_BUSTA_ORA_REGISTRAZIONE = new InfoDato(2, 
			"Indica l'ora di registrazione della busta nel formato AAAAMMDDHHMMSSsss");
	
	/**
	 * 	Posizione 4 (max 20 caratteri o '-')
	  - Indica il tipo di ora registrazione presente nel protocollo
	 */
	public static final InfoDato TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_PROTOCOLLO = new InfoDato(3, 
			"Indica il tipo di ora registrazione della busta generato dal protocollo");
	
	/**
	 * 	Posizione 5 (1 carattere)
	  - (-) Non presente
	  - (L) Indica il tipo di ora registrazione (codice openspcoop2) 'Locale'
	  - (S) Indica il tipo di ora registrazione (codice openspcoop2) 'Sincronizzato'
	  - (U) Indica il tipo di ora registrazione (codice openspcoop2) 'Sconosciuto'
	 */
	public static final InfoDato TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP = new InfoDato(4, 
			"Indica il tipo di ora registrazione della busta tramite la codifica openspcoop Locale (L), Sincronizzato(S) o Sconosciuto(U)");
	
	/**
	* Posizione 6 (1 carattere)
	  - (-) indica che la traccia non contenenva valorizzato il riferimento messaggio
	  - (1) la traccia conteneva valorizzata come rifMessaggio il campo id-messaggio-richiesta
	  - (2) la traccia conteneva valorizzata come rifMessaggio il campo id-messaggio-risposta
	  - (3) la traccia conteneva valorizzata come rifMessaggio il campo id-asincrono
	  */
	public static final InfoDato TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO = new InfoDato(5, 
			"Indica come era valorizzato il riferimento messaggio della busta: non valorizzato (-), idMessaggioRichiesta (1), idMessaggioRisposta (2), idAsincrono (3)");

	public static final char TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO_ID_MESSAGGIO_RICHIESTA = '1'; 
	public static final char TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO_ID_MESSAGGIO_RISPOSTA = '2'; 
	public static final char TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO_ID_ASICRONO = '3'; 
	
	/**
	Posizione 7 (17 caratteri o '-')
	  - Indica l'ora di scadenza presente nel protocollo nel formato AAAAMMDDHHMMSSsss
	*/
	public static final InfoDato TRACCIA_BUSTA_SCADENZA = new InfoDato(6, 
			"Indica l'ora di scadenza presente nel protocollo nel formato AAAAMMDDHHMMSSsss");

	/**
	Posizione 8 (max 20 caratteri o '-')
	  - Indica il tipo di filtro duplicat
	  */
	public static final InfoDato TRACCIA_FILTRO_DUPLICATI = new InfoDato(7, 
			"Indica il tipo di filtro duplicati");

	/**
	 * Posizione 9 (1 carattere)
	  - (-) Non presente
	  - (D) Indica il tipo di filtro duplicati (codice openspcoop2) 'PIUDIUNAVOLTA'
	  - (S) Indica il tipo di filtro duplicati (codice openspcoop2) 'ALPIUUNAVOLTA' 
	  - (U) Indica il tipo di filtro duplicati (codice openspcoop2) 'Sconosciuto'
	 */
	public static final InfoDato TRACCIA_FILTRO_DUPLICATI_CODE = new InfoDato(8, 
			"Indica il tipo di filtro duplicati (codice openspcoop2): Non presente (-), 'PIUDIUNAVOLTA' (D), 'ALPIUUNAVOLTA' (S), 'Sconosciuto' (U),");
	
	public static final char TRACCIA_FILTRO_DUPLICATI_CODE_PIUDIUNAVOLTA = 'D';
	public static final char TRACCIA_FILTRO_DUPLICATI_CODE_ALPIUUNAVOLTA = 'S';
	public static final char TRACCIA_FILTRO_DUPLICATI_CODE_SCONOSCIUTO = 'U';

	/**
	 Posizione 10 (max 10 caratteri o '-')
	  - Indica la sequenza
	*/
	public static final InfoDato TRACCIA_BUSTA_SEQUENZA = new InfoDato(9, 
			"Indica la sequenza");

	/**
	 * Posizione 11 (17 caratteri o '-')
	  - Indica l'ora di registrazione presente nel riscontro (traccia di risposta) nel formato AAAAMMDDHHMMSSsss
	*/
	public static final InfoDato TRACCIA_BUSTA_RISCONTRO_ORA_REGISTRAZIONE = new InfoDato(10, 
			"Indica l'ora di registrazione presente nel riscontro (traccia di risposta) nel formato AAAAMMDDHHMMSSsss");

	/**
	 * Posizione 12 (max 20 caratteri o '-')
	  - Indica il tipo di ora registrazione presente nel riscontro
	*/
	public static final InfoDato TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE =  new InfoDato(11, 
			"Indica il tipo di ora registrazione presente nel riscontro");

	/**
	 * Posizione 13 (1 carattere)
	  - (-) Non presente
	  - (L) Indica il tipo di ora registrazione presente nel riscontro (codice openspcoop2) 'Locale'
	  - (S) Indica il tipo di ora registrazione presente nel riscontro (codice openspcoop2) 'Sincronizzato'
	  - (U) Indica il tipo di ora registrazione presente nel riscontro (codice openspcoop2) 'Sconosciuto'
	*/
	public static final InfoDato TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE =  new InfoDato(12, 
			"Indica il tipo di ora registrazione presente nel riscontro (codice openspcoop2): non presente (-), 'Locale' (L), 'Sincronizzato' (S), 'Sconosciuto' (U)");

	public static final char TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE_LOCALE = 'L';
	public static final char TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE_SINCRONIZZATO = 'S';
	public static final char TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE_SCONOSCIUTO = 'U';

	/**
	 * Posizione 14 (17 caratteri o '-')
	  - Indica l'ora di registrazione presente nella prima trasmissione (traccia di risposta) nel formato AAAAMMDDHHMMSSsss
	*/
	public static final InfoDato TRACCIA_BUSTA_PRIMA_TRASMISSIONE_ORA_REGISTRAZIONE = new InfoDato(13, 
			"Indica l'ora di registrazione presente nella prima trasmissione (traccia di risposta) nel formato AAAAMMDDHHMMSSsss");

	/**
	 * Posizione 15 (max 20 caratteri o '-')
	  - Indica il tipo di ora registrazione presente nella prima trasmissione
	*/
	public static final InfoDato TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE = new InfoDato(14, 
			"Indica il tipo di ora registrazione presente nella prima trasmissione");

	/**
	 * Posizione 16 (1 carattere)
	 * Questo carattere viene utilizzato anche per comprendere se la trasmissione è 
	 * - 'normale' (mittente e destinatario allineato con quelli della busta)
	 * - 'invertita' (mittente e destinatario invertiti rispetto a quelli della busta ed è presente solamente 1 trasmissione nella traccia)
	  - (-) Non presente
	  - (L) Indica traccia normale e il tipo di ora registrazione presente nella prima trasmissione (codice openspcoop2) 'Locale'
	  - (S) Indica traccia normale e il tipo di ora registrazione presente nella prima trasmissione (codice openspcoop2) 'Sincronizzato'
	  - (U) Indica traccia normale e il tipo di ora registrazione presente nella prima trasmissione (codice openspcoop2) 'Sconosciuto'
	  - (l) Indica traccia invertita e il tipo di ora registrazione presente nella prima trasmissione (codice openspcoop2) 'Locale'
	  - (s) Indica traccia invertita eil tipo di ora registrazione presente nella prima trasmissione (codice openspcoop2) 'Sincronizzato'
	  - (u) Indica traccia invertita eil tipo di ora registrazione presente nella prima trasmissione (codice openspcoop2) 'Sconosciuto'
	*/
	public static final InfoDato TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE = new InfoDato(15, 
			"Indica il tipo di ora registrazione presente nella prima trasmissione (codice openspcoop2): non presente (-), 'Locale' (L), 'Sincronizzato' (S), 'Sconosciuto' (U)");

	public static final char TRACCIA_BUSTA_PRIMA_TRASMISSIONE_NORMALE_TIPO_ORA_REGISTRAZIONE_CODE_LOCALE = 'L';
	public static final char TRACCIA_BUSTA_PRIMA_TRASMISSIONE_NORMALE_TIPO_ORA_REGISTRAZIONE_CODE_SINCRONIZZATO = 'S';
	public static final char TRACCIA_BUSTA_PRIMA_TRASMISSIONE_NORMALE_TIPO_ORA_REGISTRAZIONE_CODE_SCONOSCIUTO = 'U';
	public static final char TRACCIA_BUSTA_PRIMA_TRASMISSIONE_INVERTITA_TIPO_ORA_REGISTRAZIONE_CODE_LOCALE = 'l';
	public static final char TRACCIA_BUSTA_PRIMA_TRASMISSIONE_INVERTITA_TIPO_ORA_REGISTRAZIONE_CODE_SINCRONIZZATO = 's';
	public static final char TRACCIA_BUSTA_PRIMA_TRASMISSIONE_INVERTITA_TIPO_ORA_REGISTRAZIONE_CODE_SCONOSCIUTO = 'u';

	/**
	 * Posizione 17 (17 caratteri o '-')
	  - Indica l'ora di registrazione presente nella seconda trasmissione (traccia di risposta) nel formato AAAAMMDDHHMMSSsss
	*/
	public static final InfoDato TRACCIA_BUSTA_SECONDA_TRASMISSIONE_ORA_REGISTRAZIONE = new InfoDato(16, 
			"Indica l'ora di registrazione presente nella seconda trasmissione (traccia di risposta) nel formato AAAAMMDDHHMMSSsss");

	/**
	 * Posizione 18 (max 20 caratteri o '-')
	  - Indica il tipo di ora registrazione presente nella seconda trasmissione
	*/
	public static final InfoDato TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE = new InfoDato(17, 
			"Indica il tipo di ora registrazione presente nella seconda trasmissione");

	/**
	 * Posizione 19 (1 carattere)
	  - (-) Non presente
	  - (L) Indica il tipo di ora registrazione presente nella seconda trasmissione (codice openspcoop2) 'Locale'
	  - (S) Indica il tipo di ora registrazione presente nella seconda trasmissione (codice openspcoop2) 'Sincronizzato'
	  - (U) Indica il tipo di ora registrazione presente nella seconda trasmissione (codice openspcoop2) 'Sconosciuto'
	*/
	public static final InfoDato TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE = new InfoDato(18, 
			"Indica il tipo di ora registrazione presente nella seconda trasmissione (codice openspcoop2): non presente (-), 'Locale' (L), 'Sincronizzato' (S), 'Sconosciuto' (U)");

	public static final char TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE_LOCALE = 'L';
	public static final char TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE_SINCRONIZZATO = 'S';
	public static final char TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE_SCONOSCIUTO = 'U';

	/**
	 * Posizione 20 (1 carattere) 
	  - (-) Non presente 
	  - (T) Indica che la confermaRicezione e' richiesta (true) nel Profilo di Trasmissione
	  - (F) Indica che la confermaRicezione non e' richiesta (false) nel Profilo di Trasmissione
	*/
	public static final InfoDato TRACCIA_BUSTA_CONFERMA_RICHIESTA = new InfoDato(19, 
			"Indica il tipo di conferma ricezione presente nel Profilo di Trasmissione: non presente (-), richiesta (T), non richiesta (F)");

	public static final char TRACCIA_BUSTA_CONFERMA_RICHIESTA_TRUE = 'T';
	public static final char TRACCIA_BUSTA_CONFERMA_RICHIESTA_FALSE = 'F';

	/**
	 * Posizione 21 (1 carattere) 
	  - (-) Non presente 
	  - (T) Indica che la busta all'interno della traccia presenta i valori tipoServizioCorrelato e servizioCorrelato poi salvati nella transazione
	  - (F) Indica che la busta all'interno della traccia non presenta i valori tipoServizioCorrelato e servizioCorrelato
	*/
	public static final InfoDato TRACCIA_BUSTA_SERVIZIO_CORRELATO_PRESENTE = new InfoDato(20, 
			"Indica che la busta all'interno della traccia presenta (T) o non presenta (F) i valori tipoServizioCorrelato e servizioCorrelato poi salvati nella transazione");

	public static final char TRACCIA_BUSTA_SERVIZIO_CORRELATO_PRESENTE_TRUE = 'T';
	public static final char TRACCIA_BUSTA_SERVIZIO_CORRELATO_PRESENTE_FALSE = 'F';
	
	/**
	 * Posizione 22 (1 carattere)
		- (I) Indica un esito traccia Inviata
		- (R) Indica un esito traccia Ricevuta
		- (E) Indica un esito traccia Errore
	 */
	public static final InfoDato TRACCIA_BUSTA_ESITO_TRACCIA = new InfoDato(21, 
			"Indica il tipo di esito della traccia: Inviata (I), Ricevuta (R) o Errore (E)");
	public static final char TRACCIA_BUSTA_ESITO_TRACCIA_INVIATA = 'I';
	public static final char TRACCIA_BUSTA_ESITO_TRACCIA_RICEVUTA = 'R';
	public static final char TRACCIA_BUSTA_ESITO_TRACCIA_ERRORE = 'E';
	
	/**
	 * Posizione 23 (1 carattere) 
	  - (-) Non presente 
	  - (T) Indica che il mittente della busta di richiesta o il destinatario della busta di risposta è il soggetto proprietario del token
	  - (F) Indica che il mittente della busta di richiesta o il destinatario della busta di rispostanon è il soggetto proprietario del token
	*/
	public static final InfoDato TRACCIA_BUSTA_SOGGETTO_APPLICATIVO_TOKEN = new InfoDato(22, 
			"Indica che il mittente della busta di richiesta o il destinatario della busta di risposta è il soggetto proprietario del token: non presente (-), richiesta (T), non richiesta (F)");

	public static final char TRACCIA_BUSTA_SOGGETTO_APPLICATIVO_TOKEN_TRUE = 'T';
	public static final char TRACCIA_BUSTA_SOGGETTO_APPLICATIVO_TOKEN_FALSE = 'F';
	

	public static final int LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE_PRECEDENTE_INTRODOTTO_22 = 22;
	public static final int LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE = 23; // incrementare ogni volta si aggiunge una nuova info
}
