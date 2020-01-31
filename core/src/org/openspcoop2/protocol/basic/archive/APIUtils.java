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

package org.openspcoop2.protocol.basic.archive;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * APIUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class APIUtils {

	public static String normalizeResourceName(HttpMethod requestMethod, String path) {
		if(requestMethod==null || path==null) {
			return null;
		}
		HttpRequestMethod httpMethodCheck = HttpRequestMethod.valueOf(requestMethod.getValue());
		return normalizeResourceName(httpMethodCheck, path);
	}
	
	public static String normalizeResourceName(HttpRequestMethod requestMethod, String path) {
		
		if(requestMethod==null || path==null) {
			return null;
		}
		
		List<Character> permit = new ArrayList<Character>();
		permit.add('.');
		permit.add('_');
		permit.add('-');
		Character cJolly = '.'; // uso un carattere come jolly '.'
		
		String nome = Utilities.convertNameToSistemaOperativoCompatible(path,true,cJolly,permit,false);
		// Elimino caratteri iniziali
		while(nome.startsWith((cJolly+"")) && nome.length()>1) {
			nome = nome.substring(1);
		}
		// Elimino caratteri finali
		while(nome.endsWith((cJolly+"")) && nome.length()>1) {
			nome = nome.substring(0,nome.length()-1);
		}
		// elimino eventuali caratteri speciali se presenti piu' di una volta consecutivamente
		int count = 0;
		StringBuilder bf = new StringBuilder();
		for (int i = 0; i < nome.length(); i++) {
			char c = nome.charAt(i);
			if(c == cJolly.charValue()) {
				count++;
				if(count==1) {
					bf.append(c);
				}
			}
			else {
				count = 0;
				bf.append(c);
			}
		}
		
		String nomeAzione = requestMethod.name() + "_" + bf.toString();
		
		if(nomeAzione.length()>255) {
			nomeAzione = nomeAzione.substring(0, 255);
		}
		return nomeAzione;
	}
	
}
