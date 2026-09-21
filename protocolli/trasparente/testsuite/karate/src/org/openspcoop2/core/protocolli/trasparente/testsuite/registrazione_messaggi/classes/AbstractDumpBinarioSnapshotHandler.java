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

package org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi.classes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* Fotografa il repository di overflow dei buffer dei messaggi
* ('org.openspcoop2.pdd.logger.dumpBinario.msgRepository') mentre la transazione e' ancora in corso.
*
* Serve a rendere verificabile cio' che, dall'esterno, non e' osservabile: al termine della
* transazione il file di overflow deve essere stato eliminato, quindi un test che si limitasse a
* guardare il repository a valle non saprebbe distinguere il rilascio corretto dal caso in cui il
* file non e' mai stato prodotto. Fotografando il repository nel punto in cui il file deve esserci
* si verifica anche il verso opposto: sotto soglia il payload resta in memoria e nessun file deve
* comparire.
*
* L'handler e' inerte se la richiesta non riporta l'header {@link #HEADER_SNAPSHOT_DIR}, con cui il
* test indica la directory in cui depositare la fotografia; puo' quindi essere associato ad una
* porta senza alcun effetto sui test che non lo utilizzano.
*
* @author $Author$
* @version $Rev$, $Date$
*/
public abstract class AbstractDumpBinarioSnapshotHandler {

	/** Header con cui il test indica la directory in cui depositare la fotografia */
	public static final String HEADER_SNAPSHOT_DIR = "govway-testsuite-dumpbinario-snapshotdir";

	public static final String SNAPSHOT_SUFFIX = ".snapshot";

	private static final String BIN_SUFFIX = ".bin";


	protected void snapshot(PdDContext pddContext, String fase) throws HandlerException {

		String dir = getHeader(pddContext, HEADER_SNAPSHOT_DIR);
		if(dir==null || dir.trim().isEmpty()) {
			return; // handler non richiesto da questo test
		}

		String idTransazione = getIdTransazione(pddContext);
		if(idTransazione==null || idTransazione.trim().isEmpty()) {
			throw new HandlerException("Identificativo di transazione non presente nel contesto");
		}

		try {
			File repository = OpenSPCoop2Properties.getInstance().getDumpBinarioRepository();

			List<File> files = find(repository, idTransazione);

			StringBuilder sb = new StringBuilder();
			sb.append("repository=").append(repository.getAbsolutePath()).append("\n");
			sb.append("fase=").append(fase).append("\n");
			sb.append("idTransazione=").append(idTransazione).append("\n");
			sb.append("files=").append(files.size()).append("\n");
			for (int i = 0; i < files.size(); i++) {
				File f = files.get(i);
				sb.append("file.").append(i).append("=").append(f.getAbsolutePath()).append("\n");
				sb.append("size.").append(i).append("=").append(f.length()).append("\n");
			}

			File dirSnapshot = new File(dir.trim());
			FileSystemUtilities.mkdir(dirSnapshot);
			File snapshot = new File(dirSnapshot, idTransazione.trim() + "." + fase + SNAPSHOT_SUFFIX);
			FileSystemUtilities.writeFile(snapshot, sb.toString().getBytes());

		} catch(Exception e) {
			throw new HandlerException("Fotografia del repository dumpBinario non riuscita (fase "+fase+"): "+e.getMessage(), e);
		}
	}


	private static List<File> find(File repository, String idTransazione) throws HandlerException {
		String atteso = "_" + idTransazione.trim().replace('-', '_') + BIN_SUFFIX;
		List<File> trovati = new ArrayList<>();
		if(!repository.exists()) {
			return trovati;
		}
		try (Stream<Path> stream = Files.walk(repository.toPath())) {
			stream.filter(Files::isRegularFile)
				.map(Path::toFile)
				.filter(f -> f.getName().endsWith(atteso))
				.sorted(Comparator.comparing(File::getName))
				.forEach(trovati::add);
		} catch(Exception e) {
			throw new HandlerException("Scansione del repository '"+repository.getAbsolutePath()+"' non riuscita: "+e.getMessage(), e);
		}
		return trovati;
	}


	protected static String getHeader(PdDContext pddContext, String nome) {
		if(pddContext==null || !pddContext.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
			return null;
		}
		RequestInfo requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		if(requestInfo==null || requestInfo.getProtocolContext()==null) {
			return null;
		}
		return requestInfo.getProtocolContext().getHeaderFirstValue(nome);
	}

	protected static String getIdTransazione(PdDContext pddContext) {
		if(pddContext==null || !pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
			return null;
		}
		return (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
	}

}
