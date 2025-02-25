package org.openspcoop2.pdd.core.integrazione.peer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.cache.CacheType;
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
	private static Cache compiledCache;
	private static final Semaphore lockCache = new Semaphore("PeerHeaderPatternLock");
	public static void initCache(CacheType type, int cacheSize) throws UtilsException {
		compiledCache = new Cache(type, "PeerHeaderPatternCache");
		compiledCache.setCacheSize(cacheSize);
		compiledCache.setCacheAlgoritm(CacheAlgorithm.LRU);
	}
	
	
	public RegexpPeerHeaderDescriptor(String key, String value) {
		this.headerName = key;
		
		if (compiledCache != null) {
			
			// cerco prima il valore del pattern nella cache
			this.pattern = (Pattern) compiledCache.get(value);
			if (this.pattern == null) {
				try {
					
					// se il valore non e' in cache entro in un blocco sincronizzato
					SemaphoreLock lock = lockCache.acquire("RegexpPeerHeaderDescriptor");
					try {
						this.pattern = (Pattern) compiledCache.get(value);
						if (this.pattern == null) {
							this.pattern = RegularExpressionEngine.createPatternEngine("^" + value + "$");
							compiledCache.put(value, this.pattern);
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
