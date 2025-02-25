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
