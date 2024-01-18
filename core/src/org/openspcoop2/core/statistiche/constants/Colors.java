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

package org.openspcoop2.core.statistiche.constants;

import java.awt.Color;

/**     
 * Colors
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Colors {

	public static final String CODE_FAULT_APPLICATIVO = org.openspcoop2.core.transazioni.constants.Colors.CODE_FAULT_APPLICATIVO;
	public static final String CODE_ERROR = org.openspcoop2.core.transazioni.constants.Colors.CODE_ERROR;
	public static final String CODE_OK = org.openspcoop2.core.transazioni.constants.Colors.CODE_OK;
	public static final String CODE_TOTALE = "3B83B7";
	public static final String CODE_BANDA_COMPLESSIVA = "3B83B7";
	public static final String CODE_BANDA_INTERNA = "FF8F52";
	public static final String CODE_BANDA_ESTERNA = "95B964";
	public static final String CODE_LATENZA_TOTALE = "3B83B7";
	public static final String CODE_LATENZA_SERVIZIO = "FF8F52";
	public static final String CODE_LATENZA_PORTA = "95B964";
	
	public static final String CSS_COLOR_FAULT_APPLICATIVO = org.openspcoop2.core.transazioni.constants.Colors.CSS_COLOR_FAULT_APPLICATIVO;
	public static final String CSS_COLOR_ERROR = org.openspcoop2.core.transazioni.constants.Colors.CSS_COLOR_ERROR;
	public static final String CSS_COLOR_OK = org.openspcoop2.core.transazioni.constants.Colors.CSS_COLOR_OK;
	public static final String CSS_COLOR_TOTALE = "#"+CODE_TOTALE;
	public static final String CSS_COLOR_BANDA_COMPLESSIVA = "#"+ CODE_BANDA_COMPLESSIVA;
	public static final String CSS_COLOR_BANDA_INTERNA = "#"+ CODE_BANDA_INTERNA;
	public static final String CSS_COLOR_BANDA_ESTERNA = "#"+ CODE_BANDA_ESTERNA;
	public static final String CSS_COLOR_LATENZA_TOTALE = "#"+CODE_LATENZA_TOTALE;
	public static final String CSS_COLOR_LATENZA_SERVIZIO = "#"+CODE_LATENZA_SERVIZIO;
	public static final String CSS_COLOR_LATENZA_PORTA = "#"+CODE_LATENZA_PORTA;
	
	public static final Color COLOR_FAULT_APPLICATIVO = org.openspcoop2.core.transazioni.constants.Colors.COLOR_FAULT_APPLICATIVO;
	public static final Color COLOR_ERROR = org.openspcoop2.core.transazioni.constants.Colors.COLOR_ERROR;
	public static final Color COLOR_OK = org.openspcoop2.core.transazioni.constants.Colors.COLOR_OK;
	public static final Color COLOR_TOTALE = Color.decode(CSS_COLOR_TOTALE);
	public static final Color COLOR_LATENZA_TOTALE = Color.decode(CSS_COLOR_LATENZA_TOTALE);
	public static final Color COLOR_LATENZA_SERVIZIO = Color.decode(CSS_COLOR_LATENZA_SERVIZIO);
	public static final Color COLOR_LATENZA_PORTA = Color.decode(CSS_COLOR_LATENZA_PORTA);
	
}
