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

package org.openspcoop2.message;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openspcoop2.utils.io.DumpByteArrayOutputStream;

/**
 * Registro dei buffer utilizzati per i contenuti dei messaggi, organizzato per transazione.
 *
 * I contenuti che superano la soglia 'org.openspcoop2.pdd.logger.dumpBinario.inMemory.threshold' non vengono
 * mantenuti in memoria ma riversati in un file, che va eliminato al termine dell'elaborazione. Il rilascio
 * avviene normalmente alla serializzazione del messaggio, ma quella non &egrave; garantita: se la transazione si
 * interrompe prima, oppure se il messaggio viene sostituito - come accade quando una validazione fallita fa
 * generare un fault al posto della risposta gi&agrave; costruita - il buffer non &egrave; pi&ugrave; raggiungibile da alcun
 * riferimento e il file sopravvive alla transazione.
 *
 * Registrando i buffer alla loro creazione, il rilascio finale non dipende n&eacute; dalla sorte del messaggio che
 * li ha prodotti n&eacute; dal percorso seguito dall'elaborazione.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageBufferRegistry {

	private MessageBufferRegistry() {}

	private static final Map<String, Set<DumpByteArrayOutputStream>> REGISTRO = new ConcurrentHashMap<>();

	/**
	 * Annota un buffer fra quelli da rilasciare al termine della transazione indicata.
	 * Senza identificativo di transazione il buffer non &egrave; riconducibile ad alcuna elaborazione e non viene
	 * registrato: resta a carico del messaggio che lo ha prodotto.
	 */
	public static void register(String idTransazione, DumpByteArrayOutputStream buffer) {
		if(idTransazione==null || idTransazione.isEmpty() || buffer==null) {
			return;
		}
		REGISTRO.computeIfAbsent(idTransazione,
				k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(buffer);
	}

	/**
	 * Rilascia i buffer della transazione indicata ed elimina la voce dal registro, restituendo il numero di
	 * buffer effettivamente rilasciati. Un buffer gi&agrave; rilasciato dal messaggio che lo deteneva non comporta
	 * alcun effetto: l'eliminazione del file &egrave; idempotente.
	 */
	public static int release(String idTransazione) {
		if(idTransazione==null || idTransazione.isEmpty()) {
			return 0;
		}
		Set<DumpByteArrayOutputStream> buffers = REGISTRO.remove(idTransazione);
		if(buffers==null) {
			return 0;
		}
		int rilasciati = 0;
		for (DumpByteArrayOutputStream buffer : buffers) {
			try {
				buffer.unlock();
				buffer.clearResources();
				rilasciati++;
			} catch(Throwable t) {
				// il rilascio di un buffer non deve impedire quello dei successivi
			}
		}
		return rilasciati;
	}

	/** Numero di transazioni con buffer ancora registrati; utile a diagnosticare accumuli. */
	public static int size() {
		return REGISTRO.size();
	}

}
