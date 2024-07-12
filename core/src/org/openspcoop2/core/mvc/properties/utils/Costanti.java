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


package org.openspcoop2.core.mvc.properties.utils;

import org.openspcoop2.core.constants.CostantiProprieta;

/**     
 * Costanti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	private Costanti() {}

	/** MVC PROPERTIES **/
	
	public static final String PRE_KEY_PROPERTIES_DEFAULT = "___";
	
	public static final String KEY_PROPERTIES_CUSTOM_SEPARATOR = CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR;
	
	public static final String KEY_PROPERTIES_DEFAULT_SEPARATOR = CostantiProprieta.KEY_PROPERTIES_DEFAULT_SEPARATOR;
	
	public static final String NOME_MAPPA_PROPERTIES_DEFAULT = PRE_KEY_PROPERTIES_DEFAULT + "defaultMap";
}
