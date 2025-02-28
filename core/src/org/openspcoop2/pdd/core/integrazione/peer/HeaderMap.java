/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.integrazione.peer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
* Classe di utilita, utilizzata per salvare header e 
* ottenere gli headers peer da una lista di header della richiesta http
*
* @author Tommaso Burlon (tommaso.burlon@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class HeaderMap extends HashMap<String, String> {
	private static final long serialVersionUID = 5811004934063754049L;
	
	public static HeaderMap computeFromHeaders(Collection<PeerHeaderDescriptor> descs, Map<String, List<String>> headers) {
		HeaderMap rv = new HeaderMap();
		descs.forEach(desc -> {
			Map<String, String> entry = desc.computeHeaders(headers.keySet());
			if (entry == null)
				return;
			
			entry.forEach((newHdr, oldHdr) -> {
				String value = String.join(",", Objects.requireNonNullElse(headers.get(oldHdr), List.of()));
				rv.put(newHdr, value);
			});
		});
		
		return rv;
	}

}
