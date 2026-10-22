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
package org.openspcoop2.utils.date.test;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openspcoop2.utils.date.DateEngineType;
import org.openspcoop2.utils.date.DateUtils;

/**
 * Verifica i formatter condivisi in sola lettura di {@link DateUtils} (get*Shared):
 * per ogni motore di gestione delle date producono lo stesso risultato dei formatter creati ad ogni invocazione,
 * sono utilizzabili contemporaneamente da piu' thread e non consentono di modificarne lo stato.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SharedDateFormatterTest {

	public static void main(String[] args) throws Exception {
		test();
	}

	private static int verifiche = 0;

	/** formatter condiviso e corrispondente formatter creato ad ogni invocazione */
	private static class Coppia {
		private final String nome;
		private final java.util.function.Supplier<SimpleDateFormat> condiviso;
		private final java.util.function.Supplier<SimpleDateFormat> nuovo;
		Coppia(String nome, java.util.function.Supplier<SimpleDateFormat> condiviso, java.util.function.Supplier<SimpleDateFormat> nuovo) {
			this.nome = nome;
			this.condiviso = condiviso;
			this.nuovo = nuovo;
		}
	}

	private static List<Coppia> coppie() {
		List<Coppia> l = new ArrayList<>();
		// pattern utilizzati dai chiamanti dei formatter condivisi
		l.add(new Coppia("ms", DateUtils::getSimpleDateFormatMsShared, DateUtils::getSimpleDateFormatMs));
		l.add(new Coppia("time HHmmssSSS", () -> DateUtils.getDefaultTimeFormatterShared("HHmmssSSS"), () -> DateUtils.getDefaultTimeFormatter("HHmmssSSS")));
		l.add(new Coppia("date yyyyMMdd", () -> DateUtils.getDefaultDateFormatterShared("yyyyMMdd"), () -> DateUtils.getDefaultDateFormatter("yyyyMMdd")));
		l.add(new Coppia("datetime yyyyMMddHHmmssSSS", () -> DateUtils.getDefaultDateTimeFormatterShared("yyyyMMddHHmmssSSS"), () -> DateUtils.getDefaultDateTimeFormatter("yyyyMMddHHmmssSSS")));
		l.add(new Coppia("datetime sql", () -> DateUtils.getDefaultDateTimeFormatterShared("yyyy-MM-dd HH:mm:ss.SSS"), () -> DateUtils.getDefaultDateTimeFormatter("yyyy-MM-dd HH:mm:ss.SSS")));
		return l;
	}

	private static List<Date> date() {
		List<Date> l = new ArrayList<>();
		l.add(new Date(0));
		l.add(new Date(999));
		l.add(new Date(1792664430123L)); // 2026-10-22T10:20:30.123Z: data fissa con millisecondi (test deterministico)
		// intorno ai cambi d'ora (Europe/Rome nel 2026: 29 marzo e 25 ottobre)
		long marzo = 1774746000000L; // 2026-03-29T01:00:00Z
		long ottobre = 1792890000000L; // 2026-10-25T01:00:00Z
		for (long delta = -2*3600_000L; delta <= 2*3600_000L; delta += 15*60_000L + 123) {
			l.add(new Date(marzo+delta));
			l.add(new Date(ottobre+delta));
		}
		l.add(new Date(4102444799999L)); // 2099-12-31T23:59:59.999Z
		return l;
	}

	public static void test() throws Exception {
		verifiche = 0;
		DateEngineType originale = DateUtils.getDEFAULT_DATA_ENGINE_TYPE();
		TimeZone tzOriginale = TimeZone.getDefault();
		try {
			for (String tz : new String[] {"Europe/Rome", "UTC"}) {
				TimeZone.setDefault(TimeZone.getTimeZone(tz));
				for (DateEngineType engine : DateEngineType.values()) {
					DateUtils.setDEFAULT_DATA_ENGINE_TYPE(engine);
					verificaEquivalenza(engine+"/"+tz);
				}
			}
			TimeZone.setDefault(tzOriginale);
			for (DateEngineType engine : DateEngineType.values()) {
				DateUtils.setDEFAULT_DATA_ENGINE_TYPE(engine);
				verificaIstanze(engine.toString());
				verificaSolaLettura(engine.toString());
				verificaConcorrenza(engine.toString());
			}
		}finally {
			DateUtils.setDEFAULT_DATA_ENGINE_TYPE(originale);
			TimeZone.setDefault(tzOriginale);
		}
		System.out.println("Test formatter condivisi completato: "+verifiche+" verifiche effettuate");
	}

	private static void verificaEquivalenza(String caso) throws Exception {
		for (Coppia c : coppie()) {
			SimpleDateFormat condiviso = c.condiviso.get();
			for (Date d : date()) {
				String atteso = c.nuovo.get().format(d);
				checkEquals(caso+" "+c.nome+" format("+d.getTime()+")", atteso, condiviso.format(d));
				String parseAtteso = risolvi(x -> String.valueOf(c.nuovo.get().parse(x).getTime()), atteso);
				checkEquals(caso+" "+c.nome+" parse("+atteso+")", parseAtteso, risolvi(x -> String.valueOf(condiviso.parse(x).getTime()), atteso));
			}
		}
	}

	private static void verificaIstanze(String caso) throws Exception {
		for (Coppia c : coppie()) {
			check(caso+" "+c.nome+": stessa istanza", c.condiviso.get()==c.condiviso.get());
			check(caso+" "+c.nome+": istanza differente dai formatter non condivisi", c.condiviso.get()!=c.nuovo.get());
		}
		check(caso+": istanze distinte per pattern", DateUtils.getDefaultDateTimeFormatterShared("yyyyMMdd")!=DateUtils.getDefaultDateTimeFormatterShared("yyyyMMddHH"));
		check(caso+": istanze distinte per tipo", DateUtils.getDefaultDateTimeFormatterShared("yyyyMMdd")!=DateUtils.getDefaultDateFormatterShared("yyyyMMdd"));
	}

	private static void verificaSolaLettura(String caso) throws Exception {
		SimpleDateFormat sdf = DateUtils.getSimpleDateFormatMsShared();
		List<Runnable> modifiche = new ArrayList<>();
		modifiche.add(() -> sdf.setTimeZone(TimeZone.getTimeZone("UTC")));
		modifiche.add(() -> sdf.setLenient(false));
		modifiche.add(() -> sdf.setCalendar(new java.util.GregorianCalendar(2026, java.util.Calendar.JANUARY, 1)));
		modifiche.add(() -> sdf.setNumberFormat(java.text.NumberFormat.getInstance()));
		modifiche.add(() -> sdf.set2DigitYearStart(new Date(0)));
		modifiche.add(() -> sdf.setDateFormatSymbols(new java.text.DateFormatSymbols()));
		modifiche.add(() -> sdf.applyPattern("yyyy"));
		modifiche.add(() -> sdf.applyLocalizedPattern("yyyy"));
		modifiche.add(() -> sdf.parse("2026-10-21_10:00:00.000", new ParsePosition(0)));
		modifiche.add(() -> sdf.formatToCharacterIterator(new Date(0)));
		for (int i = 0; i < modifiche.size(); i++) {
			boolean eccezione = false;
			try {
				modifiche.get(i).run();
			}catch(UnsupportedOperationException e) {
				eccezione = true;
			}
			check(caso+": operazione non consentita #"+i, eccezione);
		}
		// il formatter resta utilizzabile e invariato
		checkEquals(caso+": formatter invariato", DateUtils.getSimpleDateFormatMs().format(new Date(0)), sdf.format(new Date(0)));
	}

	private static void verificaConcorrenza(String caso) throws Exception {
		int threads = 8;
		int iterazioni = 5000;
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			List<Future<String>> risultati = new ArrayList<>();
			for (int t = 0; t < threads; t++) {
				final long base = 1700000000000L + t * 7_919_000L;
				risultati.add(executor.submit(() -> {
					List<Coppia> coppie = coppie();
					for (int i = 0; i < iterazioni; i++) {
						Date d = new Date(base + i * 86_399_937L);
						for (Coppia c : coppie) {
							String atteso = c.nuovo.get().format(d);
							String ottenuto = c.condiviso.get().format(d);
							if(!atteso.equals(ottenuto)) {
								return c.nome+" "+d.getTime()+" atteso "+atteso+" ottenuto "+ottenuto;
							}
						}
					}
					return null;
				}));
			}
			for (Future<String> f : risultati) {
				String errore = f.get();
				checkEquals(caso+": concorrenza", null, errore);
			}
		}finally {
			executor.shutdown();
		}
	}

	@FunctionalInterface
	private interface Parse {
		String parse(String s) throws Exception;
	}
	private static String risolvi(Parse p, String s) {
		try {
			return p.parse(s);
		}catch(Exception e) {
			return "ERRORE "+e.getClass().getName()+": "+e.getMessage();
		}
	}

	private static void check(String descrizione, boolean condizione) throws Exception {
		verifiche++;
		if(!condizione) {
			throw new Exception("Verifica fallita: "+descrizione);
		}
	}
	private static void checkEquals(String descrizione, String atteso, String ottenuto) throws Exception {
		verifiche++;
		if(atteso==null ? ottenuto!=null : !atteso.equals(ottenuto)) {
			throw new Exception("Verifica fallita: "+descrizione+"\natteso:   "+atteso+"\nottenuto: "+ottenuto);
		}
	}
}
