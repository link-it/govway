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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.regexp.RegExpException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;


/**
 * Classe utilizzata per descrivere un headers peer di tipo regexp
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegexpPeerHeaderDescriptor implements PeerHeaderDescriptor {
	private Pattern pattern = null;
	private String headerName;
	
	
	// cache utilizzata per non dover ricompilare i pattern ad ogni richiesta
	private static ConcurrentMap<String, Pattern> compiledCache;
	private static Queue<String> lruRecord;
	private static int cacheSize;
	
	private static final Semaphore lockCache = new Semaphore("PeerHeaderPatternLock");
	public static void initCache(int cacheSize) {
		RegexpPeerHeaderDescriptor.cacheSize = cacheSize;
		RegexpPeerHeaderDescriptor.lruRecord = new LinkedList<>();
		RegexpPeerHeaderDescriptor.compiledCache = new ConcurrentHashMap<>();
	}
	
	
	public RegexpPeerHeaderDescriptor(String key, String value) {
		this.headerName = key;
		if (compiledCache != null) {
			
			// cerco prima il valore del pattern nella cache
			this.pattern = compiledCache.get(value);
			if (this.pattern == null) {
				try {
					
					// se il valore non e' in cache entro in un blocco sincronizzato
					SemaphoreLock lock = lockCache.acquire("RegexpPeerHeaderDescriptor");
					try {
						this.pattern = compiledCache.get(value);
						if (this.pattern == null) {
							this.pattern = RegularExpressionEngine.createPatternEngine("^" + value + "$");
							compiledCache.put(value, this.pattern);
							
							if (compiledCache.size() > cacheSize) {
								String firstElement = lruRecord.remove();
								compiledCache.remove(firstElement);
							}
							lruRecord.add(value);
						}
					} finally {
						lockCache.release(lock, "RegexpPeerHeaderDescriptor");
					}
				} catch (UtilsException e) {
					// ignoro l'eccezione il pattern verra compilato successivamente se necessario
				}
			}
		}
		
		// se la cache non e' stata inizializzata o qualocsa e' andato storto lo compilo
		if (this.pattern == null)
			this.pattern = RegularExpressionEngine.createPatternEngine("^" + value + "$");
	}
	
	
	private static final Pattern NAME_PARAM_PATTERN = Pattern.compile("\\$\\{(\\d+)\\}");
	@Override
	public Map<String, String> computeHeaders(Collection<String> headers) {
		Map<String, String> newHdrs = new HashMap<>();
		
		for (String header : headers) {
			try {
				List<String> groups = RegularExpressionEngine.getAllStringMatchPattern(header, this.pattern);
				Matcher m = NAME_PARAM_PATTERN.matcher(this.headerName);
				String name = m.replaceAll(g -> {
					String id = g.group(1);
					int index = Integer.parseInt(id) - 1;
					if (index < 0)
						return header;
					return groups.get(index);
				});
				String value = header;
				newHdrs.put(name, value);
			} catch (RegExpException | RegExpNotFoundException e) {
				// regexp non trovata
			}
		}
		
		return newHdrs;
	}
}
