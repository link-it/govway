/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.template_scan.cli;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ScanResult
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ScanResult {

	private static class Position {
		int line;
		int column;

		Position(int line, int column) {
			this.line = line;
			this.column = column;
		}

		@Override
		public String toString() {
			return "linea " + this.line + " colonna " + this.column;
		}
	}

	private TreeMap<Position, String> matches;

	private ScanResult() {
		this.matches = new TreeMap<>((p1, p2) -> {
			int cmp = Integer.compare(p1.line, p2.line);
			if (cmp == 0) {
				return Integer.compare(p1.column, p2.column);
			}
			return cmp;
		});
	}

	public static Optional<ScanResult> parse(InputStream is, Pattern regex) {
		ScanResult res = new ScanResult();

		try (Scanner sc = new Scanner(is)) {
			String content = sc.useDelimiter("\\A").hasNext() ? sc.next() : "";

			Matcher matcher = regex.matcher(content);
			while (matcher.find()) {
				int position = matcher.start();
				Position pos = calculateLineAndColumn(content, position);
				res.matches.put(pos, matcher.group());
			}
		}
		if (res.matches.isEmpty())
			return Optional.empty();
		return Optional.of(res);
	}

	private static Position calculateLineAndColumn(String content, int offset) {
		int line = 1;
		int column = 1;

		for (int i = 0; i < offset && i < content.length(); i++) {
			if (content.charAt(i) == '\n') {
				line++;
				column = 1;
			} else {
				column++;
			}
		}

		return new Position(line, column);
	}

	@Override
	public String toString() {
		return this.matches.entrySet().stream()
				.map(e -> e.getKey().toString())
				.collect(Collectors.joining(", "));

	}
}
