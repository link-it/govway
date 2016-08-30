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



package org.openspcoop2.testsuite.core;

/**
 * Costanti utilizzate nei server della testsuite
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CostantiTestSuite {

	public final static String OPENSPCOOP2_LOCAL_HOME = "OPENSPCOOP2_HOME";
	
    public final static String OPENSPCOOP2_TESTSUITE_LOCAL_PATH = "testsuite_local.properties";
    public final static String OPENSPCOOP2_TESTSUITE_PROPERTIES = "OPENSPCOOP2_TESTSUITE_PROPERTIES";
	
	
	/** Timeout */
	public static int CONNECTION_TIMEOUT=6000;
	public static void setCONNECTION_TIMEOUT(int cONNECTION_TIMEOUT) {
		CONNECTION_TIMEOUT = cONNECTION_TIMEOUT;
	}
	public static int READ_TIMEOUT=6000;
	public static void setREAD_TIMEOUT(int rEAD_TIMEOUT) {
		READ_TIMEOUT = rEAD_TIMEOUT;
	}
	
	/** Tag inserito nell'header per la gestione di interazioni sincrone */
	public static final String TAG_NAME="ID_VALUE";
	/** Tag inserito nell'header per la gestione di interazioni sincrone */
	public static final String ID_HEADER_ATTRIBUTE="ID"; //attributo per mettere un identificatore del cliente e la porta correlata

	
	/** Username per risposta asincrona simmetrica */
	public static final String CREDENZIALI_RISPOSTA_ASINCRONA_SIMMETRICA="CREDENZIALI_AS_VALUE";
	public static final String CREDENZIALI_USERNAME="USERNAME";
	public static final String CREDENZIALI_PASSWORD="PASSWORD";
	
	
	/** File di configurazione dei server */
	public static final String TESTSUITE_PROPERTIES = "testsuite.properties";
	
	/** File di configurazione dei server */
	public static final String LOGGER_PROPERTIES = "testsuite.log4j2.properties";
		
	/** Proprieta' del tracciamento is arrived sul database tracciamento */
	public static final String PROPERTY_IS_ARRIVED = "org.openspcoop2.testsuite.server.database.tracciamentoIsArrived";
	
	/** Proprieta' del caricamento MAILCAP */
	public static final String PROPERTY_MAILCAP = "org.openspcoop2.testsuite.server.mailcap.load";
	
	/** Nome della Servlet Invocata */
	public static final String PROPERTY_HEADER_RISPOSTA_SERVLET_NAME="org.openspcoop2.testsuite.server.risposta.headerServletName";
	/** Header http generico della risposta */
	public static final String PROPERTY_HEADER_RISPOSTA_GENERICO="org.openspcoop2.testsuite.server.risposta.header.";
	
	/** Nome della Proprieta' che contiene l'id della richiesta in corso */
	public static final String PROPERTY_ID_MESSAGGIO_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.id";
	/** Nome della Proprieta' che contiene il riferimento asincrono */
	public static final String PROPERTY_RIFERIMENTO_ASINCRONO_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.riferimentoAsincrono";
	/** Nome della Proprieta' che contiene l'id della richiesta in corso */
	public static final String PROPERTY_TIPO_MITTENTE_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.tipoMittente";
	public static final String PROPERTY_MITTENTE_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.mittente";
	public static final String PROPERTY_TIPO_DESTINATARIO_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.tipoDestinatario";
	public static final String PROPERTY_DESTINATARIO_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.destinatario";
	public static final String PROPERTY_TIPO_SERVIZIO_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.tipoServizio";
	public static final String PROPERTY_SERVIZIO_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.servizio";
	public static final String PROPERTY_AZIONE_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.azione";
	/** Nome della Proprieta' che contiene la collaborazione */
	public static final String PROPERTY_COLLABORAZIONE_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.collaborazione";
	/** Servizio applicativo */
	public static final String PROPERTY_SERVIZIO_APPLICATIVO_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.integrazione.servizioApplicativo";
	/** Servizio applicativo */
	public static final String PROPERTY_ID_APPLICATIVO_TRASPORTO="org.openspcoop2.testsuite.server.trasporto.integrazione.idApplicativo";
	
	
	/** Nome della Proprieta' che contiene l'id della richiesta in corso */
	public static final String PROPERTY_ID_URL_BASED="org.openspcoop2.testsuite.server.urlBased.id";
	/** Nome della Proprieta' che contiene il riferimento asincrono */
	public static final String PROPERTY_RIFERIMENTO_ASINCRONO_URL_BASED="org.openspcoop2.testsuite.server.urlBased.riferimentoAsincrono";
	/** Nome della Proprieta' che contiene l'id della richiesta in corso */
	public static final String PROPERTY_TIPO_MITTENTE_URL_BASED="org.openspcoop2.testsuite.server.urlBased.tipoMittente";
	public static final String PROPERTY_MITTENTE_URL_BASED="org.openspcoop2.testsuite.server.urlBased.mittente";
	public static final String PROPERTY_TIPO_DESTINATARIO_URL_BASED="org.openspcoop2.testsuite.server.urlBased.tipoDestinatario";
	public static final String PROPERTY_DESTINATARIO_URL_BASED="org.openspcoop2.testsuite.server.urlBased.destinatario";
	public static final String PROPERTY_TIPO_SERVIZIO_URL_BASED="org.openspcoop2.testsuite.server.urlBased.tipoServizio";
	public static final String PROPERTY_SERVIZIO_URL_BASED="org.openspcoop2.testsuite.server.urlBased.servizio";
	public static final String PROPERTY_AZIONE_URL_BASED="org.openspcoop2.testsuite.server.urlBased.azione";
	/** Nome della Proprieta' che contiene la collaborazione */
	public static final String PROPERTY_COLLABORAZIONE_URL_BASED="org.openspcoop2.testsuite.server.urlBased.collaborazione";
	/** Servizio applicativo */
	public static final String PROPERTY_SERVIZIO_APPLICATIVO_URL_BASED="org.openspcoop2.testsuite.server.urlBased.integrazione.servizioApplicativo";
	/** Servizio applicativo */
	public static final String PROPERTY_ID_APPLICATIVO_URL_BASED="org.openspcoop2.testsuite.server.urlBased.integrazione.idApplicativo";
	
	
	/** Nome della Proprieta' che contiene l'id della richiesta in corso */
	public static final String PROPERTY_ID_MESSAGGIO_SOAP="org.openspcoop2.testsuite.server.soap.id";
	/** Nome della Proprieta' che contiene il riferimento asincrono */
	public static final String PROPERTY_RIFERIMENTO_ASINCRONO_SOAP="org.openspcoop2.testsuite.server.soap.riferimentoAsincrono";
	/** Nome della Proprieta' che contiene l'id della richiesta in corso */
	public static final String PROPERTY_TIPO_MITTENTE_SOAP="org.openspcoop2.testsuite.server.soap.tipoMittente";
	public static final String PROPERTY_MITTENTE_SOAP="org.openspcoop2.testsuite.server.soap.mittente";
	public static final String PROPERTY_TIPO_DESTINATARIO_SOAP="org.openspcoop2.testsuite.server.soap.tipoDestinatario";
	public static final String PROPERTY_DESTINATARIO_SOAP="org.openspcoop2.testsuite.server.soap.destinatario";
	public static final String PROPERTY_TIPO_SERVIZIO_SOAP="org.openspcoop2.testsuite.server.soap.tipoServizio";
	public static final String PROPERTY_SERVIZIO_SOAP="org.openspcoop2.testsuite.server.soap.servizio";
	public static final String PROPERTY_AZIONE_SOAP="org.openspcoop2.testsuite.server.soap.azione";
	/** Nome della Proprieta' che contiene la collaborazione */
	public static final String PROPERTY_COLLABORAZIONE_SOAP="org.openspcoop2.testsuite.server.soap.collaborazione";
	/** Servizio applicativo */
	public static final String PROPERTY_SERVIZIO_APPLICATIVO_SOAP="org.openspcoop2.testsuite.server.soap.integrazione.servizioApplicativo";
	/** Servizio applicativo */
	public static final String PROPERTY_ID_APPLICATIVO_SOAP="org.openspcoop2.testsuite.server.soap.integrazione.idApplicativo";
	
	
	
	/** Tipo di repository */
	public static final String TIPO_REPOSITORY_BUSTE="org.openspcoop2.repository.gestore";
		
	/** Nome della Proprieta' che indica il Tempo di attesa in millisecondi, prima della generazione della risposta asincrona */
	public static final String PROPERTY_TIME_TO_SLEEP_SERVER_ASINCRONO="org.openspcoop2.testsuite.server.generazioneRispostAsincrona.timeToSleep";
	/** Nome della Proprieta' che indica il Tempo di attesa in millisecondi, prima della generazione della risposta asincrona */
	public static final String PROPERTY_ATTESA_TERMINAZIONI_MESSAGGI_SERVER_ASINCRONO="org.openspcoop2.testsuite.server.generazioneRispostAsincrona.attesaTerminazioneMessaggi";

	
	/** Nome della Proprieta' che indica il Servizio di ricezione contenuti applicativi della porta di dominio erogatore*/
	public static final String PROPERTY_OPENSPCOOP_PD_CONSEGNA_RISPOSTA_ASINCRONA_SIMMETRICA="org.openspcoop2.testsuite.org.openspcoop2.PD.consegnaRispostaAsincronaSimmetrica";
	
	
	/** Protocollo di default */
	public static final String PROTOCOLLO_DEFAULT="org.openspcoop2.protocolloDefault";
	
	/** String che indica il Content-Type http */
	public static final String CONTENT_TYPE="Content-Type";
	
	/** Soap Action */
	public static final String SOAP_ACTION="SOAPAction";
	
	/** IntegrationManager utilizzo */
	public static final String UTILIZZO_INTEGRATION_MANAGER = "$$$IntegrationManager$$$";
		
	/** Pulsante di 'Select All' */
	public static final String BOTTONE_SELECT_ALL = "Seleziona Tutti";
	/** Pulsante di 'Deselect All' */
	public static final String BOTTONE_DESELECT_ALL = "Deseleziona Tutti";
	/** Pulsante di 'Remove Selected' */
	public static final String BOTTONE_REMOVE_SELECTED = "Rimuovi Selezionati";
	/** Pulsante di 'Add' */
	public static final String BOTTONE_ADD = "Aggiungi";
	/** Pulsante di 'Invia' */
	public static final String BOTTONE_INVIA = "Invia";
	
	public static final String TABLE_TRANSAZIONI = "transazioni";
	public static final String TABLE_TRANSAZIONI_ID_MESSAGGIO_RICHIESTA = "id_messaggio_richiesta";
	public static final String TABLE_TRANSAZIONI_DUPLICATI_RICHIESTA = "duplicati_richiesta";
	public static final String TABLE_TRANSAZIONI_ID_MESSAGGIO_RISPOSTA = "id_messaggio_risposta";
	public static final String TABLE_TRANSAZIONI_DUPLICATI_RISPOSTA = "duplicati_risposta";
	
}
