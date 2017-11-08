/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
 * @author $Author: mergefairy $
 * @version $Rev: 13269 $, $Date: 2017-09-13 16:11:02 +0200 (Wed, 13 Sep 2017) $
 */
public class APIUtils {

	public static String normalizeResourceName(HttpMethod requestMethod, String path) {
		HttpRequestMethod httpMethodCheck = HttpRequestMethod.valueOf(requestMethod.getValue());
		return normalizeResourceName(httpMethodCheck, path);
	}
	
	public static String normalizeResourceName(HttpRequestMethod requestMethod, String path) {
		List<Character> permit = new ArrayList<Character>();
		permit.add('.');
		permit.add('_');
		permit.add('-');
		Character cJolly = '.'; // uso un carattere come jolly '.'
		
		String nomeAzione = Utilities.convertNameToSistemaOperativoCompatible(path,true,cJolly,permit,false);
		if(nomeAzione.length()>255) {
			nomeAzione = nomeAzione.substring(0, 255);
		}
		return nomeAzione;
	}
	
}
