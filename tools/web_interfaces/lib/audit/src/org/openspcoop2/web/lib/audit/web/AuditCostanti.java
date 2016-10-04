/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.web.lib.audit.web;

import java.util.Vector;

import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * AuditCostanti
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuditCostanti {


	public final static String OBJECT_NAME_CONFIGURAZIONE_AUDITING = "configurazioneAuditing";
	public final static ForwardParams TIPO_OPERAZIONE_CONFIGURAZIONE_AUDITING = ForwardParams.OTHER("");

	public final static String OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI = "configurazioneAuditingFiltri";
	
	public final static String OBJECT_NAME_AUDITING = "auditing";
	public final static ForwardParams TIPO_OPERAZIONE_AUDITING = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_AUDITING_DETTAGLIO = "auditingDettaglio";
	public final static ForwardParams TIPO_OPERAZIONE_AUDITING_DETTAGLIO = ForwardParams.OTHER("");
	public final static ForwardParams TIPO_OPERAZIONE_AUDITING_DETTAGLIO_INFO = ForwardParams.OTHER("Info");
	public final static ForwardParams TIPO_OPERAZIONE_AUDITING_DETTAGLIO_DOCUMENTI_BINARI = ForwardParams.OTHER("DocumentiBinari");

	/* Servlet */

	public final static String SERVLET_NAME_AUDIT  = OBJECT_NAME_CONFIGURAZIONE_AUDITING +".do";
	public final static Vector<String> SERVLET_AUDIT  = new Vector<String>();
	static{
		SERVLET_AUDIT .add(SERVLET_NAME_AUDIT );
	}

	public final static String SERVLET_NAME_AUDIT_FILTRI_ADD = OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI+"Add.do";
	public final static String SERVLET_NAME_AUDIT_FILTRI_CHANGE = OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI+"Change.do";
	public final static String SERVLET_NAME_AUDIT_FILTRI_DELETE = OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI+"Del.do";
	public final static String SERVLET_NAME_AUDIT_FILTRI_LIST = OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI+"List.do";
	public final static Vector<String> SERVLET_AUDIT_FILTRI = new Vector<String>();
	static{
		SERVLET_AUDIT_FILTRI.add(SERVLET_NAME_AUDIT_FILTRI_ADD);
		SERVLET_AUDIT_FILTRI.add(SERVLET_NAME_AUDIT_FILTRI_CHANGE);
		SERVLET_AUDIT_FILTRI.add(SERVLET_NAME_AUDIT_FILTRI_DELETE);
		SERVLET_AUDIT_FILTRI.add(SERVLET_NAME_AUDIT_FILTRI_LIST);
	}

	public final static String SERVLET_NAME_AUDITING  = OBJECT_NAME_AUDITING +".do";
	public final static String SERVLET_NAME_AUDITING_DEL  = OBJECT_NAME_AUDITING +"Del.do";
	public final static String SERVLET_NAME_AUDITING_LIST  = OBJECT_NAME_AUDITING +"List.do";
	public final static Vector<String> SERVLET_AUDITING  = new Vector<String>();
	static{
		SERVLET_AUDITING .add(SERVLET_NAME_AUDITING );
		SERVLET_AUDITING .add(SERVLET_NAME_AUDITING_LIST );
		SERVLET_AUDITING .add(SERVLET_NAME_AUDITING_DEL );
	}

	public final static String SERVLET_NAME_AUDITING_DETTAGLIO  = OBJECT_NAME_AUDITING_DETTAGLIO +".do";
	public final static String SERVLET_NAME_AUDITING_DETTAGLIO_INFO  = OBJECT_NAME_AUDITING_DETTAGLIO +"Info.do";
	public final static String SERVLET_NAME_AUDITING_DETTAGLIO_DOCUMENTI_BINARI  = OBJECT_NAME_AUDITING_DETTAGLIO +"DocumentiBinari.do";
	public final static Vector<String> SERVLET_AUDITING_DETTAGLIO  = new Vector<String>();
	static{
		SERVLET_AUDITING_DETTAGLIO .add(SERVLET_NAME_AUDITING_DETTAGLIO );
		SERVLET_AUDITING_DETTAGLIO .add(SERVLET_NAME_AUDITING_DETTAGLIO_INFO );
		SERVLET_AUDITING_DETTAGLIO .add(SERVLET_NAME_AUDITING_DETTAGLIO_DOCUMENTI_BINARI );
	}
	


	/* LABEL */

	public final static String LABEL_AUDIT = "Auditing";
	public final static String LABEL_AUDIT_FILTRI = "Filtri";
	public final static String LABEL_AUDIT_FILTRO_GENERICO = "Filtro generico";
	public final static String LABEL_AUDIT_FILTRO_CONTENUTO = "Filtro per contenuto";
	public final static String LABEL_AUDIT_DESCRIZIONE = "Descrizione";
	public final static String LABEL_AUDIT_CONFIGURAZIONE = "Configurazione";
	public final static String LABEL_AUDIT_VISUALIZZA = "Visualizza";
	public final static String LABEL_AUDIT_COMPORTAMENTO_DI_DEFAULT = "Comportamento di default";
	public final static String LABEL_AUDIT_AZIONE = "Azione";
	public final static String LABEL_AUDIT_REPORTISTICA = "Reportistica";
	public final static String LABEL_AUDIT_OPERAZIONI = "Operazioni";
	public final static String LABEL_AUDIT_DETTAGLIO = "Dettaglio";
	public final static String LABEL_AUDIT_DOCUMENTI_BINARI = "Documenti binari";
	public final static String LABEL_AUDIT_MOTIVO_ERRORE = "Motivo errore";
	public final static String LABEL_AUDIT_DETTAGLIO_OGGETTO = "Dettaglio oggetto";
	public final static String LABEL_AUDIT_CONFIGURAZIONE_MODIFICATA = "Configurazione Audit modificata con successo<BR><b>Attenzione:</b> Le modifiche verranno utilizzate dal prossimo accesso alla console";

	/* PARAMETRI */

	public final static String PARAMETRO_AUDIT_FORMATO_DUMP = "formatodump";
	public final static String PARAMETRO_AUDIT_STATO_AUDIT = "statoaudit";
	public final static String PARAMETRO_AUDIT_LOG4J = "log4j";
	public final static String PARAMETRO_AUDIT_DUMP = "dump";
	public final static String PARAMETRO_AUDIT_STATO = "stato";
	public final static String PARAMETRO_AUDIT_UTENTE = "utente";
	public final static String PARAMETRO_AUDIT_TIPO_OPERAZIONE = "tipooperazione";
	public final static String PARAMETRO_AUDIT_TIPO_OGGETTO = "tipooggetto";
	public final static String PARAMETRO_AUDIT_STATO_OPERAZIONE = "statooperazione";
	public final static String PARAMETRO_AUDIT_TIPO_FILTRO = "tipofiltro";
	public final static String PARAMETRO_AUDIT_DUMP_AZIONE = "dumpazione";
	public final static String PARAMETRO_AUDIT_STATO_AZIONE = "statoazione";
	public final static String PARAMETRO_AUDIT_ID = "id";
	public final static String PARAMETRO_AUDIT_OLD_ID = "oldid";
	public static final String PARAMETRO_AUDIT_CONTENUTO_OGGETTO = "contoggetto";
	public final static String PARAMETRO_AUDIT_DATA_INIZIO = "datainizio";
	public final static String PARAMETRO_AUDIT_DATA_FINE = "datafine";
	public final static String PARAMETRO_AUDIT_OPERATION_ID_OP = "idop";
	public final static String PARAMETRO_AUDIT_OPERATION_TIME_EXECUTE = "timeex";
	public final static String PARAMETRO_AUDIT_OPERATION_TIME_REQUEST = "timereq";
	public final static String PARAMETRO_AUDIT_OPERATION_TIPO_OPERAZIONE = "tipoop";
	public final static String PARAMETRO_AUDIT_OPERATION_TIPO_OGGETTO = "tipoogg";
	public final static String PARAMETRO_AUDIT_OPERATION_OBJECT_ID = "objid";
	public final static String PARAMETRO_AUDIT_OPERATION_OBJECT_OLD_ID = "objoldid";
	public final static String PARAMETRO_AUDIT_TYPE = "type";
	

	/* LABEL PARAMETRI */

	public final static String LABEL_PARAMETRO_AUDIT_FORMATO_DUMP = "Formato dump";
	public final static String LABEL_PARAMETRO_AUDIT_STATO_AUDIT = "Stato audit";
	public final static String LABEL_PARAMETRO_AUDIT_LOG4J = "Log4j Auditing";
	public final static String LABEL_PARAMETRO_AUDIT_DUMP = "Dump";
	public final static String LABEL_PARAMETRO_AUDIT_STATO = "Stato";
	public final static String LABEL_PARAMETRO_AUDIT_STATO_2 = "Audit";
	public final static String LABEL_PARAMETRO_AUDIT_UTENTE = "Utente";
	public final static String LABEL_PARAMETRO_AUDIT_TIPO_OPERAZIONE = "Tipo operazione";
	public final static String LABEL_PARAMETRO_AUDIT_TIPO_OGGETTO = "Tipo oggetto";
	public final static String LABEL_PARAMETRO_AUDIT_STATO_OPERAZIONE = "Stato operazione";
	public final static String LABEL_PARAMETRO_AUDIT_TIPO_FILTRO = "Tipo filtro";
	public final static String LABEL_PARAMETRO_AUDIT_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_AUDIT_DUMP_AZIONE = "Dump azione";
	public final static String LABEL_PARAMETRO_AUDIT_STATO_AZIONE = "Stato azione";
	public final static String LABEL_PARAMETRO_AUDIT_ID_UPPER_CASE = "ID";
	public final static String LABEL_PARAMETRO_AUDIT_ID = "Id";
	public final static String LABEL_PARAMETRO_AUDIT_OPERAZIONE = "Operazione";
	public final static String LABEL_PARAMETRO_AUDIT_OGGETTO = "Oggetto";
	public final static String LABEL_PARAMETRO_AUDIT_CHECKSUM = "Checksum";
	public final static String LABEL_PARAMETRO_AUDIT_OPERATION_TIME_EXECUTE = "Time execute";
	public final static String LABEL_PARAMETRO_AUDIT_OPERATION_TIME_REQUEST = "Time request";
	public final static String LABEL_PARAMETRO_AUDIT_OPERATION_OLD_ID = "Old id";

	/* DEFAULT VALUES */

	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_NOME = "log4jAppender";
	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_CLASS_NAME = "org.openspcoop2.web.lib.audit.appender.AuditLog4JAppender";
	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_FILE_CONFIGURAZIONE_NAME = "fileConfigurazione";
	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_FILE_CONFIGURAZIONE_VALUE = "audit.log4j2.properties";
	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_CATEGORY_NAME = "category";
	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_CATEGORY_VALUE = "audit";
	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_XML_NAME = "xml";
	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_XML_VALUE = "true";

	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_NORMALE = "normale";
	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_ESPRESSIONE_REGOLARE = "espressioneRegolare";


	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_TYPE_ERROR = "error";
	public final static String DEFAULT_VALUE_PARAMETRO_AUDIT_TYPE_OGGETTO = "oggetto";

	public final static String DEFAULT_VALUE_ABILITATO = "abilitato";
	public final static String DEFAULT_VALUE_DISABILITATO = "disabilitato";
	
}
