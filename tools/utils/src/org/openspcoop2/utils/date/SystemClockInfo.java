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

package org.openspcoop2.utils.date;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Informazioni sulla sorgente di tempo del sistema (clocksource) e sul costo di una lettura dell'orologio.
 *
 * La sorgente di tempo e' rilevabile solamente su Linux (anche all'interno di container); sugli altri sistemi
 * viene riportato il meccanismo utilizzato dalla JVM. Il costo della lettura dell'orologio viene invece misurato
 * su qualsiasi sistema ed e' il dato che determina l'impatto sulle prestazioni.
 *
 * Nessun metodo solleva eccezioni: un'informazione non rilevabile viene riportata come non disponibile.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SystemClockInfo {

	public static final String NON_DISPONIBILE = "non disponibile";

	private static final String LINUX_CLOCKSOURCE_DIR = "/sys/devices/system/clocksource/clocksource0/";
	private static final String LINUX_CURRENT_CLOCKSOURCE = LINUX_CLOCKSOURCE_DIR + "current_clocksource";
	private static final String LINUX_AVAILABLE_CLOCKSOURCE = LINUX_CLOCKSOURCE_DIR + "available_clocksource";

	private static final String CLOCKSOURCE_TSC = "tsc";
	/** Sorgenti di tempo lente (lettura tramite chiamata di sistema), per le quali, se disponibile, e' preferibile 'tsc' */
	private static final List<String> SLOW_CLOCKSOURCES = Arrays.asList("xen", "hpet", "acpi_pm");

	/** Oltre questa soglia (in nanosecondi) la lettura dell'orologio viene considerata lenta */
	private static final long SLOW_CLOCK_THRESHOLD_NS = 200;

	private static final int WARMUP_ITERATIONS = 10000;
	private static final int MEASURE_ITERATIONS = 10000;
	private static final int MEASURE_REPETITIONS = 3;

	/** Destinazione dei valori letti durante la misurazione, per evitare che il compilatore JIT elimini le chiamate */
	private static volatile long sink = 0;

	private String currentClockSource = null;
	private String availableClockSources = null;
	private String clockSourceDescription = NON_DISPONIBILE;
	private long nanoTimeCost = -1;
	private long currentTimeMillisCost = -1;
	private String warning = null;

	private SystemClockInfo() {}

	/**
	 * Rileva la sorgente di tempo e misura il costo della lettura dell'orologio.
	 * La misurazione richiede alcuni millisecondi (qualche decina con sorgenti di tempo lente).
	 */
	public static SystemClockInfo read() {
		SystemClockInfo info = new SystemClockInfo();
		try {
			info.initClockSource();
		}catch(Throwable t) {
			// informazione opzionale: resta 'non disponibile'
		}
		try {
			info.nanoTimeCost = measureNanoTime();
		}catch(Throwable t) {
			// informazione opzionale: resta non misurata
		}
		try {
			info.currentTimeMillisCost = measureCurrentTimeMillis();
		}catch(Throwable t) {
			// informazione opzionale: resta non misurata
		}
		try {
			info.initWarning();
		}catch(Throwable t) {
			// informazione opzionale: nessun avviso
		}
		return info;
	}

	private void initClockSource() {
		String os = System.getProperty("os.name", "");
		String osLowerCase = os.toLowerCase(Locale.ROOT);
		if(osLowerCase.contains("linux")) {
			this.currentClockSource = readFile(LINUX_CURRENT_CLOCKSOURCE);
			this.availableClockSources = readFile(LINUX_AVAILABLE_CLOCKSOURCE);
			if(this.currentClockSource!=null) {
				this.clockSourceDescription = this.availableClockSources!=null ?
						this.currentClockSource+" (disponibili: "+this.availableClockSources+")" :
						this.currentClockSource;
			}
		}
		else if(osLowerCase.contains("windows")) {
			this.clockSourceDescription = "QueryPerformanceCounter (sorgente hardware non rilevabile su Windows)";
		}
		else if(osLowerCase.contains("mac")) {
			this.clockSourceDescription = "mach_absolute_time (sorgente hardware non rilevabile su macOS)";
		}
		else {
			this.clockSourceDescription = "non rilevabile su "+os;
		}
	}
	private static String readFile(String file) {
		try {
			Path p = Paths.get(file);
			if(Files.isReadable(p)) {
				String s = Files.readString(p).trim();
				return s.isEmpty() ? null : s;
			}
		}catch(Throwable t) {
			// file non leggibile: informazione non disponibile
		}
		return null;
	}

	private static long measureNanoTime() {
		long s = 0;
		for (int i = 0; i < WARMUP_ITERATIONS; i++) {
			s += System.nanoTime();
		}
		long best = Long.MAX_VALUE;
		for (int r = 0; r < MEASURE_REPETITIONS; r++) {
			long start = System.nanoTime();
			for (int i = 0; i < MEASURE_ITERATIONS; i++) {
				s += System.nanoTime();
			}
			long elapsed = System.nanoTime() - start;
			best = Math.min(best, elapsed / MEASURE_ITERATIONS);
		}
		sink = s;
		return best;
	}
	private static long measureCurrentTimeMillis() {
		long s = 0;
		for (int i = 0; i < WARMUP_ITERATIONS; i++) {
			s += System.currentTimeMillis();
		}
		long best = Long.MAX_VALUE;
		for (int r = 0; r < MEASURE_REPETITIONS; r++) {
			long start = System.nanoTime();
			for (int i = 0; i < MEASURE_ITERATIONS; i++) {
				s += System.currentTimeMillis();
			}
			long elapsed = System.nanoTime() - start;
			best = Math.min(best, elapsed / MEASURE_ITERATIONS);
		}
		sink = s;
		return best;
	}

	private void initWarning() {
		if(this.currentClockSource!=null && SLOW_CLOCKSOURCES.contains(this.currentClockSource) &&
				this.availableClockSources!=null && Arrays.asList(this.availableClockSources.split("\\s+")).contains(CLOCKSOURCE_TSC)) {
			this.warning = "lettura dell'orologio lenta (clocksource '"+this.currentClockSource+"'"+
					(this.nanoTimeCost>=0 ? ", "+this.nanoTimeCost+" ns per lettura" : "")+
					"); valutare il clocksource '"+CLOCKSOURCE_TSC+"' (parametro del kernel clocksource="+CLOCKSOURCE_TSC+")";
		}
		else if(this.nanoTimeCost>SLOW_CLOCK_THRESHOLD_NS) {
			this.warning = "lettura dell'orologio lenta ("+this.nanoTimeCost+" ns per lettura); verificare la sorgente di tempo del sistema";
		}
	}

	/** Sorgente di tempo in uso (con le alternative disponibili), o il meccanismo usato dalla JVM se non rilevabile */
	public String getClockSource() {
		return this.clockSourceDescription;
	}
	/** Costo medio di una lettura dell'orologio */
	public String getClockReadCost() {
		if(this.nanoTimeCost<0 && this.currentTimeMillisCost<0) {
			return NON_DISPONIBILE;
		}
		return "nanoTime "+(this.nanoTimeCost>=0 ? this.nanoTimeCost+" ns" : NON_DISPONIBILE)+
				", currentTimeMillis "+(this.currentTimeMillisCost>=0 ? this.currentTimeMillisCost+" ns" : NON_DISPONIBILE);
	}
	/** Costo medio misurato di System.nanoTime() in nanosecondi, -1 se non misurato */
	public long getNanoTimeCost() {
		return this.nanoTimeCost;
	}
	/** Costo medio misurato di System.currentTimeMillis() in nanosecondi, -1 se non misurato */
	public long getCurrentTimeMillisCost() {
		return this.currentTimeMillisCost;
	}
	/** Avviso da segnalare se la lettura dell'orologio e' lenta, null altrimenti */
	public String getWarning() {
		return this.warning;
	}

	static long getSink() {
		return sink;
	}
}
