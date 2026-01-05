/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Classe che serve a descrivere un headers peer di tipo string
 * 
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StringPeerHeaderDescriptor implements PeerHeaderDescriptor {
	private final Map<String, Integer> headerMap;
	private final String headerName;
	
	public StringPeerHeaderDescriptor(String key, String value) {
		this.headerName = key;
		
		List<String> headerList = Arrays.stream(value.split(","))
				.map(String::trim)
				.collect(Collectors.toList());
		this.headerMap = IntStream.range(0, headerList.size())
				.boxed()
				.collect(Collectors.toMap(headerList::get, i -> i, Math::min, HashMap::new));
	}
	
	@Override
	public Map<String, String> computeHeaders(Collection<String> headers) {
		Integer bestMatch = this.headerMap.size();
		String name = this.headerName;
		String match = null;
		for (String header : headers) {
			Integer priority = this.headerMap.get(header);
			
			if (priority != null && priority < bestMatch) {
				bestMatch = priority;
				match = header;
			}
		}
		
		if (match == null)
			return Map.of();
		return Map.of(name, match);
	}

}
