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
package org.openspcoop2.web.ctrlstat.servlet.monitor;

import java.util.Vector;

import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * MonitorCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MonitorCostanti {

	/* OBJECT NAME */
		
	public final static String OBJECT_NAME_MONITOR = "monitor";
	
	public final static ForwardParams TIPO_OPERAZIONE_MONITOR = ForwardParams.OTHER("");
	
	public final static ForwardParams TIPO_OPERAZIONE_MONITOR_DETTAGLI = ForwardParams.OTHER("Dettagli");
	public final static ForwardParams TIPO_OPERAZIONE_MONITOR_STATO_PDD = ForwardParams.OTHER("StatoPdd");
	public final static ForwardParams TIPO_OPERAZIONE_MONITOR_CONFERMA = ForwardParams.OTHER("Confirm");
	/* SERVLET NAME */
	
	public final static String SERVLET_NAME_MONITOR = OBJECT_NAME_MONITOR+".do";
	public final static Vector<String> SERVLET_MONITOR = new Vector<String>();
	static{
		SERVLET_MONITOR.add(SERVLET_NAME_MONITOR);
	}
	
	
	
	/* ATTRIBUTI SESSIONE */
	
	public final static String SESSION_ATTRIBUTE_FORM_BEAN = "formBean";
	public final static String SESSION_ATTRIBUTE_FILTER_SEARCH = "filterSearch";
	
	/* ATTRIBUTI REQUEST */
	
	/* LABEL GENERALI */
	
	public final static String LABEL_MONITOR = "Coda Messaggi";
//	public final static String LABEL_MONITOR_ESTESO = "Monitoraggio Applicativo";
	public final static String LABEL_MONITOR_FILTRO_RICERCA = "Filtro Ricerca";
	public final static String LABEL_MONITOR_INFORMAZIONI_PROTOCOLLO = "Informazioni Protocollo";
	public final static String LABEL_MONITOR_DETTAGLIO_MESSAGGIO = "Dettaglio Messaggio";	
	public final static String LABEL_MONITOR_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO = ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.getEngineValue();
	public final static String LABEL_MONITOR_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO = ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.getEngineValue();
	public final static String LABEL_MONITOR_PROFILO_COLLABORAZIONE_SINCRONO=ProfiloDiCollaborazione.SINCRONO.getEngineValue();
	public final static String LABEL_MONITOR_PROFILO_COLLABORAZIONE_SINGOLO_ONEWAY =ProfiloDiCollaborazione.ONEWAY.getEngineValue();
	public final static String LABEL_MONITOR_PROFILO_COLLABORAZIONE_NONE = "-";
	
	public final static String LABEL_MONITOR_SOGGETTO_MITTENTE = "Soggetto Mittente";
	public final static String LABEL_MONITOR_SOGGETTO_DESTINATARIO = "Soggetto Destinatario";
	public final static String LABEL_MONITOR_SERVIZIO = "Servizio";
	public final static String LABEL_MONITOR_AZIONE = "Azione";
	//public final static String LABEL_MONITOR_RICERCA = "Ricerca";
	public final static String LABEL_MONITOR_STATO_PORTA_DOMINIO = "Stato Porta di Dominio";
	public final static String LABEL_MONITOR_NESSUNA_INFORMAZIONE_PRESENTE = "Nessuna informazione presente!";
	public final static String LABEL_MONITOR_TOTALE_MESSAGGI = "Totale Messaggi";
	public final static String LABEL_MONITOR_NESSUN_MESSAGGIO = "Nessun Messaggio";
	public final static String LABEL_MONITOR_TEMPO_MASSIMO_ATTESA = "Tempo Massimo Attesa";
	public final static String LABEL_MONITOR_TEMPO_MEDIO_ATTESA = "Tempo Medio Attesa";
	public final static String LABEL_MONITOR_MESSAGGI_CONSEGNA = "Messaggi in Consegna";
	public final static String LABEL_MONITOR_MESSAGGI_SPEDIZIONE = "Messaggi in Spedizione";
	public final static String LABEL_MONITOR_NESSUN_MESSAGGIO_CONSEGNA = "Nessun Messaggio in Consegna";
	public final static String LABEL_MONITOR_NESSUN_MESSAGGIO_SPEDIZIONE_ = "Nessun Messaggio in Spedizione";
	public final static String LABEL_MONITOR_MESSAGGI_PROCESSAMENTO = "Messaggi in Processamento";
	public final static String LABEL_MONITOR_TOTALE_MESSAGGI_PROCESSAMENTO = "Totale Messaggi";
	public final static String LABEL_MONITOR_NESSUN_MESSAGGIO_PROCESSAMENTO =  "Nessun Messaggio in Processamento";
	public final static String LABEL_MONITOR_MESSAGGI_DUPLICATI = "Messaggi Duplicati";
	public final static String LABEL_MONITOR_NESSUN_MESSAGGIO_DUPLICATO = "Nessun Messaggio Duplicato";
	public final static String LABEL_MONITOR_EROGAZIONE = "Erogazione"; 
	public final static String LABEL_MONITOR_DETTAGLIO = "Dettaglio"; 
	public final static String LABEL_MONITOR_DETTAGLI_CONSEGNA = "Dettagli Consegna";
	public final static String LABEL_MONITOR_IDMESSAGGIO = "IDMessaggio";
//	public final static String LABEL_MONITOR_INTEGRATION_MANAGER = "IntegrationManager";
	
	
	/*LABEL PULSANTI */

	public final static String LABEL_ACCEDI = "Conferma";
//	public final static String LABEL_MONITOR_BUTTON_CONFERMA = "Conferma"; // TODO cancellami
	
//	public final static String LABEL_MONITOR_BUTTON_OK = "Ok";
//	public final static String LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_1 = "EseguiOp(1)";
//	public final static String LABEL_MONITOR_BUTTON_ANNULLA = "Annulla";
//	public final static String LABEL_MONITOR_BUTTON_ANNULLA_1 = "Annulla(1)"; 
//	
//	public final static String LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_PARAM = "EseguiOp({0})";
//	public final static String LABEL_MONITOR_BUTTON_ANNULLA_OPERAZIONE_PARAM = "Annulla({0})";
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_MONITOR_XXX = "id";
	public final static String PARAMETRO_MONITOR_ID = "id";
	public final static String PARAMETRO_MONITOR_TIPO = "tipo";
	public final static String PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE = "profcoll";
	public final static String PARAMETRO_MONITOR_ID_MESSAGGIO = "idMessaggio";
	public final static String PARAMETRO_MONITOR_METHOD = "method";
	public final static String PARAMETRO_MONITOR_ACTION_CONFIRM = "actionConfirm";
	public final static String PARAMETRO_MONITOR_PAGE_SIZE = "pageSize";
	public final static String PARAMETRO_MONITOR_ACTION = "action";
	public final static String PARAMETRO_MONITOR_NEW_SEARCH = "newSearch";
	public final static String PARAMETRO_MONITOR_TIPO_MITTENTE = "tipoMittente";
	public final static String PARAMETRO_MONITOR_NOME_MITTENTE = "nomeMittente";
	public final static String PARAMETRO_MONITOR_TIPO_DESTINATARIO = "tipoDestinatario";
	public final static String PARAMETRO_MONITOR_NOME_DESTINATARIO = "nomeDestinatario";
	public final static String PARAMETRO_MONITOR_TIPO_SERVIZIO = "tipoServizio";
	public final static String PARAMETRO_MONITOR_NOME_SERVIZIO = "nomeServizio";
	public final static String PARAMETRO_MONITOR_VERSIONE_SERVIZIO = "versioneServizio";
	public final static String PARAMETRO_MONITOR_AZIONE = "azione";
	public final static String PARAMETRO_MONITOR_SOGLIA = "soglia";
	public final static String PARAMETRO_MONITOR_STATO = "stato";
	public final static String PARAMETRO_MONITOR_PDD = "pdd";
	public final static String PARAMETRO_MONITOR_SORGENTE = "sorgente";
	public final static String PARAMETRO_MONITOR_RISCONTRO = "riscontro";
	//public final static String PARAMETRO_MONITOR_PATTERN = "pattern";
	public final static String PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA = "correlazioneApplicativa";
	public final static String PARAMETRO_MONITOR_MODULO = "modulo";
	public final static String PARAMETRO_MONITOR_ERRORE = "errore";
	public final static String PARAMETRO_MONITOR_TIPO_CONSEGNA = "tipoConsegna";
	public final static String PARAMETRO_MONITOR_NOME_CONSEGNA_PORTA = "nomeConsegnaPorta";
	public final static String PARAMETRO_MONITOR_NOME_CONSEGNA_APPLICATIVO_INTERNO = "nomeConsegnaApp";
	public final static String PARAMETRO_MONITOR_NOME_CONSEGNA_CONNETTORE = "nomeConsegnaConnettore";
	public final static String PARAMETRO_MONITOR_AUTORIZZAZIONE = "autorizzazione";
	public final static String PARAMETRO_MONITOR_IN_CONSEGNA_DA = "dataRispedizione";
	public final static String PARAMETRO_MONITOR_SBUSTAMENTO = "sbustamento";
	public final static String PARAMETRO_MONITOR_SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = "sbustamentoProtocollo";
	public final static String PARAMETRO_MONITOR_ORA_REGISTRAZIONE = "oraReg";
	public final static String PARAMETRO_MONITOR_ORA_ATTUALE = "oraAtt";
	public final static String PARAMETRO_MONITOR_MITTENTE = "mittente";
	public final static String PARAMETRO_MONITOR_DESTINATARIO = "destinatario";
	public final static String PARAMETRO_MONITOR_SERVIZIO = "servizio";
	public final static String PARAMETRO_MONITOR_RIFERIMENTO = "riferimento";
	public final static String PARAMETRO_MONITOR_PROFILO = "profilo";
	public final static String PARAMETRO_MONITOR_CODA = "coda";
	public final static String PARAMETRO_MONITOR_PRIORITA = "priorita";
	public final static String PARAMETRO_MONITOR_ATTESA_ESITO = "attesaEsito";
	
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_MONITOR_METHOD = "Metodo";
	public final static String LABEL_PARAMETRO_MONITOR_SORGENTE = "Runtime";
	public final static String LABEL_PARAMETRO_MONITOR_PORTA_DOMINIO = "Porta di Dominio";
	public final static String LABEL_PARAMETRO_MONITOR_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE = "Profilo di collaborazione";
	public final static String LABEL_PARAMETRO_MONITOR_TIPO_MITTENTE = "Tipo";
	public final static String LABEL_PARAMETRO_MONITOR_NOME_MITTENTE = "Nome";
	public final static String LABEL_PARAMETRO_MONITOR_TIPO_DESTINATARIO = "Tipo";
	public final static String LABEL_PARAMETRO_MONITOR_NOME_DESTINATARIO = "Nome";
	public final static String LABEL_PARAMETRO_MONITOR_TIPO_SERVIZIO = "Tipo";
	public final static String LABEL_PARAMETRO_MONITOR_NOME_SERVIZIO = "Nome";
	public final static String LABEL_PARAMETRO_MONITOR_VERSIONE_SERVIZIO = "Versione";
	public final static String LABEL_PARAMETRO_MONITOR_AZIONE = "Azione";
	public final static String LABEL_PARAMETRO_MONITOR_SOGLIA_LABEL = "Anzianità Messaggi (Minuti)";
	public final static String LABEL_PARAMETRO_MONITOR_SOGLIA_NOTE = "Permette di selezionare i messaggi più vecchi dei minuti indicati";
	public final static String LABEL_PARAMETRO_MONITOR_STATO = "Stato";
	public final static String LABEL_PARAMETRO_MONITOR_PDD = "Pdd";
	public final static String LABEL_PARAMETRO_MONITOR_RISCONTRO = "In Attesa di Riscontro";
	//public final static String LABEL_PARAMETRO_MONITOR_PATTERN = "Contenuto messaggio";
	public final static String LABEL_PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA = "ID Applicativo";
	public final static String LABEL_PARAMETRO_MONITOR_ID_MESSAGGIO = "ID";
	public final static String LABEL_PARAMETRO_MONITOR_MODULO = "ID Modulo";
	public final static String LABEL_PARAMETRO_MONITOR_ERRORE = "Errore Processamento";
	public final static String LABEL_PARAMETRO_MONITOR_TIPO_CONSEGNA = "Tipo";
	public final static String LABEL_PARAMETRO_MONITOR_NOME_CONSEGNA_APPLICATIVO_INTERNO = "Nome Applicativo";
	public final static String LABEL_PARAMETRO_MONITOR_NOME_CONSEGNA_PORTA = "Erogazione";
	public final static String LABEL_PARAMETRO_MONITOR_NOME_CONSEGNA_CONNETTORE = "Connettore";
	public final static String LABEL_PARAMETRO_MONITOR_IN_CONSEGNA_DA = "Prossima Consegna";
	public final static String LABEL_PARAMETRO_MONITOR_AUTORIZZAZIONE = "Authz MessageBox";
	public final static String LABEL_PARAMETRO_MONITOR_SBUSTAMENTO = "Sbustamento SOAP";
	public final static String LABEL_PARAMETRO_MONITOR_SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = "Sbustamento Protocollo";
	public final static String LABEL_PARAMETRO_MONITOR_ORA_REGISTRAZIONE = "Ora Registrazione";
	public final static String LABEL_PARAMETRO_MONITOR_ORA_ATTUALE = "Ora Attuale";
	public final static String LABEL_PARAMETRO_MONITOR_MITTENTE = "Mittente";
	public final static String LABEL_PARAMETRO_MONITOR_DESTINATARIO = "Destinatario";
	public final static String LABEL_PARAMETRO_MONITOR_SERVIZIO = "Servizio";
	public final static String LABEL_PARAMETRO_MONITOR_RIFERIMENTO = "Riferimento Messaggio";
	public final static String LABEL_PARAMETRO_MONITOR_PROFILO = "Profilo Collaborazione";
	public final static String LABEL_PARAMETRO_MONITOR_CODA = "Coda";
	public final static String LABEL_PARAMETRO_MONITOR_PRIORITA = "Priorita";
	public final static String LABEL_PARAMETRO_MONITOR_ATTESA_ESITO = "Attesa Esito Sincrono";
	
	/* DEFAULT VALUE PARAMETRI */

	public final static String DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ANY = "-";
	public final static String DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ONEWAY = "oneway";
	public final static String DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_SINCRONO = "sincrono";
	public final static String DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO = "asincrono-simmetrico";
	public final static String DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO = "asincrono-asimmetrico";
	
	public final static String[]  DEFAULT_VALUES_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE = {
		DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ANY, DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ONEWAY,
		DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_SINCRONO, DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
		DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO
	};
	
	
	public final static String DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS = "details";
	
	public final static String DEFAULT_VALUE_PARAMETRO_MONITOR_ACTION_DELETE = "delete";
	 
	
	public final static String DEFAULT_VALUE_FALSE = "false";
	 
	 
	public final static String DEFAULT_VALUE_PARAMETRO_STATO_NONE = "-";
	public final static String DEFAULT_VALUE_PARAMETRO_STATO_CONSEGNA = "consegna";
	public final static String DEFAULT_VALUE_PARAMETRO_STATO_SPEDIZIONE = "spedizione";
	public final static String DEFAULT_VALUE_PARAMETRO_STATO_PROCESSAMENTO = "processamento";
	 
	public final static String[]  DEFAULT_VALUES_PARAMETRO_STATO = {
		DEFAULT_VALUE_PARAMETRO_STATO_NONE,DEFAULT_VALUE_PARAMETRO_STATO_CONSEGNA,
		DEFAULT_VALUE_PARAMETRO_STATO_SPEDIZIONE, DEFAULT_VALUE_PARAMETRO_STATO_PROCESSAMENTO
	};
	 	
	public final static String LABEL_ACTION_RICONSEGNA_IMMEDIATA = "Riconsegna Immediata";
	public final static String ACTION_RICONSEGNA_IMMEDIATA = "resend";
	public final static String ACTION_RICONSEGNA_IMMEDIATA_ONCLICK = "RemoveEntries('"+ACTION_RICONSEGNA_IMMEDIATA+"')";

}
