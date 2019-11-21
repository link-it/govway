/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
package org.openspcoop2.pdd.core.behaviour.conditional;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;

/**
 * Costanti
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti  {
	
	public static final String CONDITIONAL_ENABLED = "c_enabled"; // true/false
	
	public static final String CONDITIONAL_BY_FILTER = "c_by_filter"; // true/false
	
	public static final String CONDITIONAL_TIPO_SELETTORE = "c_selettore";
	public static final String CONDITIONAL_STATIC_INFO = "c_static_info";
	public static final String CONDITIONAL_PATTERN = "c_pattern";
	public static final String CONDITIONAL_PREFIX = "c_prefix";
	public static final String CONDITIONAL_SUFFIX = "c_suffix";
	
	public static final String CONDITIONAL_GROUP = CostantiConfigurazione.BEHAVIOUR_CONDITIONAL_GROUP;
	public static final String CONDITIONAL_GROUP_NAME = CostantiConfigurazione.BEHAVIOUR_CONDITIONAL_GROUP_NAME;
	public static final String CONDITIONAL_GROUP_ACTION_NAME = CostantiConfigurazione.BEHAVIOUR_CONDITIONAL_GROUP_ACTION_NAME; // alla fine avremo 1 proprieta per ogni azione ed e' più facile fare i vari controlli inUse.
	// Esempi:
	/*
	 * c_group_1_name=Prova Gruppo 1
	 * c_group_1_c_by_filter=true
	 * c_group_1_action_1=deleteStato
	 * c_group_1_action_2=updateStato
	 * 
	 * c_group_2_name=Prova Gruppo 2
	 * c_group_2_c_by_filter=false
	 * c_group_2_action_1=create
	 * c_group_2_action_2=get
	 * c_group_2_action_3=read
	 * */
	
	public static final String CONDITIONAL_ABORT_TRANSACTION = "c_abort"; // true/false
	public static final String CONDITIONAL_EMIT_DIAGNOSTIC_INFO = "c_diag_info"; // true/false
	public static final String CONDITIONAL_EMIT_DIAGNOSTIC_ERROR = "c_diag_error"; // true/false
	public static final String CONDITIONAL_NOME_CONNETTORE = "c_connettore";
	
	public static final String CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA = "c_selector_not_found_";
	public static final String CONDITIONAL_NESSUN_CONNETTORE_TROVATO = "c_connector_not_found_";
	
	
}
