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

package org.openspcoop2.web.lib.audit.web;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * AuditCostanti
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuditCostanti {


	public static final String OBJECT_NAME_CONFIGURAZIONE_AUDITING = "configurazioneAuditing";
	public static final ForwardParams TIPO_OPERAZIONE_CONFIGURAZIONE_AUDITING = ForwardParams.OTHER("");

	public static final String OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI = "configurazioneAuditingFiltri";
	
	public static final String OBJECT_NAME_AUDITING = "auditing";
	public static final ForwardParams TIPO_OPERAZIONE_AUDITING = ForwardParams.OTHER("");
	
	public static final String OBJECT_NAME_AUDITING_DETTAGLIO = "auditingDettaglio";
	public static final ForwardParams TIPO_OPERAZIONE_AUDITING_DETTAGLIO = ForwardParams.OTHER("");
	public static final ForwardParams TIPO_OPERAZIONE_AUDITING_DETTAGLIO_INFO = ForwardParams.OTHER("Info");
	public static final ForwardParams TIPO_OPERAZIONE_AUDITING_DETTAGLIO_DOCUMENTI_BINARI = ForwardParams.OTHER("DocumentiBinari");

	/* Servlet */

	public static final String SERVLET_NAME_AUDIT  = OBJECT_NAME_CONFIGURAZIONE_AUDITING +".do";
	public static final List<String> SERVLET_AUDIT  = new ArrayList<String>();
	static{
		SERVLET_AUDIT .add(SERVLET_NAME_AUDIT );
	}

	public static final String SERVLET_NAME_AUDIT_FILTRI_ADD = OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI+"Add.do";
	public static final String SERVLET_NAME_AUDIT_FILTRI_CHANGE = OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI+"Change.do";
	public static final String SERVLET_NAME_AUDIT_FILTRI_DELETE = OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI+"Del.do";
	public static final String SERVLET_NAME_AUDIT_FILTRI_LIST = OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI+"List.do";
	public static final List<String> SERVLET_AUDIT_FILTRI = new ArrayList<String>();
	static{
		SERVLET_AUDIT_FILTRI.add(SERVLET_NAME_AUDIT_FILTRI_ADD);
		SERVLET_AUDIT_FILTRI.add(SERVLET_NAME_AUDIT_FILTRI_CHANGE);
		SERVLET_AUDIT_FILTRI.add(SERVLET_NAME_AUDIT_FILTRI_DELETE);
		SERVLET_AUDIT_FILTRI.add(SERVLET_NAME_AUDIT_FILTRI_LIST);
	}

	public static final String SERVLET_NAME_AUDITING  = OBJECT_NAME_AUDITING +".do";
	public static final String SERVLET_NAME_AUDITING_DEL  = OBJECT_NAME_AUDITING +"Del.do";
	public static final String SERVLET_NAME_AUDITING_LIST  = OBJECT_NAME_AUDITING +"List.do";
	public static final List<String> SERVLET_AUDITING  = new ArrayList<String>();
	static{
		SERVLET_AUDITING .add(SERVLET_NAME_AUDITING );
		SERVLET_AUDITING .add(SERVLET_NAME_AUDITING_LIST );
		SERVLET_AUDITING .add(SERVLET_NAME_AUDITING_DEL );
	}

	public static final String SERVLET_NAME_AUDITING_DETTAGLIO  = OBJECT_NAME_AUDITING_DETTAGLIO +".do";
	public static final String SERVLET_NAME_AUDITING_DETTAGLIO_INFO  = OBJECT_NAME_AUDITING_DETTAGLIO +"Info.do";
	public static final String SERVLET_NAME_AUDITING_DETTAGLIO_DOCUMENTI_BINARI  = OBJECT_NAME_AUDITING_DETTAGLIO +"DocumentiBinari.do";
	public static final List<String> SERVLET_AUDITING_DETTAGLIO  = new ArrayList<String>();
	static{
		SERVLET_AUDITING_DETTAGLIO .add(SERVLET_NAME_AUDITING_DETTAGLIO );
		SERVLET_AUDITING_DETTAGLIO .add(SERVLET_NAME_AUDITING_DETTAGLIO_INFO );
		SERVLET_AUDITING_DETTAGLIO .add(SERVLET_NAME_AUDITING_DETTAGLIO_DOCUMENTI_BINARI );
	}
	


	/* LABEL */

	public static final String LABEL_AUDIT = "Auditing";
	public static final String LABEL_AUDIT_FILTRI = "Filtri";
	public static final String LABEL_AUDIT_FILTRO_GENERICO = "Filtro Generico";
	public static final String LABEL_AUDIT_FILTRO_CONTENUTO = "Filtro per Contenuto";
	public static final String LABEL_AUDIT_DESCRIZIONE = "Descrizione";
	public static final String LABEL_AUDIT_VISUALIZZA = "Visualizza";
	public static final String LABEL_AUDIT_COMPORTAMENTO_DI_DEFAULT = "Comportamento di Default";
	public static final String LABEL_AUDIT_AZIONE = "Azione";
	public static final String LABEL_AUDIT_OPERAZIONI = "Operazioni";
	public static final String LABEL_AUDIT_OPERAZIONE = "Operazione";
	public static final String LABEL_AUDIT_DETTAGLIO = "Dettaglio";
	public static final String LABEL_AUDIT_DETTAGLIO_OPERAZIONE = "Dettaglio Operazione";
	public static final String LABEL_AUDIT_DETTAGLIO_OGGETTO = "Dettaglio Oggetto";
	public static final String LABEL_AUDIT_DOCUMENTI_BINARI = "Documenti Binari";
	public static final String LABEL_AUDIT_MOTIVO_ERRORE = "Motivo errore";
	public static final String LABEL_AUDIT_CONFIGURAZIONE_MODIFICATA = "Configurazione Audit modificata con successo<BR><b>Attenzione:</b> Le modifiche verranno utilizzate dal prossimo accesso alla console";
	public static final String LABEL_AUDIT_CRITERI_RICERCA = "Criteri di Ricerca";
	
	
	/* PARAMETRI */

	public static final String PARAMETRO_AUDIT_FORMATO_DUMP = "formatodump";
	public static final String PARAMETRO_AUDIT_STATO_AUDIT = "statoaudit";
	public static final String PARAMETRO_AUDIT_LOG4J = "log4j";
	public static final String PARAMETRO_AUDIT_DUMP = "dump";
	public static final String PARAMETRO_AUDIT_STATO = "stato";
	public static final String PARAMETRO_AUDIT_UTENTE = "utente";
	public static final String PARAMETRO_AUDIT_TIPO_OPERAZIONE = "tipooperazione";
	public static final String PARAMETRO_AUDIT_TIPO_OGGETTO = "tipooggetto";
	public static final String PARAMETRO_AUDIT_STATO_OPERAZIONE = "statooperazione";
	public static final String PARAMETRO_AUDIT_TIPO_FILTRO = "tipofiltro";
	public static final String PARAMETRO_AUDIT_DUMP_AZIONE = "dumpazione";
	public static final String PARAMETRO_AUDIT_STATO_AZIONE = "statoazione";
	public static final String PARAMETRO_AUDIT_ID = "id";
	public static final String PARAMETRO_AUDIT_OLD_ID = "oldid";
	public static final String PARAMETRO_AUDIT_CONTENUTO_OGGETTO = "contoggetto";
	public static final String PARAMETRO_AUDIT_DATA_INIZIO = "datainizio";
	public static final String PARAMETRO_AUDIT_DATA_FINE = "datafine";
	public static final String PARAMETRO_AUDIT_OPERATION_ID_OP = "idop";
	public static final String PARAMETRO_AUDIT_OPERATION_TIME_EXECUTE = "timeex";
	public static final String PARAMETRO_AUDIT_OPERATION_TIME_REQUEST = "timereq";
	public static final String PARAMETRO_AUDIT_OPERATION_ID_OPERAZIONE = "iddbop";
	public static final String PARAMETRO_AUDIT_OPERATION_TIPO_OPERAZIONE = "tipoop";
	public static final String PARAMETRO_AUDIT_OPERATION_TIPO_OGGETTO = "tipoogg";
	public static final String PARAMETRO_AUDIT_OPERATION_OBJECT_ID = "objid";
	public static final String PARAMETRO_AUDIT_OPERATION_OBJECT_OLD_ID = "objoldid";
	public static final String PARAMETRO_AUDIT_TYPE = "type";
	

	/* LABEL PARAMETRI */

	public static final String LABEL_PARAMETRO_AUDIT_FORMATO_DUMP = "Formato dump";
	public static final String LABEL_PARAMETRO_AUDIT_STATO_AUDIT = "Stato audit";
	public static final String LABEL_PARAMETRO_AUDIT_LOG4J = "Log4j Auditing";
	public static final String LABEL_PARAMETRO_AUDIT_DUMP = "Dump";
	public static final String LABEL_PARAMETRO_AUDIT_STATO = "Stato";
	public static final String LABEL_PARAMETRO_AUDIT_STATO_2 = "Audit";
	public static final String LABEL_PARAMETRO_AUDIT_UTENTE = "Utente";
	public static final String LABEL_PARAMETRO_AUDIT_ID_OPERAZIONE = "Id operazione";
	public static final String LABEL_PARAMETRO_AUDIT_TIPO_OPERAZIONE = "Tipo operazione";
	public static final String LABEL_PARAMETRO_AUDIT_TIPO_OGGETTO = "Tipo oggetto";
	public static final String LABEL_PARAMETRO_AUDIT_STATO_OPERAZIONE = "Stato operazione";
	public static final String LABEL_PARAMETRO_AUDIT_TIPO_FILTRO = "Tipo filtro";
	public static final String LABEL_PARAMETRO_AUDIT_TIPO = "Tipo";
	public static final String LABEL_PARAMETRO_AUDIT_DUMP_AZIONE = "Dump azione";
	public static final String LABEL_PARAMETRO_AUDIT_STATO_AZIONE = "Stato azione";
	public static final String LABEL_PARAMETRO_AUDIT_ID_UPPER_CASE = "ID";
	public static final String LABEL_PARAMETRO_AUDIT_DATA_ESECUZIONE = "Data";
	public static final String LABEL_PARAMETRO_AUDIT_ID = "Identificativo";
	public static final String LABEL_PARAMETRO_AUDIT_OPERAZIONE = "Operazione";
	public static final String LABEL_PARAMETRO_AUDIT_OGGETTO = "Oggetto";
	public static final String LABEL_PARAMETRO_AUDIT_CHECKSUM = "Checksum";
	public static final String LABEL_PARAMETRO_AUDIT_OPERATION_TIME_EXECUTE = "Time execute";
	public static final String LABEL_PARAMETRO_AUDIT_OPERATION_TIME_REQUEST = "Time request";
	public static final String LABEL_PARAMETRO_AUDIT_OPERATION_OLD_ID = "Id precedente alla modifica";
	public static final String LABEL_PARAMETRO_AUDIT_DATA_INIZIO_LABEL = "Inizio intervallo";
	public static final String LABEL_PARAMETRO_AUDIT_DATA_INIZIO_NOTE = "Indicare una data nel formato 'yyyy-MM-dd'";
	public static final String LABEL_PARAMETRO_AUDIT_DATA_FINE_LABEL = "Fine intervallo";
	public static final String LABEL_PARAMETRO_AUDIT_DATA_FINE_NOTE = "Indicare una data nel formato 'yyyy-MM-dd'";
	public static final String LABEL_PARAMETRO_AUDIT_CONTENUTO_OGGETTO = "Contenuto";
	
	
	/* DEFAULT VALUES */

	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_NOME = "log4jAppender";
	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_CLASS_NAME = "org.openspcoop2.web.lib.audit.appender.AuditLog4JAppender";
	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_FILE_CONFIGURAZIONE_NAME = "fileConfigurazione";
	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_FILE_CONFIGURAZIONE_VALUE = "audit.log4j2.properties";
	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_CATEGORY_NAME = "category";
	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_CATEGORY_VALUE = "audit";
	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_XML_NAME = "xml";
	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_XML_VALUE = "true";

	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_NORMALE = "normale";
	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_ESPRESSIONE_REGOLARE = "espressioneRegolare";


	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_TYPE_ERROR = "error";
	public static final String DEFAULT_VALUE_PARAMETRO_AUDIT_TYPE_OGGETTO = "oggetto";

	public static final String DEFAULT_VALUE_ABILITATO = "abilitato";
	public static final String DEFAULT_VALUE_DISABILITATO = "disabilitato";
	
}
