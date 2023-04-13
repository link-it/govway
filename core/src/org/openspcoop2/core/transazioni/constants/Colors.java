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

package org.openspcoop2.core.transazioni.constants;

import java.awt.Color;

/**     
 * Colors
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Colors {

	public static final String CODE_FAULT_APPLICATIVO = "FF8F52";
	public static final String CODE_ERROR = "CD4A50";
	public static final String CODE_OK = "95B964";
	
	public static final String CSS_COLOR_FAULT_APPLICATIVO = "#"+CODE_FAULT_APPLICATIVO;
	public static final String CSS_COLOR_ERROR = "#"+CODE_ERROR;
	public static final String CSS_COLOR_OK = "#"+CODE_OK;
	
	public static final Color COLOR_FAULT_APPLICATIVO = Color.decode(CSS_COLOR_FAULT_APPLICATIVO);
	public static final Color COLOR_ERROR = Color.decode(CSS_COLOR_ERROR);
	public static final Color COLOR_OK = Color.decode(CSS_COLOR_OK);
	
}
