package org.openspcoop2.pdd.core.integrazione.peer;

import java.util.Collection;
import java.util.Map;

/**
 * Interfaccia utilizzata a descrivere un headers peer
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface PeerHeaderDescriptor {
	
	/**
	 * Trasforma una collezione di header HTTP in una mappa nome-valore, 
	 * applicando una mappatura specifica per rinominare alcuni header.
	 * e.g. GovWay-Peer-Transaction-ID: GovWay-Transaction-ID
	 * @param nome headers di una richiesta
	 * @return mappa di associazioni headers peer -> headers richiesta
	 */
	Map<String, String> computeHeaders(Collection<String> headers); 
}
