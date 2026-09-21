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


package org.openspcoop2.core.protocolli.trasparente.testsuite;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**
 * Utility per la verifica del repository di overflow dei buffer dei messaggi
 * (proprieta' di govway 'org.openspcoop2.pdd.logger.dumpBinario.msgRepository').
 *
 * I payload che superano la soglia 'org.openspcoop2.pdd.logger.dumpBinario.inMemory.threshold'
 * non vengono mantenuti in memoria ma riversati in un file temporaneo dentro tale repository;
 * il file deve essere eliminato al termine della transazione. Un file che sopravvive alla
 * transazione e' un rilascio mancato.
 *
 * Il nome del file prodotto da 'DumpByteArrayOutputStream' e':
 *   &lt;repository&gt;/&lt;yyyyMMdd&gt;/dump&lt;tipoMessaggio&gt;_&lt;yyyyMMdd_HHmmssSSS&gt;_&lt;idTransazione&gt;.bin
 * dove nell'identificativo di transazione i '-' sono sostituiti da '_'. La ricerca viene quindi
 * filtrata sulla singola transazione, in modo che la verifica resti valida anche quando altri
 * test stanno transitando messaggi sullo stesso gateway.
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpBinarioUtils {

	private DumpBinarioUtils() {}

	/** Property, obbligatoria, che indica il repository configurato sul gateway sotto test */
	public static final String PROPERTY_REPOSITORY = "dumpBinario.repository";

	/** Numero di tentativi e attesa fra un tentativo e l'altro, per dare tempo al rilascio delle risorse */
	public static final String PROPERTY_CHECK_RETRY = "dumpBinario.check.retry";
	public static final String PROPERTY_CHECK_DELAY = "dumpBinario.check.delay";

	private static final int DEFAULT_CHECK_RETRY = 5;
	private static final long DEFAULT_CHECK_DELAY = 400;

	private static final String SUFFIX = ".bin";

	/** Millisecondi oltre i quali il presidio globale smette di attendere e segnala i file ancora presenti */
	public static final String PROPERTY_CHECK_GLOBALE_TIMEOUT = "dumpBinario.check.globale.timeoutMs";

	private static final int DEFAULT_CHECK_GLOBALE_TIMEOUT = 10000;




	/**
	 * Ritorna il repository configurato, verificandone i prerequisiti.
	 *
	 * L'accessibilita' del repository e' un prerequisito dei test che lo utilizzano: se la property
	 * non e' valorizzata, oppure il path non esiste, non e' una directory o non e' leggibile, il test
	 * fallisce. Non viene effettuato alcuno skip: un test che non e' in grado di osservare il
	 * repository non e' un test superato.
	 */
	public static File getRepository() {
		String dir = System.getProperty(PROPERTY_REPOSITORY);
		if(dir==null || dir.trim().isEmpty()) {
			fail("Property '"+PROPERTY_REPOSITORY+"' non definita in "+ConfigLoader.propFileName+
					"; indicare il repository configurato sul gateway nella proprieta' di govway "+
					"'org.openspcoop2.pdd.logger.dumpBinario.msgRepository' (attenzione: il valore effettivo puo' differire "+
					"da quello indicato nel file di configurazione, per via della risoluzione delle variabili d'ambiente e "+
					"della strategia di cluster id applicata ai path di log)");
		}
		File f = new File(dir.trim());
		if(!f.exists()) {
			fail("Repository dumpBinario '"+f.getAbsolutePath()+"' non esistente (property '"+PROPERTY_REPOSITORY+"')");
		}
		if(!f.isDirectory()) {
			fail("Repository dumpBinario '"+f.getAbsolutePath()+"' non e' una directory (property '"+PROPERTY_REPOSITORY+"')");
		}
		if(!f.canRead()) {
			fail("Repository dumpBinario '"+f.getAbsolutePath()+"' non leggibile (property '"+PROPERTY_REPOSITORY+"')");
		}
		return f;
	}


	/**
	 * Repository configurato, oppure {@code null} se la property non e' valorizzata.
	 *
	 * A differenza di {@link #getRepository()} non fa fallire il test: e' pensato per il presidio globale,
	 * che deve poter restare inerte sugli ambienti in cui il repository non e' stato indicato.
	 */
	private static File getRepositorySeConfigurato() {
		String dir = System.getProperty(PROPERTY_REPOSITORY);
		if(dir==null || dir.trim().isEmpty()) {
			return null;
		}
		File f = new File(dir.trim());
		return (f.exists() && f.isDirectory() && f.canRead()) ? f : null;
	}


	/**
	 * Presidio globale sul repository di overflow: verifica che l'esecuzione non abbia lasciato file dietro
	 * di se'.
	 *
	 * A differenza di {@link #verifyNoResidui(Logger, String)} non richiede di conoscere l'identificativo
	 * della transazione, percio' puo' essere applicato indistintamente a qualunque classe di test e
	 * intercetta anche gli scenari che nessuna verifica puntuale copre.
	 *
	 * Sono considerati solo i file prodotti dopo l'istante indicato, cosi' da ignorare i residui di
	 * esecuzioni precedenti e non pretendere un repository pulito in partenza. Un file ancora in uso da
	 * una elaborazione non conclusa viene escluso non per anzianita' ma attendendo: la verifica viene
	 * ripetuta finche' il repository non risulta pulito, e solo al termine dei tentativi segnala.
	 */
	public static void verifyNessunResiduoRecente(Logger log, long istanteAvvio) {

		File repository = getRepositorySeConfigurato();
		if(repository==null) {
			return; // presidio non applicabile su questo ambiente
		}

		long timeout = getIntProperty(PROPERTY_CHECK_GLOBALE_TIMEOUT, DEFAULT_CHECK_GLOBALE_TIMEOUT);
		long delay = getIntProperty(PROPERTY_CHECK_DELAY, (int)DEFAULT_CHECK_DELAY);
		long scadenza = System.currentTimeMillis() + timeout;

		// attesa attiva, ma limitata: un file ancora in uso sparisce nel giro di poco, uno non rilasciato
		// resterebbe per sempre e non ha senso continuare ad aspettarlo
		List<File> residui = elencaProdottiDopo(repository, istanteAvvio);
		while(!residui.isEmpty() && System.currentTimeMillis() < scadenza) {
			Utilities.sleep(delay);
			residui = elencaProdottiDopo(repository, istanteAvvio);
		}

		if(residui.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Repository dumpBinario: l'esecuzione ha lasciato ").append(residui.size())
			.append(" file non eliminati.\n\n");
		sb.append("COSA SIGNIFICA\n");
		sb.append("  Un file in '").append(repository.getAbsolutePath()).append("' e' la copia su file system di un\n");
		sb.append("  payload che ha superato la soglia 'org.openspcoop2.pdd.logger.dumpBinario.inMemory.threshold'.\n");
		sb.append("  Deve essere eliminato al termine della transazione che lo ha prodotto: la sua presenza indica\n");
		sb.append("  che il buffer del messaggio non e' stato rilasciato.\n\n");
		sb.append("COSA E' STATO CONSIDERATO\n");
		sb.append("  Solo i file prodotti dopo l'avvio di questa classe di test (").append(new java.util.Date(istanteAvvio))
			.append("),\n");
		sb.append("  cosi' da escludere i residui di esecuzioni precedenti. La verifica e' stata ripetuta ogni ")
			.append(delay).append(" ms\n");
		sb.append("  per ").append(timeout).append(" ms complessivi (property '").append(PROPERTY_CHECK_GLOBALE_TIMEOUT)
			.append("'): i file elencati\n");
		sb.append("  non sono quindi in uso da una elaborazione ancora in corso.\n\n");
		sb.append("FILE RILEVATI\n");
		for (File f : residui) {
			sb.append("  ").append(f.getAbsolutePath()).append("\n");
			sb.append("      dimensione ").append(f.length()).append(" bytes");
			String[] info = descriviDalNome(f.getName());
			if(info!=null) {
				sb.append(", transazione ").append(info[1]).append(", contenuto ").append(info[0]);
			}
			sb.append("\n");
		}
		String msg = sb.toString();
		if(log!=null) {
			log.error(msg);
		}
		fail(msg);
	}

	private static List<File> elencaProdottiDopo(File repository, long istante) {
		return elenca(repository, f -> f.getName().endsWith(SUFFIX) && f.lastModified() >= istante);
	}

	/**
	 * Elenca ricorsivamente i file del repository che soddisfano il criterio indicato.
	 *
	 * La scansione utilizza 'File.listFiles' e non 'Files.walk': quest'ultima legge gli attributi di
	 * ciascuna voce dopo averla elencata, e un file che il gateway rilascia nel frattempo la fa terminare
	 * con 'NoSuchFileException'. Un file che sparisce durante la scansione non e' un errore ma esattamente
	 * il comportamento atteso, e non deve far fallire la verifica.
	 */
	private static List<File> elenca(File repository, Predicate<File> criterio) {
		List<File> trovati = new ArrayList<>();
		raccogli(repository, criterio, trovati);
		trovati.sort(Comparator.comparing(File::getName));
		return trovati;
	}
	private static void raccogli(File dir, Predicate<File> criterio, List<File> trovati) {
		File[] contenuto = dir.listFiles();
		if(contenuto==null) {
			return; // directory eliminata o non leggibile durante la scansione
		}
		for (File f : contenuto) {
			if(f.isDirectory()) {
				raccogli(f, criterio, trovati);
			}
			else if(criterio.test(f)) {
				trovati.add(f);
			}
		}
	}

	/**
	 * Ricava tipo di contenuto e identificativo di transazione dal nome del file, che li riporta entrambi
	 * nella forma 'dump&lt;tipo&gt;_&lt;timestamp&gt;_&lt;idTransazione&gt;.bin', con i trattini sostituiti da underscore.
	 */
	private static String[] descriviDalNome(String nome) {
		if(!nome.startsWith("dump") || !nome.endsWith(SUFFIX)) {
			return null;
		}
		String corpo = nome.substring("dump".length(), nome.length()-SUFFIX.length());
		int i = corpo.indexOf('_');
		if(i<0) {
			return null;
		}
		String tipo = corpo.substring(0, i);
		String resto = corpo.substring(i+1);
		// il timestamp occupa i primi due gruppi, l'identificativo di transazione i cinque successivi
		String[] gruppi = resto.split("_");
		if(gruppi.length < 7) {
			return null;
		}
		StringBuilder id = new StringBuilder();
		for (int g = 2; g < gruppi.length; g++) {
			if(g>2) {
				id.append("-");
			}
			id.append(gruppi[g]);
		}
		return new String[] { tipo, id.toString() };
	}


	/**
	 * Ritorna i file di overflow presenti nel repository per la transazione indicata.
	 * La scansione e' ricorsiva su tutte le directory giornaliere, poiche' una transazione a cavallo
	 * della mezzanotte puo' avere prodotto file in giornate differenti.
	 */
	public static List<File> getFiles(String idTransazione) {
		if(idTransazione==null || idTransazione.trim().isEmpty()) {
			fail("Identificativo di transazione non fornito; impossibile verificare il repository dumpBinario");
		}
		File repository = getRepository();
		String atteso = "_" + idTransazione.trim().replace('-', '_') + SUFFIX;
		return elenca(repository, f -> f.getName().endsWith(atteso));
	}


	/**
	 * Verifica che la transazione indicata non abbia lasciato file nel repository.
	 *
	 * Il rilascio avviene alla serializzazione del messaggio, quindi al ritorno della risposta il file
	 * dovrebbe gia' essere stato eliminato; viene comunque effettuato qualche tentativo, per non
	 * dipendere dall'istante esatto in cui il gateway chiude la transazione.
	 */
	public static void verifyNoResidui(Logger log, String idTransazione) {
		verifyNoResidui(log, idTransazione, null);
	}

	/**
	 * Variante che consente di indicare il contesto (es. il caso di test) nel messaggio di errore.
	 */
	public static void verifyNoResidui(Logger log, String idTransazione, String contesto) {

		int retry = getIntProperty(PROPERTY_CHECK_RETRY, DEFAULT_CHECK_RETRY);
		long delay = getIntProperty(PROPERTY_CHECK_DELAY, (int)DEFAULT_CHECK_DELAY);

		List<File> residui = getFiles(idTransazione);
		for (int i = 0; i < retry && !residui.isEmpty(); i++) {
			Utilities.sleep(delay);
			residui = getFiles(idTransazione);
		}

		if(residui.isEmpty()) {
			if(log!=null) {
				log.debug("Repository dumpBinario: nessun residuo per la transazione [{}]", idTransazione);
			}
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Repository dumpBinario: rilevati ").append(residui.size())
			.append(" file non eliminati per la transazione [").append(idTransazione).append("]");
		if(contesto!=null && !contesto.trim().isEmpty()) {
			sb.append(" (").append(contesto.trim()).append(")");
		}
		for (File f : residui) {
			sb.append("\n\t").append(f.getAbsolutePath()).append(" (").append(f.length()).append(" bytes)");
		}
		String msg = sb.toString();
		if(log!=null) {
			log.error(msg);
		}
		fail(msg);
	}


	/**
	 * Elimina gli eventuali file lasciati dalla transazione indicata, ritornando quanti ne sono stati
	 * eliminati. Da utilizzare esclusivamente per bonificare l'ambiente fra un'esecuzione e l'altra,
	 * mai al posto della verifica.
	 */
	public static int deleteResidui(Logger log, String idTransazione) {
		List<File> residui = getFiles(idTransazione);
		int eliminati = 0;
		for (File f : residui) {
			if(f.delete()) {
				eliminati++;
			}
			else if(log!=null) {
				log.warn("Repository dumpBinario: eliminazione di '{}' non riuscita", f.getAbsolutePath());
			}
		}
		return eliminati;
	}


	// ------------------------------------------------------------------------------------------
	// Fotografie prodotte dall'handler 'DumpBinarioSnapshot*Handler' a transazione ancora in corso.
	//
	// Verificare a valle che il repository sia pulito non basta: un repository pulito e' anche cio'
	// che si osserva quando il file non e' mai stato prodotto. Le fotografie scattate dall'handler
	// dicono invece se il payload e' effettivamente finito su file system, e consentono quindi la
	// verifica nei due versi: sopra soglia il file deve esserci, sotto soglia no.
	// ------------------------------------------------------------------------------------------

	/** Header con cui il test indica all'handler dove depositare le fotografie */
	public static final String HEADER_SNAPSHOT_DIR = "govway-testsuite-dumpbinario-snapshotdir";

	/** Directory in cui l'handler deposita le fotografie */
	public static final String PROPERTY_SNAPSHOT_DIR = "dumpBinario.snapshot.dir";

	/** Ore oltre le quali una fotografia e' considerata residuo di esecuzioni precedenti */
	public static final String PROPERTY_SNAPSHOT_RETENTION_HOURS = "dumpBinario.snapshot.retention.hours";

	private static final int DEFAULT_SNAPSHOT_RETENTION_HOURS = 24;

	public static final String FASE_OUT_REQUEST = "outRequest";
	public static final String FASE_OUT_RESPONSE = "outResponse";

	private static final String SNAPSHOT_SUFFIX = ".snapshot";


	/**
	 * Directory in cui l'handler deposita le fotografie. Come per il repository, l'accessibilita' e'
	 * un prerequisito: se la property non e' valorizzata il test fallisce.
	 */
	public static File getSnapshotDir() {
		String dir = System.getProperty(PROPERTY_SNAPSHOT_DIR);
		if(dir==null || dir.trim().isEmpty()) {
			fail("Property '"+PROPERTY_SNAPSHOT_DIR+"' non definita in "+ConfigLoader.propFileName+
					"; indicare la directory in cui l'handler deposita le fotografie del repository dumpBinario");
		}
		return new File(dir.trim());
	}

	/**
	 * Valore da riportare nell'header {@link #HEADER_SNAPSHOT_DIR} della richiesta, per attivare
	 * l'handler sulla singola invocazione. Senza questo header l'handler resta inerte.
	 */
	public static String getSnapshotDirHeaderValue() {
		return getSnapshotDir().getAbsolutePath();
	}


	/**
	 * Verifica che nella fase indicata il payload sia effettivamente finito su file system.
	 * Da utilizzare nei test con payload oltre la soglia: senza questa verifica, l'assenza di residui
	 * potrebbe dipendere dal fatto che non e' mai stato prodotto alcun file.
	 */
	public static void verifySpill(Logger log, String idTransazione, String fase) {
		int files = readNumeroFile(idTransazione, fase);
		if(files<=0) {
			fail("Repository dumpBinario: nella fase '"+fase+"' della transazione ["+idTransazione+
					"] non risulta prodotto alcun file, mentre il payload supera la soglia configurata "+
					"('org.openspcoop2.pdd.logger.dumpBinario.inMemory.threshold')");
		}
		if(log!=null) {
			log.debug("Repository dumpBinario: nella fase '{}' della transazione [{}] risultano {} file", fase, idTransazione, files);
		}
	}

	/**
	 * Verifica che nella fase indicata il payload sia rimasto in memoria, senza produrre alcun file.
	 * Da utilizzare nei test con payload sotto la soglia.
	 */
	public static void verifyNessunSpill(Logger log, String idTransazione, String fase) {
		int files = readNumeroFile(idTransazione, fase);
		if(files>0) {
			fail("Repository dumpBinario: nella fase '"+fase+"' della transazione ["+idTransazione+
					"] risultano prodotti "+files+" file, mentre il payload e' inferiore alla soglia configurata "+
					"('org.openspcoop2.pdd.logger.dumpBinario.inMemory.threshold') e dovrebbe restare in memoria"+
					leggiDettaglioFile(idTransazione, fase));
		}
		if(log!=null) {
			log.debug("Repository dumpBinario: nella fase '{}' della transazione [{}] nessun file prodotto", fase, idTransazione);
		}
	}


	/**
	 * Ritorna la fotografia scattata dall'handler, verificando che sia stata effettivamente prodotta;
	 * la sua assenza significa che l'handler non e' associato alla porta oppure che l'header
	 * {@link #HEADER_SNAPSHOT_DIR} non e' stato inviato.
	 */
	public static Properties readSnapshot(String idTransazione, String fase) {
		File f = new File(getSnapshotDir(), idTransazione + "." + fase + SNAPSHOT_SUFFIX);
		if(!f.exists()) {
			fail("Fotografia del repository dumpBinario non prodotta per la fase '"+fase+"' della transazione ["+idTransazione+
					"]: file '"+f.getAbsolutePath()+"' non presente. Verificare che l'handler sia associato alla porta e che la "+
					"richiesta riporti l'header '"+HEADER_SNAPSHOT_DIR+"'");
		}
		Properties p = new Properties();
		try (java.io.InputStream is = java.nio.file.Files.newInputStream(f.toPath())) {
			p.load(is);
		} catch(IOException e) {
			fail("Lettura della fotografia '"+f.getAbsolutePath()+"' non riuscita: "+e.getMessage());
		}
		return p;
	}

	/**
	 * Elimina le fotografie della transazione indicata, per non lasciare residui fra un test e l'altro.
	 */
	public static void deleteSnapshot(String idTransazione) {
		File dir = getSnapshotDir();
		if(!dir.exists()) {
			return;
		}
		File[] files = dir.listFiles((d, name) -> name.startsWith(idTransazione + ".") && name.endsWith(SNAPSHOT_SUFFIX));
		if(files!=null) {
			for (File f : files) {
				if(!f.delete() && f.exists()) {
					f.deleteOnExit();
				}
			}
		}
	}


	/**
	 * Verifica il numero esatto di file presenti nella fase indicata. Utile per distinguere il
	 * rilascio tempestivo dal rilascio mancato: sul percorso corretto il file della richiesta non e'
	 * piu' presente quando la risposta viene serializzata, mentre in presenza del difetto sopravvive.
	 */
	public static void verifyNumeroFile(Logger log, String idTransazione, String fase, int atteso) {
		int files = readNumeroFile(idTransazione, fase);
		if(files!=atteso) {
			fail("Repository dumpBinario: nella fase '"+fase+"' della transazione ["+idTransazione+
					"] risultano "+files+" file, attesi "+atteso+leggiDettaglioFile(idTransazione, fase));
		}
		if(log!=null) {
			log.debug("Repository dumpBinario: nella fase '{}' della transazione [{}] risultano {} file, come atteso", fase, idTransazione, files);
		}
	}


	/**
	 * Elimina le fotografie residue di esecuzioni precedenti, cioe' quelle piu' vecchie della finestra
	 * indicata da {@link #PROPERTY_SNAPSHOT_RETENTION_HOURS}.
	 *
	 * Le fotografie dei test superati vengono rimosse dal test stesso, mentre quelle dei test falliti sono
	 * deliberatamente conservate per l'analisi: senza una scadenza si accumulerebbero indefinitamente.
	 * Da invocare all'avvio delle classi che le utilizzano, cosi' da non toccare quelle dell'esecuzione in
	 * corso.
	 */
	public static int deleteSnapshotScaduti(Logger log) {
		File dir = getSnapshotDir();
		if(!dir.exists()) {
			return 0;
		}
		int ore = getIntProperty(PROPERTY_SNAPSHOT_RETENTION_HOURS, DEFAULT_SNAPSHOT_RETENTION_HOURS);
		long soglia = System.currentTimeMillis() - (ore * 3600_000L);
		File[] files = dir.listFiles((d, name) -> name.endsWith(SNAPSHOT_SUFFIX));
		if(files==null) {
			return 0;
		}
		int eliminati = 0;
		for (File f : files) {
			if(f.lastModified() < soglia && f.delete()) {
				eliminati++;
			}
		}
		if(eliminati>0 && log!=null) {
			log.info("Rimosse {} fotografie del repository dumpBinario piu' vecchie di {} ore", eliminati, ore);
		}
		return eliminati;
	}


	private static int readNumeroFile(String idTransazione, String fase) {
		Properties p = readSnapshot(idTransazione, fase);
		String files = p.getProperty("files");
		if(files==null) {
			fail("Fotografia del repository dumpBinario priva dell'informazione 'files' (fase '"+fase+"', transazione ["+idTransazione+"])");
		}
		try {
			return Integer.parseInt(files.trim());
		} catch(NumberFormatException e) {
			fail("Fotografia del repository dumpBinario con 'files' non numerico: "+files);
			return 0; // non raggiungibile
		}
	}

	private static String leggiDettaglioFile(String idTransazione, String fase) {
		Properties p = readSnapshot(idTransazione, fase);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; p.getProperty("file."+i)!=null; i++) {
			sb.append("\n\t").append(p.getProperty("file."+i))
				.append(" (").append(p.getProperty("size."+i)).append(" bytes)");
		}
		return sb.toString();
	}


	private static int getIntProperty(String nome, int defaultValue) {
		String value = System.getProperty(nome);
		if(value==null || value.trim().isEmpty()) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch(NumberFormatException e) {
			fail("Property '"+nome+"' non numerica: "+value);
			return defaultValue; // non raggiungibile
		}
	}

}
