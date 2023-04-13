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
		
	public static final String OBJECT_NAME_MONITOR = "monitor";
	
	public static final ForwardParams TIPO_OPERAZIONE_MONITOR = ForwardParams.OTHER("");
	
	public static final ForwardParams TIPO_OPERAZIONE_MONITOR_DETTAGLI = ForwardParams.OTHER("Dettagli");
	public static final ForwardParams TIPO_OPERAZIONE_MONITOR_STATO_PDD = ForwardParams.OTHER("StatoPdd");
	public static final ForwardParams TIPO_OPERAZIONE_MONITOR_CONFERMA = ForwardParams.OTHER("Confirm");
	/* SERVLET NAME */
	
	public static final String SERVLET_NAME_MONITOR = OBJECT_NAME_MONITOR+".do";
	public static final Vector<String> SERVLET_MONITOR = new Vector<String>();
	static{
		SERVLET_MONITOR.add(SERVLET_NAME_MONITOR);
	}
	
	
	
	/* ATTRIBUTI SESSIONE */
	
	public static final String SESSION_ATTRIBUTE_FORM_BEAN = "formBean";
	public static final String SESSION_ATTRIBUTE_FILTER_SEARCH = "filterSearch";
	
	/* ATTRIBUTI REQUEST */
	
	/* LABEL GENERALI */
	
	public static final String LABEL_MONITOR = "Coda Messaggi";
//	public static final String LABEL_MONITOR_ESTESO = "Monitoraggio Applicativo";
	public static final String LABEL_MONITOR_FILTRO_RICERCA = "Filtro Ricerca";
	public static final String LABEL_MONITOR_INFORMAZIONI_PROTOCOLLO = "Informazioni Protocollo";
	public static final String LABEL_MONITOR_DETTAGLIO_MESSAGGIO = "Dettaglio Messaggio";	
	public static final String LABEL_MONITOR_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO = ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.getEngineValue();
	public static final String LABEL_MONITOR_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO = ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.getEngineValue();
	public static final String LABEL_MONITOR_PROFILO_COLLABORAZIONE_SINCRONO=ProfiloDiCollaborazione.SINCRONO.getEngineValue();
	public static final String LABEL_MONITOR_PROFILO_COLLABORAZIONE_SINGOLO_ONEWAY =ProfiloDiCollaborazione.ONEWAY.getEngineValue();
	public static final String LABEL_MONITOR_PROFILO_COLLABORAZIONE_NONE = "-";
	
	public static final String LABEL_MONITOR_SOGGETTO_MITTENTE = "Soggetto Mittente";
	public static final String LABEL_MONITOR_SOGGETTO_DESTINATARIO = "Soggetto Destinatario";
	public static final String LABEL_MONITOR_SERVIZIO = "Servizio";
	public static final String LABEL_MONITOR_AZIONE = "Azione";
	//public static final String LABEL_MONITOR_RICERCA = "Ricerca";
	public static final String LABEL_MONITOR_STATO_PORTA_DOMINIO = "Stato Porta di Dominio";
	public static final String LABEL_MONITOR_NESSUNA_INFORMAZIONE_PRESENTE = "Nessuna informazione presente!";
	public static final String LABEL_MONITOR_TOTALE_MESSAGGI = "Totale Messaggi";
	public static final String LABEL_MONITOR_NESSUN_MESSAGGIO = "Nessun Messaggio";
	public static final String LABEL_MONITOR_TEMPO_MASSIMO_ATTESA = "Tempo Massimo Attesa";
	public static final String LABEL_MONITOR_TEMPO_MEDIO_ATTESA = "Tempo Medio Attesa";
	public static final String LABEL_MONITOR_MESSAGGI_CONSEGNA = "Messaggi in Consegna";
	public static final String LABEL_MONITOR_MESSAGGI_SPEDIZIONE = "Messaggi in Spedizione";
	public static final String LABEL_MONITOR_NESSUN_MESSAGGIO_CONSEGNA = "Nessun Messaggio in Consegna";
	public static final String LABEL_MONITOR_NESSUN_MESSAGGIO_SPEDIZIONE_ = "Nessun Messaggio in Spedizione";
	public static final String LABEL_MONITOR_MESSAGGI_PROCESSAMENTO = "Messaggi in Processamento";
	public static final String LABEL_MONITOR_TOTALE_MESSAGGI_PROCESSAMENTO = "Totale Messaggi";
	public static final String LABEL_MONITOR_NESSUN_MESSAGGIO_PROCESSAMENTO =  "Nessun Messaggio in Processamento";
	public static final String LABEL_MONITOR_MESSAGGI_DUPLICATI = "Messaggi Duplicati";
	public static final String LABEL_MONITOR_NESSUN_MESSAGGIO_DUPLICATO = "Nessun Messaggio Duplicato";
	public static final String LABEL_MONITOR_EROGAZIONE = "Erogazione"; 
	public static final String LABEL_MONITOR_DETTAGLIO = "Dettaglio"; 
	public static final String LABEL_MONITOR_DETTAGLI_CONSEGNA = "Dettagli Consegna";
	public static final String LABEL_MONITOR_IDMESSAGGIO = "IDMessaggio";
//	public static final String LABEL_MONITOR_INTEGRATION_MANAGER = "IntegrationManager";
	
	
	/*LABEL PULSANTI */

	public static final String LABEL_ACCEDI = "Conferma";
//	public static final String LABEL_MONITOR_BUTTON_CONFERMA = "Conferma"; // TODO cancellami
	
//	public static final String LABEL_MONITOR_BUTTON_OK = "Ok";
//	public static final String LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_1 = "EseguiOp(1)";
//	public static final String LABEL_MONITOR_BUTTON_ANNULLA = "Annulla";
//	public static final String LABEL_MONITOR_BUTTON_ANNULLA_1 = "Annulla(1)"; 
//	
//	public static final String LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_PARAM = "EseguiOp({0})";
//	public static final String LABEL_MONITOR_BUTTON_ANNULLA_OPERAZIONE_PARAM = "Annulla({0})";
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_MONITOR_XXX = "id";
	public static final String PARAMETRO_MONITOR_ID = "id";
	public static final String PARAMETRO_MONITOR_TIPO = "tipo";
	public static final String PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE = "profcoll";
	public static final String PARAMETRO_MONITOR_ID_MESSAGGIO = "idMessaggio";
	public static final String PARAMETRO_MONITOR_METHOD = "method";
	public static final String PARAMETRO_MONITOR_ACTION_CONFIRM = "actionConfirm";
	public static final String PARAMETRO_MONITOR_PAGE_SIZE = "pageSize";
	public static final String PARAMETRO_MONITOR_ACTION = "action";
	public static final String PARAMETRO_MONITOR_NEW_SEARCH = "newSearch";
	public static final String PARAMETRO_MONITOR_TIPO_MITTENTE = "tipoMittente";
	public static final String PARAMETRO_MONITOR_NOME_MITTENTE = "nomeMittente";
	public static final String PARAMETRO_MONITOR_TIPO_DESTINATARIO = "tipoDestinatario";
	public static final String PARAMETRO_MONITOR_NOME_DESTINATARIO = "nomeDestinatario";
	public static final String PARAMETRO_MONITOR_TIPO_SERVIZIO = "tipoServizio";
	public static final String PARAMETRO_MONITOR_NOME_SERVIZIO = "nomeServizio";
	public static final String PARAMETRO_MONITOR_VERSIONE_SERVIZIO = "versioneServizio";
	public static final String PARAMETRO_MONITOR_AZIONE = "monitorAzione";
	public static final String PARAMETRO_MONITOR_SOGLIA = "soglia";
	public static final String PARAMETRO_MONITOR_STATO = "stato";
	public static final String PARAMETRO_MONITOR_ORDER_BY_CONSEGNA_ASINCRONA = "orderAsinc";
	public static final String PARAMETRO_MONITOR_PDD = "pdd";
	public static final String PARAMETRO_MONITOR_SORGENTE = "sorgente";
	public static final String PARAMETRO_MONITOR_RISCONTRO = "riscontro";
	//public static final String PARAMETRO_MONITOR_PATTERN = "pattern";
	public static final String PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA = "correlazioneApplicativa";
	public static final String PARAMETRO_MONITOR_MODULO = "modulo";
	public static final String PARAMETRO_MONITOR_ERRORE = "errore";
	public static final String PARAMETRO_MONITOR_TIPO_CONSEGNA = "tipoConsegna";
	public static final String PARAMETRO_MONITOR_NOME_CONSEGNA_PORTA = "nomeConsegnaPorta";
	public static final String PARAMETRO_MONITOR_NOME_CONSEGNA_APPLICATIVO_INTERNO = "nomeConsegnaApp";
	public static final String PARAMETRO_MONITOR_NOME_CONSEGNA_CONNETTORE = "nomeConsegnaConnettore";
	public static final String PARAMETRO_MONITOR_AUTORIZZAZIONE = "autorizzazione";
	public static final String PARAMETRO_MONITOR_IN_CONSEGNA_DA = "dataRispedizione";
	public static final String PARAMETRO_MONITOR_SBUSTAMENTO = "sbustamento";
	public static final String PARAMETRO_MONITOR_SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = "sbustamentoProtocollo";
	public static final String PARAMETRO_MONITOR_ORA_REGISTRAZIONE = "oraReg";
	public static final String PARAMETRO_MONITOR_ORA_ATTUALE = "oraAtt";
	public static final String PARAMETRO_MONITOR_MITTENTE = "mittente";
	public static final String PARAMETRO_MONITOR_DESTINATARIO = "destinatario";
	public static final String PARAMETRO_MONITOR_SERVIZIO = "servizio";
	public static final String PARAMETRO_MONITOR_RIFERIMENTO = "riferimento";
	public static final String PARAMETRO_MONITOR_PROFILO = "profilo";
	public static final String PARAMETRO_MONITOR_CODA = "coda";
	public static final String PARAMETRO_MONITOR_PRIORITA = "priorita";
	public static final String PARAMETRO_MONITOR_ATTESA_ESITO = "attesaEsito";
	
	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_MONITOR_METHOD = "Metodo";
	public static final String LABEL_PARAMETRO_MONITOR_SORGENTE = "Runtime";
	public static final String LABEL_PARAMETRO_MONITOR_PARAMETRO_MONITOR_ORDER_BY_CONSEGNA_ASINCRONA = "Criterio di Ordinamento";
	public static final String LABEL_PARAMETRO_MONITOR_PORTA_DOMINIO = "Porta di Dominio";
	public static final String LABEL_PARAMETRO_MONITOR_TIPO = "Tipo";
	public static final String LABEL_PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE = "Profilo di collaborazione";
	public static final String LABEL_PARAMETRO_MONITOR_TIPO_MITTENTE = "Tipo";
	public static final String LABEL_PARAMETRO_MONITOR_NOME_MITTENTE = "Nome";
	public static final String LABEL_PARAMETRO_MONITOR_TIPO_DESTINATARIO = "Tipo";
	public static final String LABEL_PARAMETRO_MONITOR_NOME_DESTINATARIO = "Nome";
	public static final String LABEL_PARAMETRO_MONITOR_TIPO_SERVIZIO = "Tipo";
	public static final String LABEL_PARAMETRO_MONITOR_NOME_SERVIZIO = "Nome";
	public static final String LABEL_PARAMETRO_MONITOR_VERSIONE_SERVIZIO = "Versione";
	public static final String LABEL_PARAMETRO_MONITOR_AZIONE = "Azione";
	public static final String LABEL_PARAMETRO_MONITOR_SOGLIA_LABEL = "Anzianità Messaggi (Minuti)";
	public static final String LABEL_PARAMETRO_MONITOR_SOGLIA_NOTE = "Permette di selezionare i messaggi più vecchi dei minuti indicati";
	public static final String LABEL_PARAMETRO_MONITOR_STATO = "Stato";
	public static final String LABEL_PARAMETRO_MONITOR_PDD = "Pdd";
	public static final String LABEL_PARAMETRO_MONITOR_RISCONTRO = "In Attesa di Riscontro";
	//public static final String LABEL_PARAMETRO_MONITOR_PATTERN = "Contenuto messaggio";
	public static final String LABEL_PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA = "ID Applicativo";
	public static final String LABEL_PARAMETRO_MONITOR_ID_MESSAGGIO = "ID";
	public static final String LABEL_PARAMETRO_MONITOR_MODULO = "ID Modulo";
	public static final String LABEL_PARAMETRO_MONITOR_ERRORE = "Errore Processamento";
	public static final String LABEL_PARAMETRO_MONITOR_TIPO_CONSEGNA = "Tipo";
	public static final String LABEL_PARAMETRO_MONITOR_NOME_CONSEGNA_APPLICATIVO_INTERNO = "Nome Applicativo";
	public static final String LABEL_PARAMETRO_MONITOR_NOME_CONSEGNA_PORTA = "Erogazione";
	public static final String LABEL_PARAMETRO_MONITOR_NOME_CONSEGNA_CONNETTORE = "Connettore";
	public static final String LABEL_PARAMETRO_MONITOR_IN_CONSEGNA_DA = "Prossima Consegna";
	public static final String LABEL_PARAMETRO_MONITOR_AUTORIZZAZIONE = "Authz MessageBox";
	public static final String LABEL_PARAMETRO_MONITOR_SBUSTAMENTO = "Sbustamento SOAP";
	public static final String LABEL_PARAMETRO_MONITOR_SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = "Sbustamento Protocollo";
	public static final String LABEL_PARAMETRO_MONITOR_ORA_REGISTRAZIONE = "Ora Registrazione";
	public static final String LABEL_PARAMETRO_MONITOR_ORA_ATTUALE = "Ora Attuale";
	public static final String LABEL_PARAMETRO_MONITOR_MITTENTE = "Mittente";
	public static final String LABEL_PARAMETRO_MONITOR_DESTINATARIO = "Destinatario";
	public static final String LABEL_PARAMETRO_MONITOR_SERVIZIO = "Servizio";
	public static final String LABEL_PARAMETRO_MONITOR_RIFERIMENTO = "Riferimento Messaggio";
	public static final String LABEL_PARAMETRO_MONITOR_PROFILO = "Profilo Collaborazione";
	public static final String LABEL_PARAMETRO_MONITOR_CODA = "Coda";
	public static final String LABEL_PARAMETRO_MONITOR_PRIORITA = "Priorita";
	public static final String LABEL_PARAMETRO_MONITOR_ATTESA_ESITO = "Attesa Esito Sincrono";
	
	public static final String LABEL_PARAMETRO_MONITOR_NOW = "Ora Attuale";
	public static final String LABEL_PARAMETRO_MONITOR_SERVIZIO_APPLICATIVO = "Servizio";
	
	public static final String LABEL_PARAMETRO_MONITOR_VECCHIO = "Vecchio: ";
	public static final String LABEL_PARAMETRO_MONITOR_RECENTE = "Recente: ";
	
	public static final String LABEL_PARAMETRO_MONITOR_IN_CODA = "In Coda";
	public static final String LABEL_PARAMETRO_MONITOR_IN_CODA_VECCHIO = "Vecchio in Coda";
	public static final String LABEL_PARAMETRO_MONITOR_IN_CODA_RECENTE = "Recente in Coda";

	public static final String LABEL_PARAMETRO_MONITOR_IN_RICONSEGNA = "In Riconsegna";
	public static final String LABEL_PARAMETRO_MONITOR_IN_RICONSEGNA_VECCHIO = "Vecchio in Riconsegna";
	public static final String LABEL_PARAMETRO_MONITOR_IN_RICONSEGNA_RECENTE = "Recente in Riconsegna";
	
	public static final String LABEL_PARAMETRO_MONITOR_IN_MESSAGE_BOX = "In MessageBox";
	public static final String LABEL_PARAMETRO_MONITOR_IN_MESSAGE_BOX_VECCHIO = "Vecchio in MessageBox";
	public static final String LABEL_PARAMETRO_MONITOR_IN_MESSAGE_BOX_RECENTE = "Recente in MessageBox";

	
	/* DEFAULT VALUE PARAMETRI */

	public static final String DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ANY = "-";
	public static final String DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ONEWAY = "oneway";
	public static final String DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_SINCRONO = "sincrono";
	public static final String DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO = "asincrono-simmetrico";
	public static final String DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO = "asincrono-asimmetrico";
	
	public static final String[]  DEFAULT_VALUES_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE = {
		DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ANY, DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ONEWAY,
		DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_SINCRONO, DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
		DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO
	};
	
	
	public static final String DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS = "details";
	
	public static final String DEFAULT_VALUE_PARAMETRO_MONITOR_ACTION_DELETE = "delete";
	 
	
	public static final String DEFAULT_VALUE_FALSE = "false";
	 
	 
	public static final String DEFAULT_VALUE_PARAMETRO_STATO_NONE = "-";
	public static final String DEFAULT_VALUE_PARAMETRO_STATO_CONSEGNA = "consegna";
	public static final String DEFAULT_VALUE_PARAMETRO_STATO_SPEDIZIONE = "spedizione";
	public static final String DEFAULT_VALUE_PARAMETRO_STATO_PROCESSAMENTO = "processamento";
	 
	public static final String[]  DEFAULT_VALUES_PARAMETRO_STATO = {
		DEFAULT_VALUE_PARAMETRO_STATO_NONE,DEFAULT_VALUE_PARAMETRO_STATO_CONSEGNA,
		DEFAULT_VALUE_PARAMETRO_STATO_SPEDIZIONE, DEFAULT_VALUE_PARAMETRO_STATO_PROCESSAMENTO
	};
	 	
	public static final String LABEL_ACTION_RICONSEGNA_IMMEDIATA = "Riconsegna Immediata";
	public static final String ACTION_RICONSEGNA_IMMEDIATA = "resend";
	public static final String ACTION_RICONSEGNA_IMMEDIATA_ONCLICK = "RemoveEntries('"+ACTION_RICONSEGNA_IMMEDIATA+"')";

}
