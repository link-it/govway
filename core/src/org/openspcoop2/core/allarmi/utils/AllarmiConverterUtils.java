/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.core.allarmi.utils;

import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.constants.TipoPeriodo;

/**     
 * AllarmiConverterUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiConverterUtils {

	public static int toIntegerValue(StatoAllarme stato) {
		switch (stato) {
		case OK:
			return 0;
		case WARNING:
			return 1;
		case ERROR:
			return 2;
		}
		return -1;
	}
	
	public static StatoAllarme toStatoAllarme(Integer stato) {
		if(stato==null) {
			return null;
		}
		switch (stato) {
		case 0:
			return StatoAllarme.OK;
		case 1:
			return StatoAllarme.WARNING;
		case 2:
			return StatoAllarme.ERROR;
		}
		return null;
	}
	
	
	public static String toValue(TipoPeriodo tipo) {
		return tipo.getValue()+"";
	}
	
	public static TipoPeriodo toTipoPeriodo(String v) {
		return TipoPeriodo.toEnumConstant(v.charAt(0));
	}
	
}
