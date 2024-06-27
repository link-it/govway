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

package org.openspcoop2.pdd.config.vault.cli.testsuite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.transport.http.HttpUtilsException;
import org.slf4j.Logger;

/**
* ConfigLoader
*
* @author Andrea Poli (andrea.poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ConfigLoader {

    public static final String PROP_FILE_NAME = "testsuite.properties";
    private static final String TESTSUITE_BUNDLE_PATH = "src/configurazioni-govway/vaultTestBundle.zip";
    private static final String TESTSUITE_BYOK_PATH = "src/configurazioni-govway/byok.properties";
    
    private static final String BUILD_DIR = "build";
    
    private static final String PLAIN = "plain";
    
    private static final String LOG_SCRIPT_PATH = "Script path: " ; 
    
    private static final String CONFIG_LOADER_PATH = "config_loader_path";
    private static final String VAULT_PATH = "govway_vault_path";
    
    public static final String DEFAULT_POLICY = "gw-pbkdf2";
    public static final String GW_KEYS_POLICY = "gw-keys";
    
	public static final String SECURITY_IN="-sec_in";
	public static final String SECURITY_OUT="-sec_out";
	public static final String PLAIN_IN="-plain_in";
	public static final String PLAIN_OUT="-plain_out";
	public static final String REPORT="-report";
    
    protected static Properties prop = new Properties();
    static {
        setupProperties();
	}
    
    protected static Logger logCore = null;
	
	public static Logger getLoggerCore() {
		return logCore;
	}
	public static void logCoreInfo(String msg) {
		if(logCore!=null) {
			logCore.info(msg);
		}
	}
	
	public static DbUtils getDbUtils() {
		return dbUtils;
	}
		
	@BeforeClass
	public static void setupLogger() {
		logCore =  LoggerWrapperFactory.getLogger("testsuite.core");
	}
	
	protected static DbUtils dbUtils;
	
    @Rule public TestName testName = new TestName();

    @Before
    public void before() {
    	logCoreInfo(
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
    
        try(InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(PROP_FILE_NAME);) {
            
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + PROP_FILE_NAME + "' not found in the classpath");
            }
            
            for(var p : prop.entrySet()) {
            	System.setProperty((String)p.getKey(), (String)p.getValue());
            }
                        
            setupLogger();

        }catch(Exception t) {
            throw new UtilsRuntimeException(t.getMessage(),t);
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
    
    private static void logDebug(org.slf4j.Logger logger, String msg) {
    	logger.debug(msg);
    }
    
    
    
    // LOADER
    
    public static void prepareConfig() throws UtilsException, HttpUtilsException, IOException {

        org.slf4j.Logger logger = LoggerWrapperFactory.getLogger(ConfigLoader.class);

        String configLoaderPath = prop.getProperty(CONFIG_LOADER_PATH);
        String scriptPath = configLoaderPath + File.separatorChar + (SystemUtils.IS_OS_WINDOWS ? "createOrUpdate.cmd" : "createOrUpdate.sh");
        String testsuiteBundle = new File(TESTSUITE_BUNDLE_PATH).getAbsolutePath();

        logDebug(logger,LOG_SCRIPT_PATH+ scriptPath);
        logDebug(logger,"Config loader path: " + configLoaderPath);
        logDebug(logger,"testsuite bundle path: " + testsuiteBundle);
        logDebug(logger,"Carico la configurazione su govway...");

        disableByokConfigLoader();
        
        org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(scriptPath);
        scriptInvoker.run(new File(configLoaderPath), testsuiteBundle);
        
        // Dopo aver caricato lo script, resetto le cache
        resetCache();
    }
    
    public static void deleteConfig() throws UtilsException {
    	
        String configLoaderPath = prop.getProperty(CONFIG_LOADER_PATH);
        String scriptPath = configLoaderPath + File.separatorChar + (SystemUtils.IS_OS_WINDOWS ? "delete.cmd" : "delete.sh");
        String trasparenteBundle = new File("src/configurazioni-govway/trasparenteTestBundle.zip").getAbsolutePath();
        
        org.slf4j.Logger logger = LoggerWrapperFactory.getLogger(ConfigLoader.class);
        
        logDebug(logger,LOG_SCRIPT_PATH+ scriptPath);
        logDebug(logger,"Config loader path: " + configLoaderPath);
        logDebug(logger,"trasparente bundle path: " + trasparenteBundle);
        logDebug(logger,"Elimino la configurazione su govway...");
                
        org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(scriptPath);
        scriptInvoker.run(new File(configLoaderPath), trasparenteBundle);

    }
    
    public static void disableByokConfigLoader() throws IOException, UtilsException {
    	setConf(true, null, "config_loader");
    }
    
    
    
    // VAULT
    
    public static void vaultSecrets(String srcPolicy, String destPolicy, boolean report) throws UtilsException, HttpUtilsException {

        org.slf4j.Logger logger = LoggerWrapperFactory.getLogger(ConfigLoader.class);

        String vaultPath = prop.getProperty(VAULT_PATH);
        String scriptPath = vaultPath + File.separatorChar + (SystemUtils.IS_OS_WINDOWS ? "update.cmd" : "update.sh");
        
        logDebug(logger,LOG_SCRIPT_PATH+ scriptPath);
        logDebug(logger,"Vault path: " + vaultPath);
        logDebug(logger,"Vault src-policy '"+((srcPolicy!=null) ? srcPolicy : PLAIN)+"'");
        logDebug(logger,"Vault dest-policy '"+((destPolicy!=null) ? destPolicy : PLAIN)+"'");
        File reportPath = null;
        if(report) {
        	reportPath = new File(BUILD_DIR,"report-in_"+
        			((srcPolicy!=null) ? srcPolicy : PLAIN)
        			+"-out_"+
        			((destPolicy!=null) ? destPolicy : PLAIN)
        			+".txt");
        }
        logDebug(logger,"Vault report '"+((reportPath!=null) ? reportPath.getAbsolutePath() : "none")+"'");
        
        List<String> params = new ArrayList<>();
        if(srcPolicy!=null) {
        	params.add(SECURITY_IN+"="+srcPolicy);
        }
        else {
        	params.add(PLAIN_IN);
        }
        if(destPolicy!=null) {
        	params.add(SECURITY_OUT+"="+destPolicy);
        }
        else {
        	params.add(PLAIN_OUT);
        }
        if(reportPath!=null) {
        	params.add(REPORT+"="+reportPath.getAbsolutePath());
        }
        
        org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(scriptPath);
        scriptInvoker.run(new File(vaultPath), params.toArray(new String[1]));
        
        // Dopo attuato l'operazione, resetto le cache
        resetCache();
    }
    
    
    
    // Utilities
    
    private static void setConf(boolean disable, String policy, String tool) throws IOException, UtilsException {
    	
    	String byokPath = new File(TESTSUITE_BYOK_PATH).getAbsolutePath();
    	String contentByok = org.openspcoop2.utils.resources.FileSystemUtilities.readFile(byokPath);
    	contentByok = contentByok.replace("govway.security=gw-pbkdf2", "#govway.security=gw-pbkdf2");
    	File byokDisablePath = disable ? new File(BUILD_DIR,"byok-disable.properties") : new File(BUILD_DIR,"byok-enable-"+policy+".properties");
    	org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(byokDisablePath, contentByok.getBytes());
    	
    	String configLoaderPath = prop.getProperty(CONFIG_LOADER_PATH);
        String scriptProperties = configLoaderPath + File.separatorChar + "properties" + File.separatorChar  + tool+".cli.properties";
        Properties p = new Properties();
        String content = org.openspcoop2.utils.resources.FileSystemUtilities.readFile(scriptProperties);
        try(ByteArrayInputStream bin = new ByteArrayInputStream(content.getBytes())){
        	p.load(bin);
        }
    	String v = p.getProperty("byok.config");
    	if(v==null || !v.equals(byokPath)) {
    		String newContent = content.replaceAll("byok.config", "#byok.config");
    		newContent += "\n\nbyok.config="+byokDisablePath.getAbsolutePath();
    		org.openspcoop2.utils.resources.FileSystemUtilities.deleteFile(new File(scriptProperties));
    		org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(scriptProperties, newContent.getBytes());
    	}
    }
    
    
    
    // Caches
    
    public static void resetCache() throws UtilsException, HttpUtilsException {
        
    	org.slf4j.Logger logger = LoggerWrapperFactory.getLogger(ConfigLoader.class);
    	logDebug(logger,"---- resetAllCache ----");
    	
        String jmxUser = prop.getProperty("jmx_username");
        String jmxPass = prop.getProperty("jmx_password"); 
    	
        String[] govwayCaches = prop.getProperty("jmx_cache_resources").split(",");
        for (String resource : govwayCaches) {
            logDebug(logger,"Resetto cache: " + resource);
            String url = prop.getProperty("govway_base_path") + "/check?methodName=resetCache&resourceName=" + resource;
            org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmxUser, jmxPass);
        }

    }
    
    public static void resetCacheExcludeCachePrimoLivello() throws UtilsException, HttpUtilsException {
    	resetCache(true, "DatiRichieste");
    }
    
    public static void resetCachePrimoLivello() throws UtilsException, HttpUtilsException {
    	resetCache(false, "DatiRichieste");
    }
    
    public static void resetCache(boolean useForSkip, String ... cache) throws UtilsException, HttpUtilsException {
        
    	if(cache==null || cache.length<=0) {
    		resetCache();
    		return;
    	}
    	
    	org.slf4j.Logger logger =  LoggerWrapperFactory.getLogger(ConfigLoader.class);
    	logDebug(logger,"---- resetCache useForSkip:"+useForSkip+" cache:"+Arrays.asList(cache)+" ----");
    	    	
        String jmxUser = prop.getProperty("jmx_username");
        String jmxPass = prop.getProperty("jmx_password"); 
    	
        String[] govwayCaches = prop.getProperty("jmx_cache_resources").split(",");
        for (String resource : govwayCaches) {
        	resetCache(logger, useForSkip, resource, 
            		jmxUser, jmxPass,
            		cache);
        }

    }
    private static void resetCache(org.slf4j.Logger logger, boolean useForSkip, String resource, 
    		String jmxUser, String jmxPass,
    		String ... cache) throws UtilsException, HttpUtilsException {
    	boolean found = false;
		for (String skipc : cache) {
			if(resource.equals(skipc)) {
				found = true;
				break;
			}
		}
    	if(useForSkip && found) {
    		logDebug(logger,"Skip reset cache: " + resource);
			return;
    	}
    	else if(!useForSkip && !found) {
    		logDebug(logger,"Salto reset cache perchè non richiesto: " + resource);
    		return;
    	}
        logDebug(logger,"Resetto cache: " + resource);
        String url = prop.getProperty("govway_base_path") + "/check?methodName=resetCache&resourceName=" + resource;
        org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmxUser, jmxPass);
    }

}
