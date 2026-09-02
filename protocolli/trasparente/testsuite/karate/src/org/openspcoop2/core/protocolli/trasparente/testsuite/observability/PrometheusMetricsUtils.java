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

package org.openspcoop2.core.protocolli.trasparente.testsuite.observability;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.TreeSet;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * PrometheusMetricsUtils
 *
 * Helper di parsing per il formato testuale di esposizione Prometheus (version 0.0.4).
 * Legge lo scrape (righe {@code name{labels} value [timestamp]}, ignorando i commenti
 * {@code # HELP}/{@code # TYPE}) in una struttura interrogabile per nome metrica e label.
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PrometheusMetricsUtils {

	/** Singola serie campionata: nome metrica, label e valore. */
	public static class Sample {
		private final String name;
		private final Map<String,String> labels;
		private final double value;

		Sample(String name, Map<String,String> labels, double value) {
			this.name = name;
			this.labels = labels;
			this.value = value;
		}
		public String getName() {
			return this.name;
		}
		public Map<String,String> getLabels() {
			return this.labels;
		}
		public String getLabel(String key) {
			return this.labels.get(key);
		}
		public double getValue() {
			return this.value;
		}
		@Override
		public String toString() {
			return this.name + this.labels + " " + this.value;
		}
	}

	private final List<Sample> samples;
	private final Set<String> metricNames;

	private PrometheusMetricsUtils(List<Sample> samples) {
		this.samples = samples;
		Set<String> names = new TreeSet<>();
		for (Sample s : samples) {
			names.add(s.getName());
		}
		this.metricNames = names;
	}

	// ---- scrape + parse ----

	/** Effettua la GET dell'endpoint (senza autenticazione) e ne parsa il body Prometheus. */
	public static PrometheusMetricsUtils scrape(String url) throws UtilsException {
		HttpResponse response = HttpUtilities.getHTTPResponse(url);
		int status = response.getResultHTTPOperation();
		if(status!=200) {
			throw new UtilsException("Scrape dell'endpoint '"+url+"' ha restituito HTTP "+status);
		}
		byte[] content = response.getContent();
		String body = (content!=null) ? new String(content, StandardCharsets.UTF_8) : "";
		return parse(body);
	}

	/** Parsa un'esposizione Prometheus in formato testuale. */
	public static PrometheusMetricsUtils parse(String text) {
		List<Sample> out = new ArrayList<>();
		if(text!=null) {
			for (String raw : text.split("\n")) {
				String line = raw.trim();
				if(line.isEmpty() || line.charAt(0)=='#') {
					continue; // riga vuota o commento (# HELP / # TYPE)
				}
				Sample s = parseLine(line);
				if(s!=null) {
					out.add(s);
				}
			}
		}
		return new PrometheusMetricsUtils(out);
	}

	// ---- interrogazione ----

	/** Indica se e' presente almeno una serie con il nome metrica indicato. */
	public boolean contains(String name) {
		return this.metricNames.contains(name);
	}

	/** Nomi metrica presenti (ordinati). */
	public Set<String> names() {
		return Collections.unmodifiableSet(this.metricNames);
	}

	/** Tutte le serie campionate. */
	public List<Sample> samples() {
		return Collections.unmodifiableList(this.samples);
	}

	/** Serie con il nome metrica indicato. */
	public List<Sample> samples(String name) {
		List<Sample> out = new ArrayList<>();
		for (Sample s : this.samples) {
			if(s.getName().equals(name)) {
				out.add(s);
			}
		}
		return out;
	}

	/** Serie con il nome metrica indicato che matchano tutte le label del filtro. */
	public List<Sample> samples(String name, Map<String,String> labelFilter) {
		List<Sample> out = new ArrayList<>();
		for (Sample s : samples(name)) {
			if(matches(s, labelFilter)) {
				out.add(s);
			}
		}
		return out;
	}

	/** Valore della prima serie che matcha nome + label ({@code empty} se assente). */
	public OptionalDouble value(String name, Map<String,String> labelFilter) {
		for (Sample s : samples(name)) {
			if(matches(s, labelFilter)) {
				return OptionalDouble.of(s.getValue());
			}
		}
		return OptionalDouble.empty();
	}

	/** Somma dei valori di tutte le serie che matchano nome + label ({@code 0} se nessuna). */
	public double sum(String name, Map<String,String> labelFilter) {
		double total = 0;
		for (Sample s : samples(name)) {
			if(matches(s, labelFilter)) {
				total += s.getValue();
			}
		}
		return total;
	}

	/** Numero di serie che matchano nome + label. */
	public int countSamples(String name, Map<String,String> labelFilter) {
		return samples(name, labelFilter).size();
	}

	/** Valori distinti assunti dalla label indicata, tra le serie che matchano nome + filtro. */
	public Set<String> labelValues(String name, String labelKey, Map<String,String> labelFilter) {
		Set<String> values = new TreeSet<>();
		for (Sample s : samples(name, labelFilter)) {
			String v = s.getLabel(labelKey);
			if(v!=null) {
				values.add(v);
			}
		}
		return values;
	}

	// ---- diff tra due scrape ----

	/**
	 * Differenza (after - before) della somma dei valori delle serie che matchano nome + label.
	 * Utile per contatori/histogram cumulativi quando l'endpoint espone gia' dati preesistenti
	 * all'inizio del test: si confrontano due scrape e si valuta solo il delta prodotto.
	 * Le serie assenti in uno dei due scrape valgono {@code 0}.
	 */
	public static double delta(PrometheusMetricsUtils before, PrometheusMetricsUtils after,
			String name, Map<String,String> labelFilter) {
		double b = (before!=null) ? before.sum(name, labelFilter) : 0;
		double a = (after!=null) ? after.sum(name, labelFilter) : 0;
		return a - b;
	}

	/** Convenience: delta senza filtro di label (somma su tutte le serie del nome metrica). */
	public static double delta(PrometheusMetricsUtils before, PrometheusMetricsUtils after, String name) {
		return delta(before, after, name, null);
	}

	private static boolean matches(Sample s, Map<String,String> labelFilter) {
		if(labelFilter==null || labelFilter.isEmpty()) {
			return true;
		}
		for (Map.Entry<String,String> e : labelFilter.entrySet()) {
			if(!e.getValue().equals(s.getLabels().get(e.getKey()))) {
				return false;
			}
		}
		return true;
	}

	// ---- parsing di una singola riga ----

	private static Sample parseLine(String line) {
		int brace = line.indexOf('{');
		int firstWs = indexOfWhitespace(line, 0);

		String name;
		Map<String,String> labels;
		int valueStart;

		if(brace>=0 && (firstWs<0 || brace<firstWs)) {
			name = line.substring(0, brace).trim();
			int close = findClosingBrace(line, brace);
			if(close<0) {
				return null;
			}
			labels = parseLabels(line.substring(brace+1, close));
			valueStart = close+1;
		}
		else {
			int ws = (firstWs<0) ? line.length() : firstWs;
			name = line.substring(0, ws).trim();
			labels = Collections.emptyMap();
			valueStart = ws;
		}

		if(name.isEmpty()) {
			return null;
		}
		String valuePart = line.substring(valueStart).trim();
		if(valuePart.isEmpty()) {
			return null;
		}
		// primo token = valore; l'eventuale secondo token e' il timestamp (ignorato)
		int ws2 = indexOfWhitespace(valuePart, 0);
		String valueToken = (ws2<0) ? valuePart : valuePart.substring(0, ws2);
		return new Sample(name, labels, parseValue(valueToken));
	}

	private static double parseValue(String token) {
		if("NaN".equals(token)) {
			return Double.NaN;
		}
		if("+Inf".equals(token) || "Inf".equals(token)) {
			return Double.POSITIVE_INFINITY;
		}
		if("-Inf".equals(token)) {
			return Double.NEGATIVE_INFINITY;
		}
		try {
			return Double.parseDouble(token);
		}catch(NumberFormatException e) {
			return Double.NaN;
		}
	}

	/** Cerca la '}' di chiusura a partire dalla '{' aperta, ignorando le graffe dentro le stringhe. */
	private static int findClosingBrace(String line, int open) {
		boolean inQuotes = false;
		boolean escaped = false;
		for (int i=open+1; i<line.length(); i++) {
			char c = line.charAt(i);
			if(inQuotes) {
				if(escaped) {
					escaped = false;
				}
				else if(c=='\\') {
					escaped = true;
				}
				else if(c=='"') {
					inQuotes = false;
				}
			}
			else {
				if(c=='"') {
					inQuotes = true;
				}
				else if(c=='}') {
					return i;
				}
			}
		}
		return -1;
	}

	/** Parsa il contenuto tra graffe: {@code key="value",key2="value2"} (valori con escape). */
	private static Map<String,String> parseLabels(String content) {
		Map<String,String> labels = new LinkedHashMap<>();
		int i = 0;
		int n = content.length();
		while(i<n) {
			// salta separatori/spazi
			while(i<n && (content.charAt(i)==',' || content.charAt(i)==' ' || content.charAt(i)=='\t')) {
				i++;
			}
			if(i>=n) {
				break;
			}
			// chiave: fino a '='
			int eq = content.indexOf('=', i);
			if(eq<0) {
				break;
			}
			String key = content.substring(i, eq).trim();
			// valore: stringa quotata
			int q = content.indexOf('"', eq);
			if(q<0) {
				break;
			}
			StringBuilder val = new StringBuilder();
			int j = q+1;
			boolean escaped = false;
			while(j<n) {
				char c = content.charAt(j);
				if(escaped) {
					switch(c) {
						case 'n': val.append('\n'); break;
						case 't': val.append('\t'); break;
						default: val.append(c); break; // \" e \\ compresi
					}
					escaped = false;
				}
				else if(c=='\\') {
					escaped = true;
				}
				else if(c=='"') {
					break;
				}
				else {
					val.append(c);
				}
				j++;
			}
			if(!key.isEmpty()) {
				labels.put(key, val.toString());
			}
			i = j+1; // oltre la '"' di chiusura
		}
		return labels;
	}

	private static int indexOfWhitespace(String s, int from) {
		for (int i=from; i<s.length(); i++) {
			char c = s.charAt(i);
			if(c==' ' || c=='\t') {
				return i;
			}
		}
		return -1;
	}
}
