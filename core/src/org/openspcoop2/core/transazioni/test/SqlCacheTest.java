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

package org.openspcoop2.core.transazioni.test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.TransazioneExtendedInfo;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCTransazioneServiceImpl;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneFieldConverter;
import org.openspcoop2.generic_project.beans.AliasField;
import org.openspcoop2.generic_project.beans.AliasTableField;
import org.openspcoop2.generic_project.beans.ConstantField;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * Verifica le cache introdotte per la scrittura delle transazioni:
 * - cache dei nomi delle colonne in {@link AbstractSQLFieldConverter} (toColumn/toAliasColumn): per tutti i converter jdbc del core,
 *   per tutti i field dei relativi modelli e per tutte le combinazioni dei parametri, il risultato deve essere identico
 *   alla risoluzione effettuata dal converter (toColumn con 3 parametri), anche in caso di eccezione;
 * - cache dell'SQL di insert in {@link JDBCTransazioneServiceImpl#create}: SQL e parametri inviati al database devono essere identici
 *   tra la prima invocazione (costruzione dell'SQL) e le successive (SQL in cache), anche con colonne estese (transazione-extended-info).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SqlCacheTest {

	public static void main(String[] args) throws Exception {
		test();
	}

	private static int verifiche = 0;

	public static void test() throws Exception {
		verifiche = 0;
		testToColumnCache();
		testToColumnChiave();
		testToColumnFieldNonInCache();
		testToColumnLimite();
		testInsertSqlCache();
		System.out.println("Test cache SQL transazioni completato: "+verifiche+" verifiche effettuate");
	}


	/* ********** CACHE toColumn ********* */

	private static final TipiDatabase[] DATABASE = { TipiDatabase.POSTGRESQL, TipiDatabase.ORACLE, TipiDatabase.MYSQL, TipiDatabase.SQLSERVER, TipiDatabase.HSQL };

	private static void testToColumnCache() throws Exception {
		List<Class<?>> converters = findConverters();
		check("converter jdbc del core individuati", converters.size()>10);
		int fieldVerificati = 0;
		for (TipiDatabase tipo : DATABASE) {
			clearToColumnCache();
			for (Class<?> c : converters) {
				AbstractSQLFieldConverter converter = newConverter(c, tipo);
				if(converter==null) {
					continue;
				}
				for (IField field : findFields(converter.getRootModel())) {
					verificaToColumn(c.getSimpleName()+"["+tipo+"]."+field.getFieldName(), converter, field);
					fieldVerificati++;
				}
				// una nuova istanza dello stesso converter utilizza la stessa cache
				AbstractSQLFieldConverter converter2 = newConverter(c, tipo);
				for (IField field : findFields(converter2.getRootModel())) {
					verificaToColumn(c.getSimpleName()+"["+tipo+"](nuova istanza)."+field.getFieldName(), converter2, field);
				}
			}
		}
		System.out.println("Cache toColumn: "+converters.size()+" converter, "+fieldVerificati+" field verificati");
	}

	private static void verificaToColumn(String caso, AbstractSQLFieldConverter converter, IField field) throws Exception {
		for (boolean appendTablePrefix : new boolean[] {false, true}) {
			String atteso = risolvi(() -> converter.toColumn(field, false, appendTablePrefix));
			String attesoAlias = risolvi(() -> converter.toColumn(field, true, appendTablePrefix));
			for (int i = 0; i < 2; i++) { // la seconda invocazione utilizza la cache
				String ottenuto = risolvi(() -> converter.toColumn(field, appendTablePrefix));
				checkEquals(caso+" toColumn(prefix="+appendTablePrefix+") #"+i, atteso, ottenuto);
				String ottenutoAlias = risolvi(() -> converter.toAliasColumn(field, appendTablePrefix));
				checkEquals(caso+" toAliasColumn(prefix="+appendTablePrefix+") #"+i, attesoAlias, ottenutoAlias);
			}
		}
	}

	/** Converter il cui risultato dipende da tutti gli elementi della chiave della cache (classe, tipo di database, field e parametri) */
	private static class TestConverter extends AbstractSQLFieldConverter {
		private final TipiDatabase tipo;
		private int invocazioni = 0;
		TestConverter(TipiDatabase tipo) {
			this.tipo = tipo;
		}
		@Override
		public IModel<?> getRootModel() {
			return Transazione.model();
		}
		@Override
		public TipiDatabase getDatabaseType() {
			return this.tipo;
		}
		@Override
		public String toColumn(IField field, boolean returnAlias, boolean appendTablePrefix) throws ExpressionException {
			this.invocazioni++;
			if(field.equals(Transazione.model().STATO)) {
				throw new ExpressionException("errore-"+this.invocazioni);
			}
			if(field.equals(Transazione.model().AZIONE)) {
				return null;
			}
			return prefisso()+this.tipo+"-"+field.getFieldName()+"-"+returnAlias+"-"+appendTablePrefix;
		}
		protected String prefisso() {
			return "A-";
		}
	}
	private static class TestConverter2 extends TestConverter {
		TestConverter2(TipiDatabase tipo) {
			super(tipo);
		}
		@Override
		protected String prefisso() {
			return "B-";
		}
	}

	private static void testToColumnChiave() throws Exception {
		clearToColumnCache();
		IField[] fields = { Transazione.model().ESITO, Transazione.model().PROTOCOLLO };
		AbstractSQLFieldConverter[] converters = { new TestConverter(TipiDatabase.POSTGRESQL), new TestConverter(TipiDatabase.ORACLE),
				new TestConverter2(TipiDatabase.POSTGRESQL), new TestConverter2(TipiDatabase.ORACLE) };
		for (int giro = 0; giro < 2; giro++) { // il secondo giro utilizza la cache
			for (AbstractSQLFieldConverter converter : converters) {
				for (IField field : fields) {
					String caso = converter.getClass().getSimpleName()+"["+converter.getDatabaseType()+"]."+field.getFieldName()+" giro "+giro;
					verificaToColumn(caso, converter, field);
				}
			}
		}
		check("voci in cache distinte per tutti gli elementi della chiave", getToColumnCache().size()==converters.length*fields.length*4);

		// eccezioni e valori null non vengono mantenuti in cache: il converter viene invocato ogni volta
		TestConverter converter = new TestConverter(TipiDatabase.POSTGRESQL);
		String errore1 = risolvi(() -> converter.toColumn(Transazione.model().STATO, false));
		String errore2 = risolvi(() -> converter.toColumn(Transazione.model().STATO, false));
		checkEquals("eccezione non in cache (1)", "ERRORE "+ExpressionException.class.getName()+": errore-1", errore1);
		checkEquals("eccezione non in cache (2)", "ERRORE "+ExpressionException.class.getName()+": errore-2", errore2);
		check("null restituito", converter.toAliasColumn(Transazione.model().AZIONE, true)==null && converter.toAliasColumn(Transazione.model().AZIONE, true)==null);
		check("null non in cache", converter.invocazioni==4);

		// field null: stesso comportamento del converter (nessuna cache)
		TransazioneFieldConverter transazioneConverter = new TransazioneFieldConverter(TipiDatabase.POSTGRESQL);
		checkEquals("field null", risolvi(() -> transazioneConverter.toColumn(null, false, true)), risolvi(() -> transazioneConverter.toColumn(null, true)));

		// equals della chiave: ogni elemento della chiave è significativo (verificato direttamente, poiché nella mappa viene invocato solo in caso di collisione di hash)
		IField esito = Transazione.model().ESITO;
		Object chiave = newToColumnCacheKey(TestConverter.class, TipiDatabase.POSTGRESQL, esito, false, false);
		check("chiave uguale a se stessa", chiave.equals(chiave));
		check("chiave uguale con stessi elementi", chiave.equals(newToColumnCacheKey(TestConverter.class, TipiDatabase.POSTGRESQL, esito, false, false))
				&& chiave.hashCode()==newToColumnCacheKey(TestConverter.class, TipiDatabase.POSTGRESQL, esito, false, false).hashCode());
		check("chiave differente da altro oggetto", !chiave.equals("chiave") && !chiave.equals(null));
		check("chiave differente per field", !chiave.equals(newToColumnCacheKey(TestConverter.class, TipiDatabase.POSTGRESQL, Transazione.model().PROTOCOLLO, false, false)));
		check("chiave differente per classe", !chiave.equals(newToColumnCacheKey(TestConverter2.class, TipiDatabase.POSTGRESQL, esito, false, false)));
		check("chiave differente per database", !chiave.equals(newToColumnCacheKey(TestConverter.class, TipiDatabase.ORACLE, esito, false, false)));
		check("chiave differente per returnAlias", !chiave.equals(newToColumnCacheKey(TestConverter.class, TipiDatabase.POSTGRESQL, esito, true, false)));
		check("chiave differente per appendTablePrefix", !chiave.equals(newToColumnCacheKey(TestConverter.class, TipiDatabase.POSTGRESQL, esito, false, true)));
		clearToColumnCache();
	}
	private static Object newToColumnCacheKey(Class<?> converterClass, TipiDatabase tipo, IField field, boolean returnAlias, boolean appendTablePrefix) throws Exception {
		Class<?> c = Class.forName(AbstractSQLFieldConverter.class.getName()+"$ToColumnCacheKey");
		Constructor<?> constructor = c.getDeclaredConstructor(Class.class, TipiDatabase.class, IField.class, boolean.class, boolean.class);
		constructor.setAccessible(true);
		return constructor.newInstance(converterClass, tipo, field, returnAlias, appendTablePrefix);
	}

	private static void testToColumnFieldNonInCache() throws Exception {
		clearToColumnCache();
		TransazioneFieldConverter converter = new TransazioneFieldConverter(TipiDatabase.POSTGRESQL);
		Field esito = (Field) Transazione.model().ESITO;
		List<IField> dinamici = new ArrayList<>();
		dinamici.add(new CustomField("custom", String.class, "colonna_custom", "tabella_custom"));
		dinamici.add(new ConstantField("costante", "valore", String.class));
		dinamici.add(new AliasField(esito, "alias_esito"));
		dinamici.add(new AliasTableField(esito, "alias_tabella"));
		for (IField field : dinamici) {
			verificaToColumn("field dinamico "+field.getClass().getSimpleName(), converter, field);
		}
		check("field dinamici non mantenuti in cache", getToColumnCache().isEmpty());

		// field con lo stesso contenuto di un field del modello ma istanza differente: stesso nome di colonna
		Field copia = new Field(esito.getFieldName(), esito.getFieldType(), esito.getClassName(), esito.getClassType());
		checkEquals("istanza differente con stesso contenuto", converter.toColumn(esito, true), converter.toColumn(copia, true));
	}

	private static void testToColumnLimite() throws Exception {
		clearToColumnCache();
		int limite = getToColumnCacheMaxEntries();
		TransazioneFieldConverter converter = new TransazioneFieldConverter(TipiDatabase.POSTGRESQL);
		Field esito = (Field) Transazione.model().ESITO;
		String atteso = converter.toColumn(esito, false, false);
		// field creati dinamicamente (istanze sempre differenti): la cache non deve superare il limite
		for (int i = 0; i < limite+50; i++) {
			Field f = new Field(esito.getFieldName(), esito.getFieldType(), esito.getClassName(), esito.getClassType());
			checkEqualsSilente("limite cache #"+i, atteso, converter.toColumn(f, false));
		}
		check("cache entro il limite ("+getToColumnCache().size()+" <= "+limite+")", getToColumnCache().size()<=limite);
		// superato il limite i field continuano ad essere risolti correttamente
		checkEquals("risoluzione oltre il limite", converter.toColumn(Transazione.model().STATO, false, true), converter.toColumn(Transazione.model().STATO, true));
		clearToColumnCache();
	}


	/* ********** CACHE SQL DI INSERT ********* */

	/** Servizio con converter impostato direttamente (senza service manager) */
	private static class TestTransazioneService extends JDBCTransazioneServiceImpl {
		private final TransazioneFieldConverter converter;
		TestTransazioneService(TipiDatabase tipo) {
			this.converter = new TransazioneFieldConverter(tipo);
		}
		@Override
		public TransazioneFieldConverter getTransazioneFieldConverter() {
			return this.converter;
		}
	}

	/** Comando registrato sulla connessione: SQL e parametri impostati */
	private static class Comando {
		private String sql;
		private final List<String> parametri = new ArrayList<>();
		@Override
		public String toString() {
			return this.sql+" "+this.parametri;
		}
	}

	private static Connection connessioneRegistrata(List<Comando> comandi) {
		InvocationHandler statementHandler = (proxy, method, args) -> {
			String name = method.getName();
			if(name.startsWith("set") && args!=null && args.length>=2 && args[0] instanceof Integer) {
				comandi.get(comandi.size()-1).parametri.add(args[0]+":"+name+"="+(args[1] instanceof byte[] ? "byte["+((byte[])args[1]).length+"]" : String.valueOf(args[1])));
				return null;
			}
			return defaultValue(method.getReturnType());
		};
		InvocationHandler connectionHandler = (proxy, method, args) -> {
			if("prepareStatement".equals(method.getName())) {
				Comando c = new Comando();
				c.sql = (String) args[0];
				comandi.add(c);
				return Proxy.newProxyInstance(SqlCacheTest.class.getClassLoader(), new Class<?>[] {PreparedStatement.class}, statementHandler);
			}
			return defaultValue(method.getReturnType());
		};
		return (Connection) Proxy.newProxyInstance(SqlCacheTest.class.getClassLoader(), new Class<?>[] {Connection.class}, connectionHandler);
	}
	private static Object defaultValue(Class<?> type) {
		if(type==boolean.class) return false;
		if(type==int.class) return 1;
		if(type==long.class) return 1L;
		if(type.isPrimitive() && type!=void.class) return 0;
		return null;
	}

	private static Transazione buildTransazione(String id, String ... extendedNames) {
		Transazione t = new Transazione();
		t.setIdTransazione(id);
		t.setStato("stato");
		t.setEsito(0);
		t.setProtocollo("trasparente");
		t.setDataIngressoRichiesta(new Date(1000L));
		t.setDataUscitaRisposta(new Date(2000L));
		t.setAzione("PUT_test");
		t.setDiagnostici("diagnostici");
		for (String nome : extendedNames) {
			TransazioneExtendedInfo info = new TransazioneExtendedInfo();
			info.setNome(nome);
			info.setValore("valore-"+nome);
			t.addTransazioneExtendedInfo(info);
		}
		return t;
	}

	private static Comando create(TestTransazioneService service, JDBCServiceManagerProperties properties, Transazione t) throws Exception {
		List<Comando> comandi = new ArrayList<>();
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(properties.getDatabase());
		service.create(properties, LoggerWrapperFactory.getLogger(SqlCacheTest.class), connessioneRegistrata(comandi), sqlQueryObject, t, null);
		check("un solo comando inviato al database", comandi.size()==1);
		return comandi.get(0);
	}

	private static void testInsertSqlCache() throws Exception {
		// la cache non viene svuotata tra un tipo di database e l'altro: le voci devono essere distinte per tipo di database
		clearInsertSqlCache();
		int vociPrecedenti = 0;
		for (TipiDatabase tipo : DATABASE) {
			JDBCServiceManagerProperties properties = new JDBCServiceManagerProperties();
			properties.setDatabaseType(tipo.getNome());
			properties.setShowSql(false);
			TestTransazioneService service = new TestTransazioneService(tipo);

			// prima invocazione: SQL costruito; seconda e terza: SQL in cache
			Comando primo = create(service, properties, buildTransazione("id-1"));
			check(tipo+": SQL in cache dopo la prima invocazione", getInsertSqlCache().size()==vociPrecedenti+1);
			Comando secondo = create(service, properties, buildTransazione("id-1"));
			checkEquals(tipo+": SQL dalla cache", primo.sql, secondo.sql);
			checkEquals(tipo+": parametri con SQL dalla cache", primo.parametri.toString(), secondo.parametri.toString());
			Comando terzo = create(service, properties, buildTransazione("id-2"));
			checkEquals(tipo+": SQL dalla cache (transazione differente)", primo.sql, terzo.sql);
			check(tipo+": parametri della transazione differente", !primo.parametri.equals(terzo.parametri) && terzo.parametri.toString().contains("id-2"));
			check(tipo+": numero di parametri = colonne dell'insert", primo.parametri.size()==contaParametri(primo.sql));

			// colonne estese: SQL distinti per insieme di colonne, ciascuno identico tra costruzione e cache
			Comando extA1 = create(service, properties, buildTransazione("id-3", "colonna_a"));
			Comando extA2 = create(service, properties, buildTransazione("id-3", "colonna_a"));
			Comando extAB1 = create(service, properties, buildTransazione("id-4", "colonna_a", "colonna_b"));
			Comando extAB2 = create(service, properties, buildTransazione("id-4", "colonna_a", "colonna_b"));
			checkEquals(tipo+": extended info, SQL dalla cache", extA1.sql, extA2.sql);
			checkEquals(tipo+": extended info, parametri dalla cache", extA1.parametri.toString(), extA2.parametri.toString());
			checkEquals(tipo+": extended info (2 colonne), SQL dalla cache", extAB1.sql, extAB2.sql);
			check(tipo+": extended info, SQL differenti per colonne differenti", !primo.sql.equals(extA1.sql) && !extA1.sql.equals(extAB1.sql));
			check(tipo+": extended info, colonne presenti", extA1.sql.contains("colonna_a") && extAB1.sql.contains("colonna_b") && !primo.sql.contains("colonna_a"));
			check(tipo+": extended info, numero di parametri", extAB1.parametri.size()==primo.parametri.size()+2 && extAB1.parametri.size()==contaParametri(extAB1.sql));
			check(tipo+": voci in cache", getInsertSqlCache().size()==vociPrecedenti+3);

			// colonna estesa duplicata rispetto al modello: errore ad ogni invocazione (non viene mantenuta in cache)
			for (int i = 0; i < 2; i++) {
				boolean errore = false;
				try {
					create(service, properties, buildTransazione("id-5", "esito"));
				}catch(Exception e) {
					errore = true;
				}
				check(tipo+": colonna estesa duplicata #"+i, errore);
			}
			check(tipo+": colonna duplicata non in cache", getInsertSqlCache().size()==vociPrecedenti+3);
			vociPrecedenti = getInsertSqlCache().size();
		}

		// limite della cache: oltre il limite l'SQL continua ad essere costruito correttamente
		clearInsertSqlCache();
		JDBCServiceManagerProperties properties = new JDBCServiceManagerProperties();
		properties.setDatabaseType(TipiDatabase.POSTGRESQL.getNome());
		TestTransazioneService service = new TestTransazioneService(TipiDatabase.POSTGRESQL);
		int limite = getInsertSqlCacheMaxEntries();
		for (int i = 0; i < limite+10; i++) {
			Comando c = create(service, properties, buildTransazione("id-limite", "colonna_"+i));
			if(!c.sql.contains("colonna_"+i)) {
				throw new Exception("SQL errato oltre il limite della cache");
			}
		}
		check("cache SQL entro il limite", getInsertSqlCache().size()<=limite);
		clearInsertSqlCache();
	}
	private static int contaParametri(String sql) {
		int n = 0;
		for (char c : sql.toCharArray()) {
			if(c=='?') n++;
		}
		return n;
	}


	/* ********** UTILITY ********* */

	@FunctionalInterface
	private interface Risoluzione {
		String risolvi() throws Exception;
	}
	private static String risolvi(Risoluzione r) {
		try {
			return r.risolvi();
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
		checkEqualsSilente(descrizione, atteso, ottenuto);
	}
	private static void checkEqualsSilente(String descrizione, String atteso, String ottenuto) throws Exception {
		if(atteso==null ? ottenuto!=null : !atteso.equals(ottenuto)) {
			throw new Exception("Verifica fallita: "+descrizione+"\natteso:   "+atteso+"\nottenuto: "+ottenuto);
		}
	}

	private static Map<?,?> getToColumnCache() throws Exception {
		java.lang.reflect.Field f = AbstractSQLFieldConverter.class.getDeclaredField("TO_COLUMN_CACHE");
		f.setAccessible(true);
		return (Map<?,?>) f.get(null);
	}
	private static void clearToColumnCache() throws Exception {
		getToColumnCache().clear();
	}
	private static int getToColumnCacheMaxEntries() throws Exception {
		java.lang.reflect.Field f = AbstractSQLFieldConverter.class.getDeclaredField("TO_COLUMN_CACHE_MAX_ENTRIES");
		f.setAccessible(true);
		return f.getInt(null);
	}
	private static Map<?,?> getInsertSqlCache() throws Exception {
		java.lang.reflect.Field f = JDBCTransazioneServiceImpl.class.getDeclaredField("INSERT_SQL_CACHE");
		f.setAccessible(true);
		return (Map<?,?>) f.get(null);
	}
	private static void clearInsertSqlCache() throws Exception {
		getInsertSqlCache().clear();
	}
	private static int getInsertSqlCacheMaxEntries() throws Exception {
		java.lang.reflect.Field f = JDBCTransazioneServiceImpl.class.getDeclaredField("INSERT_SQL_CACHE_MAX_ENTRIES");
		f.setAccessible(true);
		return f.getInt(null);
	}

	/** Converter jdbc presenti nello stesso archivio (jar o directory) del converter delle transazioni */
	private static List<Class<?>> findConverters() throws Exception {
		List<Class<?>> list = new ArrayList<>();
		URL location = TransazioneFieldConverter.class.getProtectionDomain().getCodeSource().getLocation();
		File f = new File(location.toURI());
		List<String> nomi = new ArrayList<>();
		if(f.isDirectory()) {
			collectClassNames(f, f, nomi);
		}
		else {
			try(JarFile jar = new JarFile(f)){
				Enumeration<JarEntry> entries = jar.entries();
				while(entries.hasMoreElements()) {
					nomi.add(entries.nextElement().getName());
				}
			}
		}
		for (String nome : nomi) {
			if(nome.startsWith("org/openspcoop2/core/") && nome.contains("/dao/jdbc/converter/") && nome.endsWith("FieldConverter.class") && !nome.contains("$")) {
				Class<?> c = Class.forName(nome.substring(0, nome.length()-".class".length()).replace('/', '.'));
				if(AbstractSQLFieldConverter.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
					list.add(c);
				}
			}
		}
		Collections.sort(list, (a,b) -> a.getName().compareTo(b.getName()));
		return list;
	}
	private static void collectClassNames(File root, File dir, List<String> nomi) {
		File[] files = dir.listFiles();
		if(files==null) {
			return;
		}
		for (File file : files) {
			if(file.isDirectory()) {
				collectClassNames(root, file, nomi);
			}
			else {
				nomi.add(root.toURI().relativize(file.toURI()).getPath());
			}
		}
	}
	private static AbstractSQLFieldConverter newConverter(Class<?> c, TipiDatabase tipo) throws Exception {
		try {
			Constructor<?> constructor = c.getConstructor(TipiDatabase.class);
			return (AbstractSQLFieldConverter) constructor.newInstance(tipo);
		}catch(NoSuchMethodException e) {
			return null;
		}
	}

	/** Tutti i field (anche dei modelli annidati) raggiungibili dal modello */
	private static List<IField> findFields(IModel<?> model) throws Exception {
		List<IField> fields = new ArrayList<>();
		Set<Object> visitati = Collections.newSetFromMap(new IdentityHashMap<>());
		collectFields(model, fields, visitati);
		return fields;
	}
	private static void collectFields(Object model, List<IField> fields, Set<Object> visitati) throws Exception {
		if(model==null || !visitati.add(model)) {
			return;
		}
		for (java.lang.reflect.Field f : model.getClass().getFields()) {
			if(Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			Object value = f.get(model);
			if(value instanceof IField) {
				fields.add((IField) value);
			}
			if(value instanceof IModel) {
				collectFields(value, fields, visitati);
			}
		}
	}
}
