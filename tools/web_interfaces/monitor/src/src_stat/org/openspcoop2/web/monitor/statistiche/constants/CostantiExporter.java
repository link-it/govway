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

	public static final int ERRORE_SERVER = 503;
	public static final int DATI_NON_CORRETTI = 404;
	public static final int DATI_NON_TROVATI = 404;
	public static final int CREDENZIALI_NON_FORNITE = 401;
	public static final int AUTENTICAZIONE_FALLITA = 403;
	
	public static final String USER = "user";
	public static final String PASSWORD = "password";
	
	public static final String TIPO_DISTRIBUZIONE = "distribuzione";
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
	public static final List<String> TIPI_DISTRIBUZIONE = new ArrayList<> ();
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
	
	public static final String TIPO_FORMATO = "formato";
	public static final String TIPO_FORMATO_CSV = "csv";
	public static final String TIPO_FORMATO_XLS = "xls";
	public static final String TIPO_FORMATO_PDF = "pdf";
	public static final String TIPO_FORMATO_XML = "xml";
	public static final String TIPO_FORMATO_JSON = "json";
	public static final List<String> TIPI_FORMATO = new ArrayList<> ();
	static{
		TIPI_FORMATO.add(TIPO_FORMATO_CSV);
		TIPI_FORMATO.add(TIPO_FORMATO_XLS);
		TIPI_FORMATO.add(TIPO_FORMATO_PDF);
		TIPI_FORMATO.add(TIPO_FORMATO_XML);
		TIPI_FORMATO.add(TIPO_FORMATO_JSON);
	}
	
	public static final String DATA_INIZIO = "dataInizio";
	public static final String DATA_FINE = "dataFine";
	
	public static final String PROTOCOLLO  = "protocollo";
	public static final String MITTENTE  = "mittente";
	public static final String DESTINATARIO  = "destinatario";
	public static final String SOGGETTO_LOCALE  = "soggettoLocale";
	public static final String TRAFFICO_PER_SOGGETTO  = "trafficoPerSoggetto";
	
	public static final String GRUPPO  = "tag";
	
	public static final String API  = "api";
	public static final String API_DISTINGUI_IMPLEMENTAZIONE  = "api_distingui_implementazione";
	public static final String API_DISTINGUI_IMPLEMENTAZIONE_TRUE = "true";
	public static final String API_DISTINGUI_IMPLEMENTAZIONE_FALSE = "false";
	
	public static final String SERVIZIO  = "servizio";
	public static final String AZIONE  = "azione";
	
	public static final String TIPOLOGIA  = "tipologia";
	public static final String TIPOLOGIA_EROGAZIONE  = "Erogazione";
	public static final String TIPOLOGIA_FRUIZIONE  = "Fruizione";
	public static final String TIPOLOGIA_EROGAZIONE_FRUIZIONE  = "Erogazione/Fruizione";
	public static final List<String> TIPOLOGIE = new ArrayList<> ();
	static{
		TIPOLOGIE.add(TIPOLOGIA_EROGAZIONE);
		TIPOLOGIE.add(TIPOLOGIA_FRUIZIONE);
		TIPOLOGIE.add(TIPOLOGIA_EROGAZIONE_FRUIZIONE);
	}
	
	public static final String ID_CLUSTER  = "idCluster";
	
	public static final String TIPO_RICERCA_MITTENTE  = "tipoRicercaMittente";
	public static final String TIPO_RICERCA_MITTENTE_TOKEN_INFO = Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO;
	public static final String TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO = Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO;
	public static final String TIPO_RICERCA_MITTENTE_INDIRIZZO_IP = Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP;
	public static final String TIPO_RICERCA_MITTENTE_APPLICATIVO = Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO;
	public static final String TIPO_RICERCA_MITTENTE_SOGGETTO = Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO;
	public static final List<String> TIPI_RICERCA_MITTENTE = new ArrayList<> ();
	static{
		TIPI_RICERCA_MITTENTE.add(TIPO_RICERCA_MITTENTE_TOKEN_INFO);
		TIPI_RICERCA_MITTENTE.add(TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO);
		TIPI_RICERCA_MITTENTE.add(TIPO_RICERCA_MITTENTE_INDIRIZZO_IP);
		TIPI_RICERCA_MITTENTE.add(TIPO_RICERCA_MITTENTE_APPLICATIVO);
		TIPI_RICERCA_MITTENTE.add(TIPO_RICERCA_MITTENTE_SOGGETTO);
	}
	
	public static final String TIPO_IDENTIFICAZIONE_APPLICATIVO  = "tipo_identificazione_applicativo";
	public static final String TIPO_IDENTIFICAZIONE_APPLICATIVO_TRASPORTO = "trasporto";
	public static final String TIPO_IDENTIFICAZIONE_APPLICATIVO_TOKEN = "token";
	public static final List<String> TIPI_IDENTIFICAZIONE_APPLICATIVO = new ArrayList<> ();
	static{
		TIPI_IDENTIFICAZIONE_APPLICATIVO.add(TIPO_IDENTIFICAZIONE_APPLICATIVO_TRASPORTO);
		TIPI_IDENTIFICAZIONE_APPLICATIVO.add(TIPO_IDENTIFICAZIONE_APPLICATIVO_TOKEN);
	}
	
	public static final String APPLICATIVO  = "applicativo";
	
	public static final String TIPO_AUTENTICAZIONE  = "tipoAutenticazioneRicercaMittente";
	public static final String TIPO_AUTENTICAZIONE_BASIC  = TipoAutenticazione.BASIC.getValue();
	public static final String TIPO_AUTENTICAZIONE_SSL  = TipoAutenticazione.SSL.getValue();
	public static final String TIPO_AUTENTICAZIONE_PRINCIPAL  = TipoAutenticazione.PRINCIPAL.getValue();
	public static final List<String> TIPI_AUTENTICAZIONE = new ArrayList<> ();
	static{
		TIPI_AUTENTICAZIONE.add(TIPO_AUTENTICAZIONE_BASIC);
		TIPI_AUTENTICAZIONE.add(TIPO_AUTENTICAZIONE_SSL);
		TIPI_AUTENTICAZIONE.add(TIPO_AUTENTICAZIONE_PRINCIPAL);
	}
	
	public static final String TIPO_INDIRIZZO_IP  = "tipoIndirizzoIP";
	public static final String TIPO_INDIRIZZO_IP_SOCKET  = org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_SOCKET;
	public static final String TIPO_INDIRIZZO_IP_TRANSPORT  = org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_TRASPORTO;
	public static final List<String> TIPI_INDIRIZZI_IP = new ArrayList<> ();
	static{
		TIPI_INDIRIZZI_IP.add(TIPO_INDIRIZZO_IP_SOCKET);
		TIPI_INDIRIZZI_IP.add(TIPO_INDIRIZZO_IP_TRANSPORT);
	}
	
	public static final String TIPO_RICERCA_MITTENTE_ESATTA  = "ricercaMittenteEsatta";
	public static final String TIPO_RICERCA_MITTENTE_ESATTA_TRUE  = "true";
	public static final String TIPO_RICERCA_MITTENTE_ESATTA_FALSE  = "false";
	
	public static final String TIPO_RICERCA_MITTENTE_CASE_SENSITIVE  = "ricercaMittenteCaseSensitive";
	public static final String TIPO_RICERCA_MITTENTE_CASE_SENSITIVE_TRUE  = "true";
	public static final String TIPO_RICERCA_MITTENTE_CASE_SENSITIVE_FALSE  = "false";
	
	public static final String IDENTIFICATIVO_RICERCA_MITTENTE  = "identificativoRicercaMittente";
	
	public static final String RICERCA_MITTENTE_TIPO_CLAIM  = "claimRicercaMittente";
		
	public static final String RICERCA_ALL  = "all";
	public static final String RICERCA_INGRESSO  = "ingresso";
	public static final String RICERCA_USCITA  = "uscita";
		
	public static final String ESITO_GRUPPO  = "esitoGruppo";
	public static final String ESITO_GRUPPO_OK  = "ok";
	public static final String ESITO_GRUPPO_FAULT_APPLICATIVO  = "fault";
	public static final String ESITO_GRUPPO_FALLITE  = "fallite";
	public static final String ESITO_GRUPPO_FALLITE_E_FAULT_APPLICATIVO  = "fallite_e_fault";
	public static final String ESITO_GRUPPO_ERRORI_CONSEGNA  = "errori_consegna";
	public static final String ESITO_GRUPPO_RICHIESTE_SCARTATE  = "richieste_scartate";

	public static final List<String> ESITI_GRUPPO = new ArrayList<> ();
	static{
		ESITI_GRUPPO.add(ESITO_GRUPPO_OK);
		ESITI_GRUPPO.add(ESITO_GRUPPO_FAULT_APPLICATIVO);
		ESITI_GRUPPO.add(ESITO_GRUPPO_FALLITE);
		ESITI_GRUPPO.add(ESITO_GRUPPO_FALLITE_E_FAULT_APPLICATIVO);
		ESITI_GRUPPO.add(ESITO_GRUPPO_ERRORI_CONSEGNA);
		ESITI_GRUPPO.add(ESITO_GRUPPO_RICHIESTE_SCARTATE);
	}
	
	public static final String ESCLUDI_RICHIESTE_SCARTATE  = "escludiRichiesteScartate";
	public static final String ESCLUDI_RICHIESTE_SCARTATE_TRUE  = "true";
	public static final String ESCLUDI_RICHIESTE_SCARTATE_FALSE  = "false";
	
	public static final String ESITO  = "esito";
	
	public static final String ESITO_CONTESTO  = "contesto";
	
	
	public static final String TIPO_REPORT = "report";
	public static final TipoReport TIPO_REPORT_DISTRIBUZIONE_TEMPORALE_DEFAULT = TipoReport.LINE_CHART;
	public static final TipoReport TIPO_REPORT_DISTRIBUZIONE_OTHER_DEFAULT = TipoReport.BAR_CHART;
	
	
	public static final String TIPO_UNITA_TEMPORALE = "unitaTemporale";
	
	
	public static final String TIPO_INFORMAZIONE_VISUALIZZATA = "visualizza";
	public static final TipoVisualizzazione TIPO_INFORMAZIONE_VISUALIZZATA_DEFAULT = TipoVisualizzazione.NUMERO_TRANSAZIONI;
	public static final String TIPO_BANDA_VISUALIZZATA = "banda";
	public static final TipoBanda TIPO_BANDA_VISUALIZZATA_DEFAULT = TipoBanda.COMPLESSIVA;
	public static final String TIPO_LATENZA_VISUALIZZATA = "latenza";
	public static final TipoLatenza TIPO_LATENZA_VISUALIZZATA_DEFAULT = TipoLatenza.LATENZA_TOTALE;
	
	public static final String CLAIM = "claim";
	public static final String CLAIM_SUBJECT = "subject";
	public static final String CLAIM_ISSUER = "issuer";
	public static final String CLAIM_USERNAME = "username";
	public static final String CLAIM_EMAIL = "eMail";
	public static final String CLAIM_CLIENT_ID = "clientId";
	public static final List<String> CLAIMS = new ArrayList<> ();
	static{
		CLAIMS.add(CLAIM_SUBJECT);
		CLAIMS.add(CLAIM_ISSUER);
		CLAIMS.add(CLAIM_USERNAME);
		CLAIMS.add(CLAIM_EMAIL);
		CLAIMS.add(CLAIM_CLIENT_ID);
	}
	
	public static final String FORMATO_XLS_VALUE = "XLS";
	public static final String FORMATO_CSV_VALUE = "CSV";
	public static final String PARAMETER_FORMATO_EXPORT = "fEx";
	public static final String PARAMETER_FORMATO_EXPORT_ORIGINALE = "fExOriginali";
}
