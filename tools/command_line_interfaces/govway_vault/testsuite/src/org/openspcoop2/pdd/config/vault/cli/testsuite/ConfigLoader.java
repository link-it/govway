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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
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
    public static final String TESTSUITE_BUNDLE_PLAIN_PATH = "src/configurazioni-govway/vaultTestBundle.zip"; // da modificare usando la console senza un policy abilitato
    public static final String TESTSUITE_BUNDLE_PROPRIETA_CIFRATE_PATH = "src/configurazioni-govway/vaultTestBundleProprietaCifrate.zip"; // da modificare usando la console con un policy abilitato
    public static final String TESTSUITE_BUNDLE_PLUGIN_PATH = "src/configurazioni-govway/vaultTestBundleArchiviPluginJar.zip";
    private static final String TESTSUITE_BYOK_PATH = "src/configurazioni-govway/byok.properties";
    
    public static final String SYSTEM_ENC_PROP_NAME = "vaultTestNomeCifrato";
    public static final String SYSTEM_ENC_PROP_PLAIN_VALUE = "==gw-pbkdf2==";
    public static final String SYSTEM_ENC_PROP_ENC_VALUE = "==gw-pbkdf2==.U2FsdGVkX180iN6VYj77ymnPPJMhHJxHSt1sqR4JZlwLqHBmkUfQGymhrFzXxyB9";
    
    private static final String ERROR = "ERROR";
    
    private static final String BUILD_DIR = "build";
    
    private static final String PROPERTIES_DIR = "properties";
    
    private static final String PLAIN = "plain";
    
    private static final String LOG_SCRIPT_PATH = "Script path: " ; 
    
    private static final String CONFIG_LOADER_PATH = "config_loader_path";
    private static final String VAULT_PATH = "govway_vault_path";
    
    public static final String DEFAULT_POLICY = "gw-pbkdf2";
    public static final String GW_KEYS_POLICY = "gw-keys";
    public static final String GW_REMOTE_POLICY = "gw-remote";
    
    public static final String SECURITY_PBKDF2_UNWRAP = "openssl-pbkdf2-unwrap"; 
    public static final String SECURITY_ASYNC_KEYS_UNWRAP = "async-keys-unwrap"; 
    public static final String SECURITY_GOVWAY_REMOTE_UNWRAP = "govway-remote-unwrap";
    
    public static final String KSM_PBKDF2_WRAP = "openssl-pbkdf2-wrap-novariable";
    public static final String KSM_PBKDF2_UNWRAP = "openssl-pbkdf2-unwrap-novariable"; 
    public static final String KSM_ASYNC_KEYS_WRAP = "async-keys-wrap-novariable";
    public static final String KSM_ASYNC_KEYS_UNWRAP = "async-keys-unwrap-novariable"; 
    public static final String KSM_GOVWAY_REMOTE_WRAP = "govway-remote-wrap-novariable";
    public static final String KSM_GOVWAY_REMOTE_UNWRAP = "govway-remote-unwrap-novariable";
    
    public static final String SYSTEM_IN="-system_in";
    public static final String SYSTEM_OUT="-system_out";
    public static final String FILE_IN="-file_in";
    public static final String FILE_OUT="-file_out";
    public static final String SEC="-sec";
    public static final String KSM="-ksm";
    
	public static final String SECURITY_IN="-sec_in";
	public static final String SECURITY_OUT="-sec_out";
	public static final String PLAIN_IN="-plain_in";
	public static final String PLAIN_OUT="-plain_out";
	public static final String REPORT="-report";
    
	private static final String KEYSTORE_EXAMPLE_CLIENT1_P12 = "/etc/govway/keys/xca/ExampleClient1.p12";
	private static final String KEYSTORE_EXAMPLE_CLIENT2_P12 = "/etc/govway/keys/xca/ExampleClient2.p12";
	private static final String KEYSTORE_EXAMPLE_SERVER_P12 = "/etc/govway/keys/xca/ExampleServer.p12";
	private static final String KEYSTORE_SOGGETTO1_JKS = "/etc/govway/keys/soggetto1.jks";
	private static final String KEYSTORE_JCEKS = "/etc/govway/keys/jose_example.jceks";
	public static final String KEYSTORE_JWK = "/etc/govway/keys/testJWKprivate.jwk";
	private static final String KEYSTORE_JWK_SAME_ALIAS_PUBLIC = "/etc/govway/keys/testJWKprivate_sameAliasPublic.jwk";
	public static final String KEYSTORE_BYOK_SUFFIX = ".BYOK";
	private static final List<String> KEYSTORES_BYOK = new ArrayList<>();
	static {
		KEYSTORES_BYOK.add(KEYSTORE_EXAMPLE_CLIENT1_P12);
		KEYSTORES_BYOK.add(KEYSTORE_EXAMPLE_CLIENT2_P12);
		KEYSTORES_BYOK.add(KEYSTORE_EXAMPLE_SERVER_P12);
		KEYSTORES_BYOK.add(KEYSTORE_SOGGETTO1_JKS);
		KEYSTORES_BYOK.add(KEYSTORE_JCEKS);
		KEYSTORES_BYOK.add(KEYSTORE_JWK);
		KEYSTORES_BYOK.add(KEYSTORE_JWK_SAME_ALIAS_PUBLIC);
	}
	public static List<String> getKeystoresBYOK(){
		return KEYSTORES_BYOK;
	}
	
    protected static Properties prop = new Properties();
    static {
        setupProperties();
	}
    
    protected static Logger logCore = null;
    protected static Logger logConsole = null;
	
	public static Logger getLoggerCore() {
		return logCore;
	}
	public static void logCoreInfo(String msg) {
		if(logCore!=null) {
			logCore.info(msg);
		}
		if(msg.startsWith("@")) {
			logConsole(msg);
		}
	}
	
	public static void logConsole(String msg) {
		if(logConsole!=null) {
			logConsole.info(msg);
		}
	}
	
	public static DbUtils getDbUtils() {
		return dbUtils;
	}
		
	@BeforeClass
	public static void setupLogger() {
		logCore =  LoggerWrapperFactory.getLogger("testsuite.core");
		logConsole =  LoggerWrapperFactory.getLogger("testsuite.console");
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
    		prepareConfig(false, null, TESTSUITE_BUNDLE_PLAIN_PATH);
    		prepareConfig(true, DEFAULT_POLICY, TESTSUITE_BUNDLE_PROPRIETA_CIFRATE_PATH);
    		prepareConfig(false, null, TESTSUITE_BUNDLE_PLUGIN_PATH);
    	}
    	else {
    		deleteConfig(TESTSUITE_BUNDLE_PLAIN_PATH);
    		deleteConfig(TESTSUITE_BUNDLE_PROPRIETA_CIFRATE_PATH);
    		deleteConfig(TESTSUITE_BUNDLE_PLUGIN_PATH);
    	}
    }
    
    private static void logDebug(org.slf4j.Logger logger, String msg) {
    	logger.debug(msg);
    }
    
    private static String logScriptExit(org.slf4j.Logger logger, org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker, File ... log) throws FileNotFoundException, UtilsException {
        logDebug(logger,"script - exitCode: " + scriptInvoker.getExitValue());
        logDebug(logger,"script - errorStream: " + scriptInvoker.getErrorStream());
        logDebug(logger,"script - outStream: " + scriptInvoker.getOutputStream());
        
        if(scriptInvoker.getErrorStream()!=null && StringUtils.isNotEmpty(scriptInvoker.getErrorStream())) {
        	String sErrore = scriptInvoker.getErrorStream();
        	String check = checkErrorStream(sErrore);
        	if(check!=null) {
        		return check;
        	}
        }
        
        if(scriptInvoker.getExitValue()!=0) {
        	return "Exit value ("+scriptInvoker.getExitValue()+") <> 0";
        }
      
    	for (File fileLog : log) {
        	String content = FileSystemUtilities.readFile(fileLog);
        	content = filterERROR(content);
        	if(content.contains(ERROR)) {
        		return "Error found in log file '"+fileLog.getAbsolutePath()+"': "+extractSurroundingCharacters(content, ERROR, 300, 300);
        	}	
		}
        
        return null;
    }
    private static String checkErrorStream(String sErrore) {
    	List<String> lines = Arrays.asList(sErrore.split("\n"));
    	StringBuilder sb = new StringBuilder();
    	for (String s : lines) {
			if(!s.startsWith("WARNING: An illegal reflective access operation has occurred") &&
					!s.startsWith("WARNING: Illegal reflective access by org.openspcoop2.utils.Utilities") &&
					!s.startsWith("WARNING: Please consider reporting this to the maintainers of org.openspcoop2.utils.Utilities") &&
					!s.startsWith("WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations") &&
					!s.startsWith("WARNING: All illegal access operations will be denied in a future release")) {
				if(sb.length()>0) {
					sb.append("\n");
				}
				sb.append(s);
			}
		}
    	if(sb.length()>0) {
    		return "Error stream found small:"+sb.toString()+"\n full: "+sErrore;
    	}
    	return null;
    }
    public static String extractSurroundingCharacters(String input, String searchString, int x, int y) throws IllegalArgumentException {
        int index = input.indexOf(searchString);
        
        if (index == -1) {
            throw new IllegalArgumentException("Stringa di ricerca non trovata.");
        }

        int start = Math.max(0, index - x);
        int end = Math.min(input.length(), index + searchString.length() + y);

        return input.substring(start, end);
    }
    private static String filterERROR(String content) {
    	List<String> lines = Arrays.asList(content.split("\n"));
    	StringBuilder sb = new StringBuilder();
    	for (String s : lines) {
			if( 
					!(
							s.startsWith(ERROR) && 
							(
									s.contains("Errore durante l'eliminazione del soggetto [gw/ENTE]")
									||
									s.contains("Errore durante l'eliminazione del soggetto [gw/SoggettoInternoVaultTest]")
									||
									s.contains("Errore durante l'eliminazione del soggetto [gw/SoggettoInternoVaultTestFruitore]")
									||
									s.contains("Errore durante l'eliminazione del soggetto [modipa/ENTE]") 
									||
									s.contains("Errore durante l'eliminazione del soggetto [modipa/SoggettoInternoVaultTest]")
									||
									s.contains("Errore durante l'eliminazione del soggetto [modipa/SoggettoInternoVaultTestFruitore]")
									||
									s.contains("Errore durante l'eliminazione del soggetto [spc/ENTE]")
									||
									s.contains("Errore durante l'eliminazione del soggetto [spc/SoggettoInternoVaultTest]")
									||
									s.contains("Errore durante l'eliminazione del soggetto [spc/SoggettoInternoVaultTestFruitore]")
									||
									s.contains("Porta di Dominio [PdDENTE] non è eliminabile poichè di dominio interno")
							)
					)
			) {
				if(sb.length()>0) {
					sb.append("\n");
				}
				sb.append(s);
			}
		}
    	return sb.toString();
    }
    
    public static void checkExternalToolError() throws UtilsException {
    	if(prepareConfigError!=null) {
    		throw new UtilsException("Rilevata precedente esecuzione di 'load config' fallita: "+prepareConfigError);
    	}
    	if(prepareDeleteError!=null) {
    		throw new UtilsException("Rilevata precedente esecuzione di 'delete config' fallita: "+prepareDeleteError);
    	}
    	if(vaultSecretError!=null) {
    		throw new UtilsException("Rilevata precedente esecuzione del vault fallita: "+vaultSecretError);
    	}
    	if(encryptDecryptError!=null) {
    		throw new UtilsException("Rilevata precedente esecuzione di operazioni di cifratura/decifratura fallita: "+encryptDecryptError);
    	}
    }
    
    
    
    // LOADER
        
    private static String prepareConfigError = null;
    public static String getPrepareConfigError() {
		return prepareConfigError;
	}
	public static void prepareConfig(boolean byok, String policy, String testBundle) throws UtilsException, HttpUtilsException, IOException {

		checkExternalToolError();
		
        org.slf4j.Logger logger = logCore!=null ? logCore : LoggerWrapperFactory.getLogger(ConfigLoader.class);

        String configLoaderPath = prop.getProperty(CONFIG_LOADER_PATH);
        String scriptPath = configLoaderPath + File.separatorChar + (SystemUtils.IS_OS_WINDOWS ? "createOrUpdate.cmd" : "createOrUpdate.sh");
        String testsuiteBundle = new File(testBundle).getAbsolutePath();

        logDebug(logger,LOG_SCRIPT_PATH+ scriptPath);
        logDebug(logger,"Config loader path: " + configLoaderPath);
        logDebug(logger,"testsuite bundle path: " + testsuiteBundle);
        logDebug(logger,"Carico la configurazione su govway...");

        if(byok && policy!=null) {
        	enableByokConfigLoader(policy);
        }
        else {
        	disableByokConfigLoader();
        }
        
        File log = new File(configLoaderPath + File.separatorChar + "log"+ File.separatorChar + "govway_cli_configLoader.log");
        File logSql = new File(configLoaderPath + File.separatorChar + "log"+ File.separatorChar + "govway_cli_configLoader_sql.log");
        File logAuditing = new File(configLoaderPath + File.separatorChar + "log"+ File.separatorChar + "govway_cli_configLoader_auditing.log");
        
        FileSystemUtilities.clearFile(log);
        FileSystemUtilities.clearFile(logSql);
        FileSystemUtilities.clearFile(logAuditing);
        
        org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(scriptPath);
        scriptInvoker.run(new File(configLoaderPath), testsuiteBundle);
        
        prepareConfigError = logScriptExit(logger, scriptInvoker, 
        		log, logSql, logAuditing);
        
        // Dopo aver caricato lo script, resetto le cache
        resetCache();
    }
    
	private static String prepareDeleteError = null;
    public static String getPrepareDeleteError() {
		return prepareDeleteError;
	}
	public static void deleteConfig(String testBundle) throws UtilsException, IOException {
    	
		checkExternalToolError();
		
        String configLoaderPath = prop.getProperty(CONFIG_LOADER_PATH);
        String scriptPath = configLoaderPath + File.separatorChar + (SystemUtils.IS_OS_WINDOWS ? "delete.cmd" : "delete.sh");
        String trasparenteBundle = new File(testBundle).getAbsolutePath();
        
        org.slf4j.Logger logger = logCore!=null ? logCore : LoggerWrapperFactory.getLogger(ConfigLoader.class);
        
        logDebug(logger,LOG_SCRIPT_PATH+ scriptPath);
        logDebug(logger,"Config loader path: " + configLoaderPath);
        logDebug(logger,"trasparente bundle path: " + trasparenteBundle);
        logDebug(logger,"Elimino la configurazione su govway...");
        
        enableByokConfigLoader(DEFAULT_POLICY);
        
        String logPropsPath = configLoaderPath + File.separatorChar + PROPERTIES_DIR + File.separatorChar + "config_loader.cli.log4j2.properties";
        setLog(100, logPropsPath);
        logPropsPath = configLoaderPath + File.separatorChar + PROPERTIES_DIR + File.separatorChar + "console.audit.log4j2.properties";
        setLog(100, logPropsPath);
        
        File log = new File(configLoaderPath + File.separatorChar + "log"+ File.separatorChar + "govway_cli_configLoader.log");
        File logSql = new File(configLoaderPath + File.separatorChar + "log"+ File.separatorChar + "govway_cli_configLoader_sql.log");
        File logAuditing = new File(configLoaderPath + File.separatorChar + "log"+ File.separatorChar + "govway_cli_configLoader_auditing.log");
        
        FileSystemUtilities.clearFile(log);
        FileSystemUtilities.clearFile(logSql);
        FileSystemUtilities.clearFile(logAuditing);
        
        org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(scriptPath);
        scriptInvoker.run(new File(configLoaderPath), trasparenteBundle);
        
        prepareDeleteError = logScriptExit(logger, scriptInvoker, 
        		log, logSql, logAuditing);

    }
    
    public static void disableByokConfigLoader() throws IOException, UtilsException {
    	setConf(true, null, "config_loader");
    }
    public static void enableByokConfigLoader(String policy) throws IOException, UtilsException {
    	setConf(false, policy, "config_loader");
    }
    
    
    
    // VAULT
    
    private static String vaultSecretError = null;
    public static String getVaultSecretError() {
		return vaultSecretError;
	}
    public static void vaultSecrets(String srcPolicy, String destPolicy, boolean report) throws UtilsException, HttpUtilsException, FileNotFoundException {

    	checkExternalToolError();
    	
        org.slf4j.Logger logger = logCore!=null ? logCore : LoggerWrapperFactory.getLogger(ConfigLoader.class);

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
        	org.openspcoop2.utils.resources.FileSystemUtilities.deleteFile(reportPath);
        }
        logDebug(logger,"Vault report '"+((reportPath!=null) ? reportPath.getAbsolutePath() : "none")+"'");
        
        String logPropsPath = vaultPath + File.separatorChar + PROPERTIES_DIR + File.separatorChar + "govway_vault.cli.log4j2.properties";
        setLog(100, logPropsPath);
        
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
        
        File log = new File(vaultPath + File.separatorChar + "log"+ File.separatorChar + "govway_cli_vault.log");
         
        FileSystemUtilities.clearFile(log);
        
        logDebug(logger,"Vault params: "+params);
        
        FileSystemUtilities.clearFile(log);org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(scriptPath);
        scriptInvoker.run(new File(vaultPath), params.toArray(new String[1]));
        
        vaultSecretError = logScriptExit(logger, scriptInvoker,
        		log);
        
        // Dopo attuato l'operazione, resetto le cache
        resetCache();
    }
    
    
    
    
    
    

    
    
    // ENCRYPT/DECRYPT
    
    private static String encryptDecryptError = null;
    public static String getEncryptDecryptError() {
		return encryptDecryptError;
	}
    public static String vaultEncryptBySystem(String idPolicy, boolean security, String text) throws UtilsException, FileNotFoundException {
    	return vaultEncryptDecrypt(true, idPolicy, security, text, null, true);	
    }
    public static String vaultDecryptBySystem(String idPolicy, boolean security, String text) throws UtilsException, FileNotFoundException {
    	return vaultEncryptDecrypt(false, idPolicy, security, text, null, true);	
    }
    public static String vaultEncryptByFile(String idPolicy, boolean security, String fileIn, String fileOut) throws UtilsException, FileNotFoundException {
    	return vaultEncryptDecrypt(true, idPolicy, security, fileIn, fileOut, false);	
    }
    public static String vaultDecryptByFile(String idPolicy, boolean security, String fileIn, String fileOut) throws UtilsException, FileNotFoundException {
    	return vaultEncryptDecrypt(false, idPolicy, security, fileIn, fileOut, false);	
    }
    private static String vaultEncryptDecrypt(boolean encrypt, String idPolicy, boolean security, String fileIn, String fileOut, boolean system) throws UtilsException, FileNotFoundException {

    	checkExternalToolError();
    	
        org.slf4j.Logger logger = logCore!=null ? logCore : LoggerWrapperFactory.getLogger(ConfigLoader.class);

        String encrytpDecryptPath = prop.getProperty(VAULT_PATH);
        String command = encrypt ? "encrypt" : "decrypt";
        String scriptPath = encrytpDecryptPath + File.separatorChar + (SystemUtils.IS_OS_WINDOWS ? command+".cmd" : command+".sh");
        
        logDebug(logger,LOG_SCRIPT_PATH+ scriptPath);
        logDebug(logger,"Encrypt/Decrypt path: " + encrytpDecryptPath);
        logDebug(logger,"Encrypt/Decrypt idPolicy '"+idPolicy+"'");
        logDebug(logger,"Encrypt/Decrypt security '"+security+"'");
               
        String logPropsPath = encrytpDecryptPath + File.separatorChar + PROPERTIES_DIR + File.separatorChar + "govway_vault.cli.log4j2.properties";
        setLog(100, logPropsPath);
        
        List<String> params = new ArrayList<>();
        if(system) {
        	params.add(SYSTEM_IN+"="+fileIn);
        }
        else {
        	params.add(FILE_IN+"="+fileIn);
        }
        if(system) {
        	params.add(SYSTEM_OUT);
        }
        else {
        	params.add(FILE_OUT+"="+fileOut);
        }
        if(idPolicy!=null) {
	        if(security) {
	        	params.add(SEC+"="+idPolicy);
	        }
	        else {
	        	params.add(KSM+"="+idPolicy);
	        }
        }
        
        File log = new File(encrytpDecryptPath + File.separatorChar + "log"+ File.separatorChar + "govway_cli_vault.log");
         
        FileSystemUtilities.clearFile(log);
        
        logDebug(logger,"Encrypt/Decrypt params: "+params);
        
        FileSystemUtilities.clearFile(log);org.openspcoop2.utils.resources.ScriptInvoker scriptInvoker = new org.openspcoop2.utils.resources.ScriptInvoker(scriptPath);
        scriptInvoker.run(new File(encrytpDecryptPath), params.toArray(new String[1]));
        
        vaultSecretError = logScriptExit(logger, scriptInvoker,
        		log);
        
        if(system) {
        	return scriptInvoker.getOutputStream();
        }
        return null;
        
    }
    
    
    
    
    
    // Utilities
    
    private static void setLog(int size, String path) throws FileNotFoundException, UtilsException {
    	String logPath = new File(path).getAbsolutePath();
    	String contentLog = org.openspcoop2.utils.resources.FileSystemUtilities.readFile(logPath);
    	if(isOrigSize(contentLog)) {
    		contentLog = setSize(size, contentLog);
    		org.openspcoop2.utils.resources.FileSystemUtilities.deleteFile(new File(path));
    		org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(path, contentLog.getBytes());
    	}
    }
    private static String setSize(int size, String contentLog) {
    	String sizeString = "="+size+"MB";
    	contentLog = contentLog.replace("=1MB", sizeString);
    	contentLog = contentLog.replace("= 1MB", sizeString);
    	contentLog = contentLog.replace(" = 1MB", sizeString);
    	return contentLog;
    }
    private static boolean isOrigSize(String contentLog) {
    	return contentLog.contains("=1MB") || contentLog.contains("= 1MB") || contentLog.contains(" = 1MB");
    }
    
    private static void setConf(boolean disable, String policy, String tool) throws IOException, UtilsException {
    	
    	String byokPath = new File(TESTSUITE_BYOK_PATH).getAbsolutePath();
    	String contentByok = org.openspcoop2.utils.resources.FileSystemUtilities.readFile(byokPath);
    	
    	if(disable) {
    		contentByok = contentByok.replace("govway.security=gw-pbkdf2", "# !!DISABLED!!\n#govway.security=gw-pbkdf2");
    	}
    	else {
    		contentByok = contentByok.replace("govway.security=gw-pbkdf2", "# !!ENABLED!!\ngovway.security="+policy);
    	}
    	
    	contentByok = activeConf("policy", contentByok); 
    	contentByok = activeConf("plugin", contentByok); 
    	contentByok = activeConf("configurazioneGenerale", contentByok); 
    	
    	File byokFilePath = disable ? new File(BUILD_DIR,"byok-disable.properties") : new File(BUILD_DIR,"byok-enable-"+policy+".properties");
    	org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(byokFilePath, contentByok.getBytes());
    	
    	String configLoaderPath = prop.getProperty(CONFIG_LOADER_PATH);
        String scriptProperties = configLoaderPath + File.separatorChar + PROPERTIES_DIR + File.separatorChar  + tool+".cli.properties";
        Properties p = new Properties();
        String content = org.openspcoop2.utils.resources.FileSystemUtilities.readFile(scriptProperties);
        try(ByteArrayInputStream bin = new ByteArrayInputStream(content.getBytes())){
        	p.load(bin);
        }
    	String v = p.getProperty("byok.config");
    	if(v==null || !v.equals(byokPath)) {
    		String newContent = content.replaceAll("byok.config", "#byok.config");
    		newContent += "\n\nbyok.config="+byokFilePath.getAbsolutePath();
    		org.openspcoop2.utils.resources.FileSystemUtilities.deleteFile(new File(scriptProperties));
    		org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(scriptProperties, newContent.getBytes());
    	}
    }
    private static String activeConf(String name, String contentByok) {
    	String enabled = ".enable=true";
    	contentByok = contentByok.replace(name+".enable=false", name+enabled);
    	contentByok = contentByok.replace(name+".enable= false", name+enabled);
    	contentByok = contentByok.replace(name+".enable= false ", name+enabled);
    	contentByok = contentByok.replace(name+".enable = false", name+enabled);
    	contentByok = contentByok.replace(name+".enable =false", name+enabled);
    	return contentByok;
    }
    
    
    
    // Caches
    
    public static void resetCache() throws UtilsException, HttpUtilsException {
        
    	org.slf4j.Logger logger = logCore!=null ? logCore : LoggerWrapperFactory.getLogger(ConfigLoader.class);
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
    	
    	org.slf4j.Logger logger = logCore!=null ? logCore : LoggerWrapperFactory.getLogger(ConfigLoader.class);
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
