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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
			consentiAccessoATutti(dirSnapshot, true);

			File snapshot = new File(dirSnapshot, idTransazione.trim() + "." + fase + SNAPSHOT_SUFFIX);
			FileSystemUtilities.writeFile(snapshot, sb.toString().getBytes());
			consentiAccessoATutti(snapshot, false);

		} catch(Exception e) {
			throw new HandlerException("Fotografia del repository dumpBinario non riuscita (fase "+fase+"): "+e.getMessage(), e);
		}
	}


	/**
	 * Rende la voce accessibile a qualunque utenza.
	 *
	 * Il gateway e la testsuite non girano necessariamente con la stessa utenza: su jenkins GovWay e'
	 * avviato da root su un Tomcat configurato con 'SecurityListener.UMASK=0027', che produce file 0640
	 * e directory 0750, mentre i test girano come utente 'jenkins'. Senza questa concessione la suite
	 * non riesce nemmeno ad attraversare la directory, e la fotografia le risulta inesistente.
	 *
	 * Sulla directory serve anche il permesso di esecuzione, che e' quello che ne consente
	 * l'attraversamento, e quello di scrittura, perche' e' la suite ad eliminare le fotografie gia'
	 * verificate.
	 *
	 * Lo stesso accorgimento e' gia' adottato dagli altri plugin della testsuite che scambiano file con
	 * i test, si veda 'plugin.classes.Utilities.writeIdentificativoTest'.
	 */
	private static void consentiAccessoATutti(File f, boolean directory) {
		if(!f.setReadable(true, false)) {
			// ignore: un esito negativo dipende dal file system, e viene segnalato dalla lettura lato test
		}
		if(!f.setWritable(true, false)) {
			// ignore
		}
		if(directory && !f.setExecutable(true, false)) {
			// ignore
		}
	}

	/**
	 * Elenca i file del repository appartenenti alla transazione indicata.
	 *
	 * La scansione utilizza 'File.listFiles' e non 'Files.walk': quest'ultima legge gli attributi di
	 * ciascuna voce dopo averla elencata, percio' un file rilasciato nel frattempo, magari da un'altra
	 * transazione in transito sul medesimo nodo, la farebbe terminare con 'NoSuchFileException'.
	 * L'eccezione risalirebbe fino a 'GestoreHandlers', che la rilancia come 'HandlerException' e
	 * trasformerebbe in errore una transazione altrimenti corretta.
	 */
	private static List<File> find(File repository, String idTransazione) {
		String atteso = "_" + idTransazione.trim().replace('-', '_') + BIN_SUFFIX;
		List<File> trovati = new ArrayList<>();
		raccogli(repository, atteso, trovati);
		trovati.sort(Comparator.comparing(File::getName));
		return trovati;
	}
	private static void raccogli(File dir, String atteso, List<File> trovati) {
		File[] contenuto = dir.listFiles();
		if(contenuto==null) {
			return; // directory inesistente, eliminata durante la scansione o non leggibile
		}
		for (File f : contenuto) {
			if(f.isDirectory()) {
				raccogli(f, atteso, trovati);
			}
			else if(f.getName().endsWith(atteso)) {
				trovati.add(f);
			}
		}
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
