/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.autorizzazione;

/**
 * XACMLCostanti
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiAutorizzazione {

	public static final String AUTORIZZAZIONE_CONTENUTO_BUILT_IN = "builtIn";
	
	public static final String AUTHZ_ANY_VALUE = "${anyValue}";
	public static final String AUTHZ_REGEXP_MATCH_PREFIX = "${regExpMatch:";
	public static final String AUTHZ_REGEXP_FIND_PREFIX = "${regExpFind:";
	public static final String AUTHZ_REGEXP_SUFFIX = "}";
}
