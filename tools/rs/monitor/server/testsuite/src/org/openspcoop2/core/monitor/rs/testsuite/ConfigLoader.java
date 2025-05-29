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

package org.openspcoop2.core.monitor.rs.testsuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpUtilsException;
import org.slf4j.Logger;

import com.intuit.karate.FileUtils;

/**
 * ConfigLoader
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigLoader {

	private static final String PROP_FILE_NAME = "testsuite.properties";
	private static final String TEST_BUNDLE_DIRECTORY = "src/configurazioni-govway";

	
	protected static Properties prop = new Properties();
	static {
		setupProperties();
	}

	protected static Logger logCore = null;
	protected static Logger logRateLimiting = null;
	protected static Logger logRegistrazioneMessaggi = null;

	private static final String PROP_JMX_USERNAME = "jmx_username";
	protected static String getJmxUsername() {
		return prop.getProperty(PROP_JMX_USERNAME);
	}
	
	private static final String PROP_JMX_PASSWORD = "jmx_password";
	protected static String getJmxPassword() {
		return prop.getProperty(PROP_JMX_PASSWORD);
	}
	
	private static final String PROP_GOVWAY_BASE = "govway_base_path";
	protected static String getGovwayBasePath() {
		return prop.getProperty(PROP_GOVWAY_BASE);
	}
	
	private static final String PROP_JMX_CACHE_RESOURCES = "jmx_cache_resources";
	protected static String[] getJmxCacheResources() {
		return prop.getProperty(PROP_JMX_CACHE_RESOURCES).split(",");
	}
	
	protected static Logger getLoggerKarate() {
		return LoggerWrapperFactory.getLogger("com.intuit.karate");
	}
	
	public static Logger getLoggerCore() {
		return logCore;
	}

	public static Logger getLoggerRateLimiting() {
		return logRateLimiting;
	}

	public static Logger getLoggerRegistrazioneMessaggi() {
		return logRegistrazioneMessaggi;
	}

	public static DbUtils getDbUtils() {
		return dbUtils;
	}

	@BeforeClass
	public static void setupLogger() {
		logCore = LoggerWrapperFactory.getLogger("testsuite.core");
		logRateLimiting = LoggerWrapperFactory.getLogger("testsuite.rate_limiting");
		logRegistrazioneMessaggi = LoggerWrapperFactory.getLogger("testsuite.registrazione_messaggi");
	}

	protected static DbUtils dbUtils;

	@Rule
	public TestName testName = new TestName();

	@Before
	public void before() {
		logCore.info("\n###################" + "\nEseguo test: {}.{}" + "\n##################",
				this.getClass().getName(), this.testName.getMethodName());
	}

	@BeforeClass
	public static void setupDbUtils() {
		Map<String, Object> dbConfig = new HashMap<>();
		dbConfig.put("username", System.getProperty("db_username"));
		dbConfig.put("password", System.getProperty("db_password"));
		dbConfig.put("url", System.getProperty("db_url"));
		dbConfig.put("driverClassName", System.getProperty("db_driverClassName"));
		String type = System.getProperty("db_type");
		if (type != null) {
			dbConfig.put("dbType", type);
		}
		dbUtils = new DbUtils(dbConfig);
	}

	@BeforeClass
	public static void setupProperties() {

		try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(PROP_FILE_NAME);) {

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + PROP_FILE_NAME + "' not found in the classpath");
			}

			for (var p : prop.entrySet()) {
				System.setProperty((String) p.getKey(), (String) p.getValue());
			}

			setupLogger();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		boolean load = true;
		String inputFiles = null;
		if (args != null) {
			if (args.length > 0)
				load = Boolean.valueOf(args[0]);
			if (args.length > 1)
				inputFiles = args[1];
		}
		
		
		Set<String> paths = null;
		if (inputFiles == null) {
			paths = Stream.of(new File(TEST_BUNDLE_DIRECTORY).listFiles())
		      .filter(file -> !file.isDirectory())
		      .map(File::getName)
		      .collect(Collectors.toSet());
		} else {
			paths = Set.of(inputFiles.split(","));
		}
		
		
		for (String path : paths) {
			if (load) {
				prepareConfig(path);
			} else {
				deleteConfig(path);
			}
		}
	}

	public static void resetCache() throws UtilsException, HttpUtilsException {

		Logger logger = getLoggerKarate();
		logger.debug("---- resetAllCache ----");

		String jmxUser = getJmxUsername();
		String jmxPass = getJmxPassword();

		String[] govwayCaches = getJmxCacheResources();
		for (String resource : govwayCaches) {
			logger.debug("Resetto cache: {}", resource);
			String url = getGovwayBasePath() + "/check?methodName=resetCache&resourceName=" + resource;
			org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmxUser, jmxPass);
		}

	}

	public static void resetCacheExcludeCachePrimoLivello() throws UtilsException, HttpUtilsException {
		resetCache(true, "DatiRichieste");
	}

	public static void resetCachePrimoLivello() throws UtilsException, HttpUtilsException {
		resetCache(false, "DatiRichieste");
	}

	public static void resetCache(boolean useForSkip, String... cache) throws UtilsException, HttpUtilsException {

		if (cache == null || cache.length <= 0) {
			resetCache();
			return;
		}

		Logger logger = getLoggerKarate();
		logger.debug("---- resetCache useForSkip:{} cache:{} ----", useForSkip, Arrays.asList(cache));

		String jmxUser = getJmxUsername();
		String jmxPass = getJmxPassword();

		String[] govwayCaches = getJmxCacheResources();
		
		Set<String> cacheTable = Set.of(cache);
		
		for (String resource : govwayCaches) {
			boolean found = cacheTable.contains(resource);
			
			if ((useForSkip && found) || (!useForSkip && !found)) {
				if (found)
					logger.debug("Skip reset cache: {}", resource);
				else
					logger.debug("Salto reset cache perchè non richiesto: {}", resource);
				continue;
			}
			
			logger.debug("Resetto cache: {}", resource);
			String url = getGovwayBasePath() + "/check?methodName=resetCache&resourceName=" + resource;
			org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmxUser, jmxPass);
		}

	}

	public static void enableCaches(String... caches) throws UtilsException, HttpUtilsException {
		Logger logger = getLoggerKarate();
		logger.debug("---- enableCache  cache:{} ----", Arrays.asList(caches));

		String jmxUser = getJmxUsername();
		String jmxPass = getJmxPassword();

		String[] govwayCaches = getJmxCacheResources();

		Map<String, String> params = Map.of("methodName", "abilitaCache", CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE,
				"0", CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE_2, "true",
				CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE_3, "0", CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE_4,
				"0");
		String encodedParams = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
				.collect(Collectors.joining("&"));

		for (String resource : govwayCaches) {
			for (String c : caches) {
				if (resource.equals(c)) {
					logger.debug("abilito cache: {}", resource);
					String url = getGovwayBasePath() + "/check?" + encodedParams + "&resourceName="
							+ resource;
					org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmxUser, jmxPass);
					break;
				}
			}
		}
	}

	public static void disableCaches(String... caches) throws UtilsException, HttpUtilsException {
		Logger logger = getLoggerKarate();
		logger.debug("---- disableCache  cache:{} ----", Arrays.asList(caches));

		String jmxUser = getJmxUsername();
		String jmxPass = getJmxPassword();

		String[] govwayCaches = getJmxCacheResources();
		for (String resource : govwayCaches) {
			for (String c : caches) {
				if (resource.equals(c)) {
					logger.debug("disabilito cache: {}", resource);
					String url = getGovwayBasePath()
							+ "/check?methodName=disabilitaCache&resourceName=" + resource;
					org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmxUser, jmxPass);
					break;
				}
			}
		}
	}

	
	public static void prepareConfig(String pathToLoad) throws UtilsException, HttpUtilsException  {

		Logger logger = getLoggerKarate();

		String configLoaderPath = prop.getProperty("config_loader_path");
		String scriptPath = Path.of(configLoaderPath, (FileUtils.isOsWindows() ? "createOrUpdate.cmd" : "createOrUpdate.sh")).toAbsolutePath().toString();
		String bundle = Path.of(TEST_BUNDLE_DIRECTORY, pathToLoad).toAbsolutePath().toString();

		logger.debug("Script path: {}", scriptPath);
		logger.debug("Config loader path: {}", configLoaderPath);
		logger.debug("Bundle path: {}", bundle);
		logger.debug("Carico la configurazione su govway...");

		org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(
				scriptPath);
		scriptInvoker.run(new File(configLoaderPath), bundle);

		// Dopo aver caricato lo script, resetto le cache
		resetCache();
	}
	
	public static void deleteConfig(String pathToDelete) throws UtilsException {

		String configLoaderPath = prop.getProperty("config_loader_path");
		String scriptPath = Path.of(configLoaderPath, (FileUtils.isOsWindows() ? "delete.cmd" : "delete.sh")).toAbsolutePath().toString();
		String bundle = Path.of(TEST_BUNDLE_DIRECTORY, pathToDelete).toAbsolutePath().toString();

		Logger logger = getLoggerKarate();

		logger.debug("Script path: {}", scriptPath);
		logger.debug("Config loader path: {}", configLoaderPath);
		logger.debug("Bundle path: {}", bundle);
		logger.debug("Elimino la configurazione su govway...");

		org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(
				scriptPath);
		scriptInvoker.run(new File(configLoaderPath), bundle);

	}

}
