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
package org.openspcoop2.web.monitor.statistiche.constants;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.statistiche.bean.NumeroDimensioni;

/**
 * CostantiExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CostantiExporter {
	
	private CostantiExporter() {}

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
	private static final List<String> tipiDistribuzione = new ArrayList<> ();
	public static List<String> getTipiDistribuzione() {
		return tipiDistribuzione;
	}
	static{
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_TEMPORALE);
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_ESITI);
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_ERRORI);
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO);
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE);
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_SERVIZIO);
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_AZIONE);
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_APPLICATIVO);
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO);
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_INDIRIZZO_IP);
		tipiDistribuzione.add(TIPO_DISTRIBUZIONE_TOKEN_INFO);
	}
	
	public static final String TIPO_FORMATO = "formato";
	public static final String TIPO_FORMATO_CSV = "csv";
	public static final String TIPO_FORMATO_XLS = "xls";
	public static final String TIPO_FORMATO_PDF = "pdf";
	public static final String TIPO_FORMATO_XML = "xml";
	public static final String TIPO_FORMATO_JSON = "json";
	private static final List<String> tipiFormato = new ArrayList<> ();
	public static List<String> getTipiFormato() {
		return tipiFormato;
	}
	static{
		tipiFormato.add(TIPO_FORMATO_CSV);
		tipiFormato.add(TIPO_FORMATO_XLS);
		tipiFormato.add(TIPO_FORMATO_PDF);
		tipiFormato.add(TIPO_FORMATO_XML);
		tipiFormato.add(TIPO_FORMATO_JSON);
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
	private static final List<String> tipologie = new ArrayList<> ();
	public static List<String> getTipologie() {
		return tipologie;
	}
	static{
		tipologie.add(TIPOLOGIA_EROGAZIONE);
		tipologie.add(TIPOLOGIA_FRUIZIONE);
		tipologie.add(TIPOLOGIA_EROGAZIONE_FRUIZIONE);
	}
	
	public static final String ID_CLUSTER  = "idCluster";
	
	public static final String TIPO_RICERCA_MITTENTE  = "tipoRicercaMittente";
	public static final String TIPO_RICERCA_MITTENTE_TOKEN_INFO = Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO;
	public static final String TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO = Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO;
	public static final String TIPO_RICERCA_MITTENTE_INDIRIZZO_IP = Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP;
	public static final String TIPO_RICERCA_MITTENTE_APPLICATIVO = Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO;
	public static final String TIPO_RICERCA_MITTENTE_SOGGETTO = Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO;
	private static final List<String> tipiRicercaMittente = new ArrayList<> ();
	public static List<String> getTipiRicercaMittente() {
		return tipiRicercaMittente;
	}
	static{
		tipiRicercaMittente.add(TIPO_RICERCA_MITTENTE_TOKEN_INFO);
		tipiRicercaMittente.add(TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO);
		tipiRicercaMittente.add(TIPO_RICERCA_MITTENTE_INDIRIZZO_IP);
		tipiRicercaMittente.add(TIPO_RICERCA_MITTENTE_APPLICATIVO);
		tipiRicercaMittente.add(TIPO_RICERCA_MITTENTE_SOGGETTO);
	}
	
	public static final String TIPO_IDENTIFICAZIONE_APPLICATIVO  = "tipo_identificazione_applicativo";
	public static final String TIPO_IDENTIFICAZIONE_APPLICATIVO_TRASPORTO = "trasporto";
	public static final String TIPO_IDENTIFICAZIONE_APPLICATIVO_TOKEN = "token";
	private static final List<String> tipiIdentificazioneApplicativo = new ArrayList<> ();
	public static List<String> getTipiIdentificazioneApplicativo() {
		return tipiIdentificazioneApplicativo;
	}
	static{
		tipiIdentificazioneApplicativo.add(TIPO_IDENTIFICAZIONE_APPLICATIVO_TRASPORTO);
		tipiIdentificazioneApplicativo.add(TIPO_IDENTIFICAZIONE_APPLICATIVO_TOKEN);
	}
	
	public static final String APPLICATIVO  = "applicativo";
	
	public static final String TIPO_AUTENTICAZIONE  = "tipoAutenticazioneRicercaMittente";
	public static final String TIPO_AUTENTICAZIONE_BASIC  = TipoAutenticazione.BASIC.getValue();
	public static final String TIPO_AUTENTICAZIONE_SSL  = TipoAutenticazione.SSL.getValue();
	public static final String TIPO_AUTENTICAZIONE_PRINCIPAL  = TipoAutenticazione.PRINCIPAL.getValue();
	private static final List<String> tipiAutenticazione = new ArrayList<> ();
	public static List<String> getTipiAutenticazione() {
		return tipiAutenticazione;
	}
	static{
		tipiAutenticazione.add(TIPO_AUTENTICAZIONE_BASIC);
		tipiAutenticazione.add(TIPO_AUTENTICAZIONE_SSL);
		tipiAutenticazione.add(TIPO_AUTENTICAZIONE_PRINCIPAL);
	}
	
	public static final String TIPO_INDIRIZZO_IP  = "tipoIndirizzoIP";
	public static final String TIPO_INDIRIZZO_IP_SOCKET  = org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_SOCKET;
	public static final String TIPO_INDIRIZZO_IP_TRANSPORT  = org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_TRASPORTO;
	private static final List<String> tipiIndirizzoIp = new ArrayList<> ();
	public static List<String> getTipiIndirizzoIp() {
		return tipiIndirizzoIp;
	}
	static{
		tipiIndirizzoIp.add(TIPO_INDIRIZZO_IP_SOCKET);
		tipiIndirizzoIp.add(TIPO_INDIRIZZO_IP_TRANSPORT);
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

	private static final List<String> esitiGruppo = new ArrayList<> ();
	public static List<String> getEsitiGruppo() {
		return esitiGruppo;
	}
	static{
		esitiGruppo.add(ESITO_GRUPPO_OK);
		esitiGruppo.add(ESITO_GRUPPO_FAULT_APPLICATIVO);
		esitiGruppo.add(ESITO_GRUPPO_FALLITE);
		esitiGruppo.add(ESITO_GRUPPO_FALLITE_E_FAULT_APPLICATIVO);
		esitiGruppo.add(ESITO_GRUPPO_ERRORI_CONSEGNA);
		esitiGruppo.add(ESITO_GRUPPO_RICHIESTE_SCARTATE);
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
	
	public static final String DIMENSIONI_VISUALIZZATE = "dimensioni";
	public static final NumeroDimensioni DIMENSIONI_VISUALIZZATE_DEFAULT = NumeroDimensioni.DIMENSIONI_2;
	public static final String DIMENSIONI_VISUALIZZATE_2 = NumeroDimensioni.DIMENSIONI_2.getValue();
	public static final String DIMENSIONI_VISUALIZZATE_3 = NumeroDimensioni.DIMENSIONI_3.getValue();
	public static final String DIMENSIONI_VISUALIZZATE_3_CUSTOM = NumeroDimensioni.DIMENSIONI_3_CUSTOM.getValue();
	
	public static final String CLAIM = "claim";
	public static final String CLAIM_SUBJECT = "subject";
	public static final String CLAIM_ISSUER = "issuer";
	public static final String CLAIM_USERNAME = "username";
	public static final String CLAIM_EMAIL = "eMail";
	public static final String CLAIM_CLIENT_ID = "clientId";
	public static final String CLAIM_PDND_ORGANIZATION_NAME = "pdndOrganizationName";
	private static final List<String> claims = new ArrayList<> ();
	public static List<String> getClaims() {
		return claims;
	}
	static{
		claims.add(CLAIM_SUBJECT);
		claims.add(CLAIM_ISSUER);
		claims.add(CLAIM_USERNAME);
		claims.add(CLAIM_EMAIL);
		claims.add(CLAIM_CLIENT_ID);
		claims.add(CLAIM_PDND_ORGANIZATION_NAME);
	}
	
	public static final String TIPO_FORMATO_CONFIGURAZIONE_CSV = "csv";
	public static final String TIPO_FORMATO_CONFIGURAZIONE_XLS = "xls";
	public static final String TIPO_FORMATO_CONFIGURAZIONE = "formatoConfigurazione";
	private static final List<String> tipiFormatoConfigurazione = new ArrayList<> ();
	public static List<String> getTipiFormatoConfigurazione() {
		return tipiFormatoConfigurazione;
	}
	static{
		tipiFormatoConfigurazione.add(TIPO_FORMATO_CONFIGURAZIONE_CSV);
		tipiFormatoConfigurazione.add(TIPO_FORMATO_CONFIGURAZIONE_XLS);
	}
	
	// usato dal componente web
	public static final String PARAMETER_FORMATO_EXPORT_ORIGINALE = "fExOriginali";
}
