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
