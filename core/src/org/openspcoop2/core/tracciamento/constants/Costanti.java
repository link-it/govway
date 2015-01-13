/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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


package org.openspcoop2.core.tracciamento.constants;
/**
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	public static final String ROOT_LOCAL_NAME_TRACCIA = "traccia";
	
	public static final String TARGET_NAMESPACE = "http://www.openspcoop2.org/core/tracciamento";
	
	
	public static final String TIPO_TRACCIA_RICHIESTA = "Richiesta";
	public static final String TIPO_TRACCIA_RISPOSTA = "Risposta";
	
	public static final String TIPO_PDD_PORTA_DELEGATA = "PortaDelegata";
	public static final String TIPO_PDD_PORTA_APPLICATIVA = "PortaApplicativa";
	public static final String TIPO_PDD_INTEGRATION_MANAGER = "IntegrationManager";
	public static final String TIPO_PDD_ROUTER = "Router";
	
	public static final String TIPO_ESITO_ELABORAZIONE_INVIATO = "Inviato";
	public static final String TIPO_ESITO_ELABORAZIONE_RICEVUTO = "Ricevuto";
	public static final String TIPO_ESITO_ELABORAZIONE_ERRORE = "Errore";
	
	public static final String TIPO_PROFILO_COLLABORAZIONE_ONEWAY = "Oneway";
	public static final String TIPO_PROFILO_COLLABORAZIONE_SINCRONO = "Sincrono";
	public static final String TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO = "AsincronoSimmetrico";
	public static final String TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO = "AsincronoAsimmetrico";
	public static final String TIPO_PROFILO_COLLABORAZIONE_SCONOSCIUTO = "Sconosciuto";
	
	public static final String TIPO_TEMPO_LOCALE = "Locale";
	public static final String TIPO_TEMPO_SINCRONIZZATO = "Sincronizzato";
	public static final String TIPO_TEMPO_SCONOSCIUTO = "Sconosciuto";
	
	public static final String TIPO_INOLTRO_CON_DUPLICATI = "InoltroConDuplicati";
	public static final String TIPO_INOLTRO_SENZA_DUPLICATI = "InoltroSenzaDuplicati";
	public static final String TIPO_INOLTRO_SCONOSCIUTO = "Sconosciuto";
	
	public static final String TIPO_CODIFICA_ECCEZIONE_VALIDAZIONE = "EccezioneValidazioneProtocollo";
	public static final String TIPO_CODIFICA_ECCEZIONE_PROCESSAMENTO = "EccezioneProcessamento";
	public static final String TIPO_CODIFICA_ECCEZIONE_SCONOSCIUTO = "Sconosciuto";
	
	public static final String TIPO_RILEVANZA_ECCEZIONE_DEBUG = "DEBUG";
	public static final String TIPO_RILEVANZA_ECCEZIONE_INFO = "INFO";
	public static final String TIPO_RILEVANZA_ECCEZIONE_WARN = "WARN";
	public static final String TIPO_RILEVANZA_ECCEZIONE_ERROR = "ERROR";
	public static final String TIPO_RILEVANZA_ECCEZIONE_FATAL = "FATAL";
	public static final String TIPO_RILEVANZA_ECCEZIONE_SCONOSCIUTO = "Sconosciuto";

}


