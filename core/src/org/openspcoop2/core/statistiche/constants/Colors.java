/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

	public final static String CODE_FAULT_APPLICATIVO = org.openspcoop2.core.transazioni.constants.Colors.CODE_FAULT_APPLICATIVO;
	public final static String CODE_ERROR = org.openspcoop2.core.transazioni.constants.Colors.CODE_ERROR;
	public final static String CODE_OK = org.openspcoop2.core.transazioni.constants.Colors.CODE_OK;
	public final static String CODE_TOTALE = "3B83B7";
	public final static String CODE_BANDA_COMPLESSIVA = "3B83B7";
	public final static String CODE_BANDA_INTERNA = "FF8F52";
	public final static String CODE_BANDA_ESTERNA = "95B964";
	public final static String CODE_LATENZA_TOTALE = "3B83B7";
	public final static String CODE_LATENZA_SERVIZIO = "FF8F52";
	public final static String CODE_LATENZA_PORTA = "95B964";
	
	public final static String CSS_COLOR_FAULT_APPLICATIVO = org.openspcoop2.core.transazioni.constants.Colors.CSS_COLOR_FAULT_APPLICATIVO;
	public final static String CSS_COLOR_ERROR = org.openspcoop2.core.transazioni.constants.Colors.CSS_COLOR_ERROR;
	public final static String CSS_COLOR_OK = org.openspcoop2.core.transazioni.constants.Colors.CSS_COLOR_OK;
	public final static String CSS_COLOR_TOTALE = "#"+CODE_TOTALE;
	public final static String CSS_COLOR_BANDA_COMPLESSIVA = "#"+ CODE_BANDA_COMPLESSIVA;
	public final static String CSS_COLOR_BANDA_INTERNA = "#"+ CODE_BANDA_INTERNA;
	public final static String CSS_COLOR_BANDA_ESTERNA = "#"+ CODE_BANDA_ESTERNA;
	public final static String CSS_COLOR_LATENZA_TOTALE = "#"+CODE_LATENZA_TOTALE;
	public final static String CSS_COLOR_LATENZA_SERVIZIO = "#"+CODE_LATENZA_SERVIZIO;
	public final static String CSS_COLOR_LATENZA_PORTA = "#"+CODE_LATENZA_PORTA;
	
	public final static Color COLOR_FAULT_APPLICATIVO = org.openspcoop2.core.transazioni.constants.Colors.COLOR_FAULT_APPLICATIVO;
	public final static Color COLOR_ERROR = org.openspcoop2.core.transazioni.constants.Colors.COLOR_ERROR;
	public final static Color COLOR_OK = org.openspcoop2.core.transazioni.constants.Colors.COLOR_OK;
	public final static Color COLOR_TOTALE = Color.decode(CSS_COLOR_TOTALE);
	public final static Color COLOR_LATENZA_TOTALE = Color.decode(CSS_COLOR_LATENZA_TOTALE);
	public final static Color COLOR_LATENZA_SERVIZIO = Color.decode(CSS_COLOR_LATENZA_SERVIZIO);
	public final static Color COLOR_LATENZA_PORTA = Color.decode(CSS_COLOR_LATENZA_PORTA);
	
}
