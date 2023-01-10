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
package org.openspcoop2.web.monitor.statistiche.constants;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.web.monitor.core.constants.Costanti;

/**
 * CostantiExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CostantiExporter {

	public final static int ERRORE_SERVER = 503;
	public final static int DATI_NON_CORRETTI = 404;
	public final static int DATI_NON_TROVATI = 404;
	public final static int CREDENZIALI_NON_FORNITE = 401;
	public final static int AUTENTICAZIONE_FALLITA = 403;
	
	public final static String USER = "user";
	public final static String PASSWORD = "password";
	
	public final static String TIPO_DISTRIBUZIONE = "distribuzione";
	public static final String TIPO_DISTRIBUZIONE_TEMPORALE = "temporale";
	public static final String TIPO_DISTRIBUZIONE_ESITI = "esiti";
	public static final String TIPO_DISTRIBUZIONE_ERRORI = "errori";
	public static final String TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO = "soggetto_remoto";
	public static final String TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE = "soggetto_locale";
	public static final String TIPO_DISTRIBUZIONE_SERVIZIO = "servizio";
	public static final String TIPO_DISTRIBUZIONE_AZIONE = "azione";
	public static final String TIPO_DISTRIBUZIONE_APPLICATIVO = "applicativo";
	public static final String TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO = "identificativo_autenticato";
	public static final String TIPO_DISTRIBUZIONE_INDIRIZZO_IP = "indirizzo_ip";
	public static final String TIPO_DISTRIBUZIONE_TOKEN_INFO = "token_info";
	public static final String TIPO_DISTRIBUZIONE_PERSONALIZZATA = "personalizzata";
	public static final List<String> TIPI_DISTRIBUZIONE = new ArrayList<String> ();
	static{
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_TEMPORALE);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_ESITI);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_ERRORI);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_SERVIZIO);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_AZIONE);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_APPLICATIVO);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_INDIRIZZO_IP);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_TOKEN_INFO);
		// TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_PERSONALIZZATA); TODO
	}
	
	public final static String TIPO_FORMATO = "formato";
	public final static String TIPO_FORMATO_CSV = "csv";
	public final static String TIPO_FORMATO_XLS = "xls";
	public final static String TIPO_FORMATO_PDF = "pdf";
	public final static String TIPO_FORMATO_XML = "xml";
	public final static String TIPO_FORMATO_JSON = "json";
	public static final List<String> TIPI_FORMATO = new ArrayList<String> ();
	static{
		TIPI_FORMATO.add(TIPO_FORMATO_CSV);
		TIPI_FORMATO.add(TIPO_FORMATO_XLS);
		TIPI_FORMATO.add(TIPO_FORMATO_PDF);
		TIPI_FORMATO.add(TIPO_FORMATO_XML);
		TIPI_FORMATO.add(TIPO_FORMATO_JSON);
	}
	
	public final static String DATA_INIZIO = "dataInizio";
	public final static String DATA_FINE = "dataFine";
	
	public final static String PROTOCOLLO  = "protocollo";
	public final static String MITTENTE  = "mittente";
	public final static String DESTINATARIO  = "destinatario";
	public final static String SOGGETTO_LOCALE  = "soggettoLocale";
	public final static String TRAFFICO_PER_SOGGETTO  = "trafficoPerSoggetto";
	
	public final static String GRUPPO  = "tag";
	
	public final static String API  = "api";
	public final static String API_DISTINGUI_IMPLEMENTAZIONE  = "api_distingui_implementazione";
	public final static String API_DISTINGUI_IMPLEMENTAZIONE_TRUE = "true";
	public final static String API_DISTINGUI_IMPLEMENTAZIONE_FALSE = "false";
	
	public final static String SERVIZIO  = "servizio";
	public final static String AZIONE  = "azione";
	
	public final static String TIPOLOGIA  = "tipologia";
	public final static String TIPOLOGIA_EROGAZIONE  = "Erogazione";
	public final static String TIPOLOGIA_FRUIZIONE  = "Fruizione";
	public final static String TIPOLOGIA_EROGAZIONE_FRUIZIONE  = "Erogazione/Fruizione";
	public static final List<String> TIPOLOGIE = new ArrayList<String> ();
	static{
		TIPOLOGIE.add(TIPOLOGIA_EROGAZIONE);
		TIPOLOGIE.add(TIPOLOGIA_FRUIZIONE);
		TIPOLOGIE.add(TIPOLOGIA_EROGAZIONE_FRUIZIONE);
	}
	
	public final static String ID_CLUSTER  = "idCluster";
	
	public final static String TIPO_RICERCA_MITTENTE  = "tipoRicercaMittente";
	public static final String TIPO_RICERCA_MITTENTE_TOKEN_INFO = Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO;
	public static final String TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO = Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO;
	public static final String TIPO_RICERCA_MITTENTE_INDIRIZZO_IP = Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP;
	public static final String TIPO_RICERCA_MITTENTE_APPLICATIVO = Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO;
	public static final String TIPO_RICERCA_MITTENTE_SOGGETTO = Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO;
	public static final List<String> TIPI_RICERCA_MITTENTE = new ArrayList<String> ();
	static{
		TIPI_RICERCA_MITTENTE.add(TIPO_RICERCA_MITTENTE_TOKEN_INFO);
		TIPI_RICERCA_MITTENTE.add(TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO);
		TIPI_RICERCA_MITTENTE.add(TIPO_RICERCA_MITTENTE_INDIRIZZO_IP);
		TIPI_RICERCA_MITTENTE.add(TIPO_RICERCA_MITTENTE_APPLICATIVO);
		TIPI_RICERCA_MITTENTE.add(TIPO_RICERCA_MITTENTE_SOGGETTO);
	}
	
	public final static String TIPO_IDENTIFICAZIONE_APPLICATIVO  = "tipo_identificazione_applicativo";
	public final static String TIPO_IDENTIFICAZIONE_APPLICATIVO_TRASPORTO = "trasporto";
	public final static String TIPO_IDENTIFICAZIONE_APPLICATIVO_TOKEN = "token";
	public static final List<String> TIPI_IDENTIFICAZIONE_APPLICATIVO = new ArrayList<String> ();
	static{
		TIPI_IDENTIFICAZIONE_APPLICATIVO.add(TIPO_IDENTIFICAZIONE_APPLICATIVO_TRASPORTO);
		TIPI_IDENTIFICAZIONE_APPLICATIVO.add(TIPO_IDENTIFICAZIONE_APPLICATIVO_TOKEN);
	}
	
	public final static String APPLICATIVO  = "applicativo";
	
	public final static String TIPO_AUTENTICAZIONE  = "tipoAutenticazioneRicercaMittente";
	public final static String TIPO_AUTENTICAZIONE_BASIC  = TipoAutenticazione.BASIC.getValue();
	public final static String TIPO_AUTENTICAZIONE_SSL  = TipoAutenticazione.SSL.getValue();
	public final static String TIPO_AUTENTICAZIONE_PRINCIPAL  = TipoAutenticazione.PRINCIPAL.getValue();
	public static final List<String> TIPI_AUTENTICAZIONE = new ArrayList<String> ();
	static{
		TIPI_AUTENTICAZIONE.add(TIPO_AUTENTICAZIONE_BASIC);
		TIPI_AUTENTICAZIONE.add(TIPO_AUTENTICAZIONE_SSL);
		TIPI_AUTENTICAZIONE.add(TIPO_AUTENTICAZIONE_PRINCIPAL);
	}
	
	public final static String TIPO_INDIRIZZO_IP  = "tipoIndirizzoIP";
	public final static String TIPO_INDIRIZZO_IP_SOCKET  = org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_SOCKET;
	public final static String TIPO_INDIRIZZO_IP_TRANSPORT  = org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_TRASPORTO;
	public static final List<String> TIPI_INDIRIZZI_IP = new ArrayList<String> ();
	static{
		TIPI_INDIRIZZI_IP.add(TIPO_INDIRIZZO_IP_SOCKET);
		TIPI_INDIRIZZI_IP.add(TIPO_INDIRIZZO_IP_TRANSPORT);
	}
	
	public final static String TIPO_RICERCA_MITTENTE_ESATTA  = "ricercaMittenteEsatta";
	public final static String TIPO_RICERCA_MITTENTE_ESATTA_TRUE  = "true";
	public final static String TIPO_RICERCA_MITTENTE_ESATTA_FALSE  = "false";
	
	public final static String TIPO_RICERCA_MITTENTE_CASE_SENSITIVE  = "ricercaMittenteCaseSensitive";
	public final static String TIPO_RICERCA_MITTENTE_CASE_SENSITIVE_TRUE  = "true";
	public final static String TIPO_RICERCA_MITTENTE_CASE_SENSITIVE_FALSE  = "false";
	
	public final static String IDENTIFICATIVO_RICERCA_MITTENTE  = "identificativoRicercaMittente";
	
	public final static String RICERCA_MITTENTE_TIPO_CLAIM  = "claimRicercaMittente";
		
	public final static String RICERCA_ALL  = "all";
	public final static String RICERCA_INGRESSO  = "ingresso";
	public final static String RICERCA_USCITA  = "uscita";
		
	public final static String ESITO_GRUPPO  = "esitoGruppo";
	public final static String ESITO_GRUPPO_OK  = "ok";
	public final static String ESITO_GRUPPO_FAULT_APPLICATIVO  = "fault";
	public final static String ESITO_GRUPPO_FALLITE  = "fallite";
	public final static String ESITO_GRUPPO_FALLITE_E_FAULT_APPLICATIVO  = "fallite_e_fault";
	public final static String ESITO_GRUPPO_ERRORI_CONSEGNA  = "errori_consegna";
	public final static String ESITO_GRUPPO_RICHIESTE_SCARTATE  = "richieste_scartate";

	public static final List<String> ESITI_GRUPPO = new ArrayList<String> ();
	static{
		ESITI_GRUPPO.add(ESITO_GRUPPO_OK);
		ESITI_GRUPPO.add(ESITO_GRUPPO_FAULT_APPLICATIVO);
		ESITI_GRUPPO.add(ESITO_GRUPPO_FALLITE);
		ESITI_GRUPPO.add(ESITO_GRUPPO_FALLITE_E_FAULT_APPLICATIVO);
		ESITI_GRUPPO.add(ESITO_GRUPPO_ERRORI_CONSEGNA);
		ESITI_GRUPPO.add(ESITO_GRUPPO_RICHIESTE_SCARTATE);
	}
	
	public final static String ESCLUDI_RICHIESTE_SCARTATE  = "escludiRichiesteScartate";
	public final static String ESCLUDI_RICHIESTE_SCARTATE_TRUE  = "true";
	public final static String ESCLUDI_RICHIESTE_SCARTATE_FALSE  = "false";
	
	public final static String ESITO  = "esito";
	
	public final static String ESITO_CONTESTO  = "contesto";
	
	
	public final static String TIPO_REPORT = "report";
	public final static TipoReport TIPO_REPORT_DISTRIBUZIONE_TEMPORALE_DEFAULT = TipoReport.LINE_CHART;
	public final static TipoReport TIPO_REPORT_DISTRIBUZIONE_OTHER_DEFAULT = TipoReport.BAR_CHART;
	
	
	public final static String TIPO_UNITA_TEMPORALE = "unitaTemporale";
	
	
	public final static String TIPO_INFORMAZIONE_VISUALIZZATA = "visualizza";
	public final static TipoVisualizzazione TIPO_INFORMAZIONE_VISUALIZZATA_DEFAULT = TipoVisualizzazione.NUMERO_TRANSAZIONI;
	public final static String TIPO_BANDA_VISUALIZZATA = "banda";
	public final static TipoBanda TIPO_BANDA_VISUALIZZATA_DEFAULT = TipoBanda.COMPLESSIVA;
	public final static String TIPO_LATENZA_VISUALIZZATA = "latenza";
	public final static TipoLatenza TIPO_LATENZA_VISUALIZZATA_DEFAULT = TipoLatenza.LATENZA_TOTALE;
	
	public final static String CLAIM = "claim";
	public final static String CLAIM_SUBJECT = "subject";
	public final static String CLAIM_ISSUER = "issuer";
	public final static String CLAIM_USERNAME = "username";
	public final static String CLAIM_EMAIL = "eMail";
	public final static String CLAIM_CLIENT_ID = "clientId";
	public static final List<String> CLAIMS = new ArrayList<String> ();
	static{
		CLAIMS.add(CLAIM_SUBJECT);
		CLAIMS.add(CLAIM_ISSUER);
		CLAIMS.add(CLAIM_USERNAME);
		CLAIMS.add(CLAIM_EMAIL);
		CLAIMS.add(CLAIM_CLIENT_ID);
	}
	
	
	
	
}
