package org.openspcoop2.pdd.template_scan.cli;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScanResult {
	private TreeMap<Integer, String> matches;
	
	private ScanResult() {
		this.matches = new TreeMap<>();
	}
	
	public static Optional<ScanResult> parse(InputStream is, Pattern regex) {
		ScanResult res = new ScanResult();
		
		try (Scanner sc = new Scanner(is)) {
			sc.findAll(regex).forEach(m -> res.matches.put(m.start(), m.group()));
		}
		if (res.matches.isEmpty())
			return Optional.empty();
		return Optional.of(res);
	}
	
	@Override
	public String toString() {
		return "positions: [" + this.matches.entrySet().stream().map(e -> e.getKey().toString())
				.collect(Collectors.joining(", ")) + "]";
				
	}
}
