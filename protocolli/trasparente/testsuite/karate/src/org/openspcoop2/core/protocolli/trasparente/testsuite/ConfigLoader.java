/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.utils.transport.http.HttpUtilsException;
import org.slf4j.Logger;

import com.intuit.karate.FileUtils;

/**
* ConfigLoader
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ConfigLoader {

    public static final String propFileName = "testsuite.properties";
    private static final String trasparenteBundlePath = "src/configurazioni-govway/trasparenteTestBundle.zip";
    
    protected static Properties prop = new Properties();
    static {
        setupProperties();
	}
    
    protected static Logger logCore = null;
	protected static Logger logRateLimiting = null;
	protected static Logger logRegistrazioneMessaggi = null;
	
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
	public static void setupLogger()throws Exception {
		logCore =  LoggerWrapperFactory.getLogger("testsuite.core");
		logRateLimiting =  LoggerWrapperFactory.getLogger("testsuite.rate_limiting");
		logRegistrazioneMessaggi =  LoggerWrapperFactory.getLogger("testsuite.registrazione_messaggi");
		
		Map<String, IProtocolFactory<?>> m = new HashMap<String, IProtocolFactory<?>>();
		m.put("trasparente", Utilities.newInstance("org.openspcoop2.protocol.trasparente.TrasparenteFactory"));
		MapReader<String, IProtocolFactory<?>> map = new MapReader<String, IProtocolFactory<?>>(m, false);
		EsitiProperties.initialize(null, logCore, new Loader(), map);
	}
	
	protected static DbUtils dbUtils;
	
    @Rule public TestName testName = new TestName();

    @Before
    public void before() {
    	logCore.info(
    			"\n###################" +
    			"\nEseguo test: " + this.getClass().getName() + "." + this.testName.getMethodName() +
    			"\n##################"
    		);
    }

	
	@BeforeClass
	public static void setupDbUtils() {
		Map<String, String> dbConfig = new HashMap<>();
		dbConfig.put("username", System.getProperty("db_username"));
		dbConfig.put("password", System.getProperty("db_password"));
		dbConfig.put("url", System.getProperty("db_url"));
		dbConfig.put("driverClassName", System.getProperty("db_driverClassName"));
		String type = System.getProperty("db_type");
		if(type!=null) {
			dbConfig.put("dbType", type);
		}
		dbUtils = new DbUtils(dbConfig);
	}

    @BeforeClass
    public static void setupProperties() {
    
        try(InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(propFileName);) {
            
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            
            for(var p : prop.entrySet()) {
            	System.setProperty((String)p.getKey(), (String)p.getValue());
            }
                        
            setupLogger();

        }catch(Throwable t) {
            throw new RuntimeException(t.getMessage(),t);
        }
    }
    
    public static void main(String [] args) throws Exception {
    	boolean load = true;
    	if(args!=null && args.length>0) {
    		load = Boolean.valueOf(args[0]);
    	}
    	if(load) {
    		prepareConfig();
    	}
    	else {
    		deleteConfig();
    	}
    }
    
    public static void prepareConfig() throws Exception {

        org.slf4j.Logger logger = LoggerWrapperFactory.getLogger("com.intuit.karate");

        String configLoaderPath = prop.getProperty("config_loader_path");
        String scriptPath = configLoaderPath + "/" + (FileUtils.isOsWindows() ? "createOrUpdate.cmd" : "createOrUpdate.sh");
        String trasparenteBundle = new File(trasparenteBundlePath).getAbsolutePath();

        logger.debug("Script path: " + scriptPath);
        logger.debug("Config loader path: " + configLoaderPath);
        logger.debug("trasparente bundle path: " + trasparenteBundle);
        logger.debug("Carico la configurazione su govway...");
        
        org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(scriptPath);
        scriptInvoker.run(new File(configLoaderPath), trasparenteBundle);
        
        // Dopo aver caricato lo script, resetto le cache
        resetCache();
    }
    
    public static void resetCache() throws UtilsException, HttpUtilsException {
        
    	org.slf4j.Logger logger = LoggerWrapperFactory.getLogger("com.intuit.karate");
    	logger.debug("---- resetAllCache ----");
    	
        String jmx_user = prop.getProperty("jmx_username");
        String jmx_pass = prop.getProperty("jmx_password"); 
    	
        String[] govwayCaches = prop.getProperty("jmx_cache_resources").split(",");
        for (String resource : govwayCaches) {
            logger.debug("Resetto cache: " + resource);
            String url = prop.getProperty("govway_base_path") + "/check?methodName=resetCache&resourceName=" + resource;
            org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmx_user, jmx_pass);
        }

    }
    
    public static void resetCache_excludeCachePrimoLivello() throws Exception {
    	resetCache(true, "DatiRichieste");
    }
    
    public static void resetCachePrimoLivello() throws Exception {
    	resetCache(false, "DatiRichieste");
    }
    
    public static void resetCache(boolean useForSkip, String ... cache) throws UtilsException, HttpUtilsException {
        
    	if(cache==null || cache.length<=0) {
    		resetCache();
    		return;
    	}
    	
    	org.slf4j.Logger logger = LoggerWrapperFactory.getLogger("com.intuit.karate");
    	logger.debug("---- resetCache useForSkip:{} cache:{} ----", useForSkip, Arrays.asList(cache));
    	    	
        String jmx_user = prop.getProperty("jmx_username");
        String jmx_pass = prop.getProperty("jmx_password"); 
    	
        String[] govwayCaches = prop.getProperty("jmx_cache_resources").split(",");
        for (String resource : govwayCaches) {
        	boolean found = false;
    		for (String skipc : cache) {
				if(resource.equals(skipc)) {
					found = true;
					break;
				}
			}
        	if(useForSkip && found) {
        		logger.debug("Skip reset cache: " + resource);
				continue;
        	}
        	else if(!useForSkip && !found) {
        		logger.debug("Salto reset cache perchè non richiesto: " + resource);
				continue;
        	}
            logger.debug("Resetto cache: " + resource);
            String url = prop.getProperty("govway_base_path") + "/check?methodName=resetCache&resourceName=" + resource;
            org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmx_user, jmx_pass);
        }

    }
    
    public static void enableCaches(String ...caches) throws UtilsException, HttpUtilsException {
    	org.slf4j.Logger logger = LoggerWrapperFactory.getLogger("com.intuit.karate");
    	logger.debug("---- enableCache  cache:{} ----", Arrays.asList(caches));
    	    	
        String jmxUser = prop.getProperty("jmx_username");
        String jmxPass = prop.getProperty("jmx_password"); 
    	
        String[] govwayCaches = prop.getProperty("jmx_cache_resources").split(",");
        
        Map<String, String> params = Map.of(
        		"methodName", "abilitaCache",
        		CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE, "0",
        		CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE_2, "true",
        		CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE_3, "0",
        		CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE_4, "0"
        );
        String encodedParams = params
        		.entrySet()
        		.stream()
        		.map(e -> e.getKey() + "=" + e.getValue())
        		.collect(Collectors.joining("&"));
        
        for (String resource : govwayCaches) {
    		for (String c : caches) {
				if(resource.equals(c)) {
					logger.debug("abilito cache: {}", resource);
		            String url = prop.getProperty("govway_base_path") + "/check?" + encodedParams + "&resourceName=" + resource;
		            org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmxUser, jmxPass);
					break;
				}
			}
         }
    }
    
    public static void disableCaches(String ...caches) throws UtilsException, HttpUtilsException {
    	org.slf4j.Logger logger = LoggerWrapperFactory.getLogger("com.intuit.karate");
    	logger.debug("---- disableCache  cache:{} ----", Arrays.asList(caches));
    	    	
        String jmxUser = prop.getProperty("jmx_username");
        String jmxPass = prop.getProperty("jmx_password"); 
    	
        String[] govwayCaches = prop.getProperty("jmx_cache_resources").split(",");
        for (String resource : govwayCaches) {
    		for (String c : caches) {
				if(resource.equals(c)) {
					logger.debug("disabilito cache: {}", resource);
		            String url = prop.getProperty("govway_base_path") + "/check?methodName=disabilitaCache&resourceName=" + resource;
		            org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmxUser, jmxPass);
					break;
				}
			}
         }
    }

    public static void deleteConfig() throws Exception {
    	
        String configLoaderPath = prop.getProperty("config_loader_path");
        String scriptPath = configLoaderPath + "/" + (FileUtils.isOsWindows() ? "delete.cmd" : "delete.sh");
        String trasparenteBundle = new File("src/configurazioni-govway/trasparenteTestBundle.zip").getAbsolutePath();
        
        org.slf4j.Logger logger = LoggerWrapperFactory.getLogger("com.intuit.karate");
        
        logger.debug("Script path: " + scriptPath);
        logger.debug("Config loader path: " + configLoaderPath);
        logger.debug("trasparente bundle path: " + trasparenteBundle);
        logger.debug("Elimino la configurazione su govway...");
                
        org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(scriptPath);
        scriptInvoker.run(new File(configLoaderPath), trasparenteBundle);

    }

}
