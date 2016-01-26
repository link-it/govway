/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.generic_project.web.impl.jsf1.taglib.functions;


import org.apache.commons.lang.StringUtils;

/***
 * Classe utils per le funzionalita' della taglib
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class TaglibUtils {

	/**
	 * Concatena la stringa a con la stringa b
	 * @param a
	 * @param b
	 * @return stringhe concatenate
	 */
	public static String concat(String a, String b) {
		return StringUtils.join(new String[]{a,b},null); 
	}

	/**
	 * Concatena le stringhe presenti nell'array in input utilizzando come separatore
	 * il carattere indicato, se il separatore e' null allora le stringhe saranno concatenate senza separatore
	 * @param strings
	 * @param separator
	 * @return stringhe concatenate
	 */
	public static String concat(String[] strings, String separator){
		return StringUtils.join(strings,separator);
	}

}
