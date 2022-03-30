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


package org.openspcoop2.web.lib.audit.costanti;

/**
 * Costanti
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Costanti {

	public static final String DUMP_JSON_FORMAT = "JSON";
	public static final String DUMP_XML_FORMAT = "XML";
	
	public static final String DB_AUDIT_CONFIGURAZIONE = "audit_conf";
	public static final String DB_AUDIT_FILTRI = "audit_filters";
	public static final String DB_AUDIT_APPENDER = "audit_appender";
	public static final String DB_AUDIT_APPENDER_PROPERTIES = "audit_appender_prop";
	
	public static final String DB_AUDIT_OPERATIONS_TABLE = "audit_operations";
    public static final String DB_AUDIT_OPERATIONS_TABLE_ID = "id";
    public static final String DB_AUDIT_OPERATIONS_TABLE_SEQUENCE = "seq_audit_operations";
    public static final String DB_AUDIT_OPERATIONS_TABLE_FOR_ID_SEQUENCE = "audit_operations_init_seq";
    
    public static final String DB_AUDIT_BINARIES_TABLE = "audit_binaries";
}
