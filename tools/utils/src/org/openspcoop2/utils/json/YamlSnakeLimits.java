/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.json;

import java.io.InputStream;
import java.util.Properties;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**	
 * YamlSnakeLimits
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class YamlSnakeLimits {

	private static boolean DEBUG = false;
	public static boolean isDEBUG() {
		return DEBUG;
	}
	public static void setDEBUG(boolean dEBUG) {
		DEBUG = dEBUG;
	}
	
	private static Logger log = LoggerWrapperFactory.getLogger(YamlSnakeLimits.class);
	
	private static boolean initialized = false;
	public static void initialize() {
		_initialize();
	}
	private static synchronized void _initialize() {
		 if(initialized==false) {
			 try {
				 try(InputStream is = YamlSnakeLimits.class.getResourceAsStream("/org/openspcoop2/utils/json/yamlSnakeLimits.properties")){
					 Properties p = new Properties();
					 p.load(is);
					 initialize(log, p);
				 }
			 }catch(Throwable t) {
				 log.error(t.getMessage(),t);
			 }
			 initialized = true;
		 }
	}
    public static void initialize(Logger logger, Properties p) {
    	if(p!=null) {
    		
    		String pName = "allowDuplicateKeys";
    		try {
	    		String tmp = p.getProperty(pName);
	    		if(tmp!=null) {
	    			tmp = tmp.trim();
	    			boolean v = Boolean.parseBoolean(tmp);
	    			org.yaml.snakeyaml.LoaderOptions.getDefaultValues().setAllowDuplicateKeys(v);
	    			logger.info("[YamlSnakeLimits] "+pName+"="+
	    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().isAllowDuplicateKeys());
	    			if(DEBUG) {
	    				System.out.println("[YamlSnakeLimits] "+pName+"="+
	    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().isAllowDuplicateKeys());
	    			}
	    		}
    		}catch(Throwable t) {
    			logger.error("["+pName+"] Invalid limit value: "+t.getMessage(),t);
    		}
    		
    		pName = "wrappedToRootException";
    		try {
	    		String tmp = p.getProperty(pName);
	    		if(tmp!=null) {
	    			tmp = tmp.trim();
	    			boolean v = Boolean.parseBoolean(tmp);
	    			org.yaml.snakeyaml.LoaderOptions.getDefaultValues().setWrappedToRootException(v);
	    			logger.info("[YamlSnakeLimits] "+pName+"="+
    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().isWrappedToRootException());
	    			if(DEBUG) {
	    				System.out.println("[YamlSnakeLimits] "+pName+"="+
	    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().isWrappedToRootException());
	    			}
	    		}
    		}catch(Throwable t) {
    			logger.error("["+pName+"] Invalid limit value: "+t.getMessage(),t);
    		}
    		
    		pName = "maxAliasesForCollections";
    		try {
	    		String tmp = p.getProperty(pName);
	    		if(tmp!=null) {
	    			tmp = tmp.trim();
	    			int v = Integer.valueOf(tmp);
	    			org.yaml.snakeyaml.LoaderOptions.getDefaultValues().setMaxAliasesForCollections(v);
	    			logger.info("[YamlSnakeLimits] "+pName+"="+
    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().getMaxAliasesForCollections());
	    			if(DEBUG) {
	    				System.out.println("[YamlSnakeLimits] "+pName+"="+
	    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().getMaxAliasesForCollections());
	    			}
	    		}
    		}catch(Throwable t) {
    			logger.error("["+pName+"] Invalid limit value: "+t.getMessage(),t);
    		}

    		pName = "allowRecursiveKeys";
    		try {
	    		String tmp = p.getProperty(pName);
	    		if(tmp!=null) {
	    			tmp = tmp.trim();
	    			boolean v = Boolean.parseBoolean(tmp);
	    			org.yaml.snakeyaml.LoaderOptions.getDefaultValues().setAllowRecursiveKeys(v);
	    			logger.info("[YamlSnakeLimits] "+pName+"="+
    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().isAllowRecursiveKeys());
	    			if(DEBUG) {
	    				System.out.println("[YamlSnakeLimits] "+pName+"="+
	    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().isAllowRecursiveKeys());
	    			}
	    		}
    		}catch(Throwable t) {
    			logger.error("["+pName+"] Invalid limit value: "+t.getMessage(),t);
    		}
    		
    		pName = "processComments";
    		try {
	    		String tmp = p.getProperty(pName);
	    		if(tmp!=null) {
	    			tmp = tmp.trim();
	    			boolean v = Boolean.parseBoolean(tmp);
	    			org.yaml.snakeyaml.LoaderOptions.getDefaultValues().setProcessComments(v);
	    			logger.info("[YamlSnakeLimits] "+pName+"="+
    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().isProcessComments());
	    			if(DEBUG) {
	    				System.out.println("[YamlSnakeLimits] "+pName+"="+
	    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().isProcessComments());
	    			}
	    		}
    		}catch(Throwable t) {
    			logger.error("["+pName+"] Invalid limit value: "+t.getMessage(),t);
    		}
    		
    		pName = "enumCaseSensitive";
    		try {
	    		String tmp = p.getProperty(pName);
	    		if(tmp!=null) {
	    			tmp = tmp.trim();
	    			boolean v = Boolean.parseBoolean(tmp);
	    			org.yaml.snakeyaml.LoaderOptions.getDefaultValues().setEnumCaseSensitive(v);
	    			logger.info("[YamlSnakeLimits] "+pName+"="+
    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().isEnumCaseSensitive());
	    			if(DEBUG) {
	    				System.out.println("[YamlSnakeLimits] "+pName+"="+
	    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().isEnumCaseSensitive());
	    			}
	    		}
    		}catch(Throwable t) {
    			logger.error("["+pName+"] Invalid limit value: "+t.getMessage(),t);
    		}

    		pName = "nestingDepthLimit";
    		try {
	    		String tmp = p.getProperty(pName);
	    		if(tmp!=null) {
	    			tmp = tmp.trim();
	    			int v = Integer.valueOf(tmp);
	    			org.yaml.snakeyaml.LoaderOptions.getDefaultValues().setNestingDepthLimit(v);
	    			logger.info("[YamlSnakeLimits] "+pName+"="+
    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().getNestingDepthLimit());
	    			if(DEBUG) {
	    				System.out.println("[YamlSnakeLimits] "+pName+"="+
	    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().getNestingDepthLimit());
	    			}
	    		}
    		}catch(Throwable t) {
    			logger.error("["+pName+"] Invalid limit value: "+t.getMessage(),t);
    		}
    		
    		pName = "codePointLimit";
    		try {
	    		String tmp = p.getProperty(pName);
	    		if(tmp!=null) {
	    			tmp = tmp.trim();
	    			int v = Integer.valueOf(tmp);
	    			org.yaml.snakeyaml.LoaderOptions.getDefaultValues().setCodePointLimit(v);
	    			logger.info("[YamlSnakeLimits] "+pName+"="+
    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().getCodePointLimit());
	    			if(DEBUG) {
	    				System.out.println("[YamlSnakeLimits] "+pName+"="+
	    						org.yaml.snakeyaml.LoaderOptions.getDefaultValues().getCodePointLimit());
	    			}
	    		}
    		}catch(Throwable t) {
    			logger.error("["+pName+"] Invalid limit value: "+t.getMessage(),t);
    		}
    		
    		initialized = true; // devo indicare al costruttore di default di non attivarsi, poiche' e' stata effettuata una configurazione custom
    		
    	}
	}
}
