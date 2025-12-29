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
package org.openspcoop2.utils;

/**
 * Costanti
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	private Costanti() {}

	public static final String OPENSPCOOP2 = "GovWay";
	
	public static final String OPENSPCOOP2_LOCAL_HOME = "GOVWAY_HOME";
	public static final String OPENSPCOOP2_FORCE_CONFIG_FILE = "GOVWAY_FORCE_CONFIG_FILE";
	
	public static final String OPENSPCOOP2_LOOKUP = "GOVWAY_LOOKUP";
	 
    /** Versione beta, es: "b1" */
    public static final String OPENSPCOOP2_BETA = ".18"; /**".0.rc1";*/
    /** Versione di OpenSPCoop */
    public static final String OPENSPCOOP2_VERSION = "3.3"+Costanti.OPENSPCOOP2_BETA;
    /** Versione di OpenSPCoop */
    public static final String OPENSPCOOP2_PRODUCT = "GovWay";
    /** Versione di OpenSPCoop (User-Agent) */
    public static final String OPENSPCOOP2_PRODUCT_VERSION = Costanti.OPENSPCOOP2_PRODUCT+"/"+Costanti.OPENSPCOOP2_VERSION;
    /** Details */
    public static final String OPENSPCOOP2_DETAILS = "www.govway.org";
    /** Copyright */
	public static final String OPENSPCOOP2_COPYRIGHT = "2005-2025 Link.it srl";
	 /** License */
	public static final String OPENSPCOOP2_LICENSE = "This program is free software: you can redistribute it and/or modify\n"+
	"it under the terms of the GNU General Public License version 3, as published by\n"+
	"the Free Software Foundation.\n"+
	"\n"+
	"This program is distributed in the hope that it will be useful,\n"+
	"but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
	"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
	"GNU General Public License for more details.\n"+
	"\n"+
	"You should have received a copy of the GNU General Public License\n"+
	"along with this program.  If not, see <http://www.gnu.org/licenses/>.";
	
	/* COSTANTI PER PILOTARE LA CONFIGURAIZONE DEI LOGGERS */
	
	// nome variabili inviate ai pattern dei loggers
	public static final String VAR_LOGGER_APPNAME = "appName";
	

	// proprieta aggiunte ai *.log4j2.properties
	public static final String PROP_PREFIX = "option.";
	public static final String PROP_ENABLE_STDOUT = PROP_PREFIX+"stdout";
	public static final String PROP_ENABLE_JSON = PROP_PREFIX+"json";
	public static final String PROP_ENABLE_JSON_TEMPLATE = PROP_PREFIX+"json.template";
	public static final String PROP_ENABLE_LOG_CLUSTERID = PROP_PREFIX+"clusterId";
	public static final String PROP_ENABLE_LOG_CLUSTERID_STRATEGY = PROP_PREFIX+"clusterId.strategy";
	public static final String PROP_ENABLE_LOG_CLUSTERID_ENV = PROP_PREFIX+"clusterId.env";

	public static final String LOG_CLUSTERID_STRATEGY_FILENAME = "fileName";
	public static final String LOG_CLUSTERID_STRATEGY_DIRECTORY = "directory";
	
	// variabili d'ambiente che pilotano tutte le applicazioni
	public static final String ENV_ENABLE_STDOUT = "GOVWAY_LOG_STDOUT";
	public static final String ENV_ENABLE_JSON = "GOVWAY_LOG_JSON";
	public static final String ENV_ENABLE_JSON_TEMPLATE = "GOVWAY_LOG_JSON_TEMPLATE";
	public static final String ENV_ENABLE_LOG_CLUSTERID = "GOVWAY_LOG_CLUSTER_ID";
	public static final String ENV_ENABLE_LOG_CLUSTERID_STRATEGY = "GOVWAY_LOG_CLUSTER_ID_STRATEGY";
	public static final String ENV_ENABLE_LOG_CLUSTERID_ENV = "GOVWAY_LOG_CLUSTER_ID_ENV";
	public static final java.util.Map<String, String> ENV_LOG = java.util.Map.of(
			PROP_ENABLE_STDOUT, ENV_ENABLE_STDOUT,
			PROP_ENABLE_JSON, ENV_ENABLE_JSON,
			PROP_ENABLE_JSON_TEMPLATE, ENV_ENABLE_JSON_TEMPLATE,
			PROP_ENABLE_LOG_CLUSTERID, ENV_ENABLE_LOG_CLUSTERID,
			PROP_ENABLE_LOG_CLUSTERID_STRATEGY, ENV_ENABLE_LOG_CLUSTERID_STRATEGY,
			PROP_ENABLE_LOG_CLUSTERID_ENV, ENV_ENABLE_LOG_CLUSTERID_ENV
	);
	
	// variabili d'ambiente per il controllo da singola applicazione
	public static final String ENV_ENABLE_STDOUT_GOVWAY = "GOVWAY_RUN_LOG_STDOUT";
	public static final String ENV_ENABLE_JSON_GOVWAY = "GOVWAY_RUN_LOG_JSON";
	public static final String ENV_ENABLE_JSON_TEMPLATE_GOVWAY = "GOVWAY_RUN_LOG_JSON_TEMPLATE";
	public static final String ENV_ENABLE_LOG_CLUSTERID_GOVWAY = "GOVWAY_RUN_LOG_CLUSTER_ID";
	public static final String ENV_ENABLE_LOG_CLUSTERID_STRATEGY_GOVWAY = "GOVWAY_RUN_LOG_CLUSTER_ID_STRATEGY";
	public static final String ENV_ENABLE_LOG_CLUSTERID_ENV_GOVWAY = "GOVWAY_RUN_LOG_CLUSTER_ID_ENV";
	public static final java.util.Map<String, String> ENV_LOG_GOVWAY = java.util.Map.of(
			PROP_ENABLE_STDOUT, ENV_ENABLE_STDOUT_GOVWAY,
			PROP_ENABLE_JSON, ENV_ENABLE_JSON_GOVWAY,
			PROP_ENABLE_JSON_TEMPLATE, ENV_ENABLE_JSON_TEMPLATE_GOVWAY,
			PROP_ENABLE_LOG_CLUSTERID, ENV_ENABLE_LOG_CLUSTERID_GOVWAY,
			PROP_ENABLE_LOG_CLUSTERID_STRATEGY, ENV_ENABLE_LOG_CLUSTERID_STRATEGY_GOVWAY,
			PROP_ENABLE_LOG_CLUSTERID_ENV, ENV_ENABLE_LOG_CLUSTERID_ENV_GOVWAY
	);
	
	public static final String ENV_ENABLE_STDOUT_CONSOLE = "GOVWAY_CONSOLE_LOG_STDOUT";
	public static final String ENV_ENABLE_JSON_CONSOLE = "GOVWAY_CONSOLE_LOG_JSON";
	public static final String ENV_ENABLE_JSON_TEMPLATE_CONSOLE = "GOVWAY_CONSOLE_LOG_JSON_TEMPLATE";
	public static final String ENV_ENABLE_LOG_CLUSTERID_CONSOLE = "GOVWAY_CONSOLE_LOG_CLUSTER_ID";
	public static final String ENV_ENABLE_LOG_CLUSTERID_STRATEGY_CONSOLE = "GOVWAY_CONSOLE_LOG_CLUSTER_ID_STRATEGY";
	public static final String ENV_ENABLE_LOG_CLUSTERID_ENV_CONSOLE = "GOVWAY_CONSOLE_LOG_CLUSTER_ID_ENV";
	public static final java.util.Map<String, String> ENV_LOG_CONSOLE = java.util.Map.of(
			PROP_ENABLE_STDOUT, ENV_ENABLE_STDOUT_CONSOLE,
			PROP_ENABLE_JSON, ENV_ENABLE_JSON_CONSOLE,
			PROP_ENABLE_JSON_TEMPLATE, ENV_ENABLE_JSON_TEMPLATE_CONSOLE,
			PROP_ENABLE_LOG_CLUSTERID, ENV_ENABLE_LOG_CLUSTERID_CONSOLE,
			PROP_ENABLE_LOG_CLUSTERID_STRATEGY, ENV_ENABLE_LOG_CLUSTERID_STRATEGY_CONSOLE,
			PROP_ENABLE_LOG_CLUSTERID_ENV, ENV_ENABLE_LOG_CLUSTERID_ENV_CONSOLE
	);
	
	public static final String ENV_ENABLE_STDOUT_MONITOR = "GOVWAY_MONITOR_LOG_STDOUT";
	public static final String ENV_ENABLE_JSON_MONITOR  = "GOVWAY_MONITOR_LOG_JSON";
	public static final String ENV_ENABLE_JSON_TEMPLATE_MONITOR = "GOVWAY_MONITOR_LOG_JSON_TEMPLATE";
	public static final String ENV_ENABLE_LOG_CLUSTERID_MONITOR = "GOVWAY_MONITOR_LOG_CLUSTER_ID";
	public static final String ENV_ENABLE_LOG_CLUSTERID_STRATEGY_MONITOR = "GOVWAY_MONITOR_LOG_CLUSTER_ID_STRATEGY";
	public static final String ENV_ENABLE_LOG_CLUSTERID_ENV_MONITOR = "GOVWAY_MONITOR_LOG_CLUSTER_ID_ENV";
	public static final java.util.Map<String, String> ENV_LOG_MONITOR = java.util.Map.of(
			PROP_ENABLE_STDOUT, ENV_ENABLE_STDOUT_MONITOR,
			PROP_ENABLE_JSON, ENV_ENABLE_JSON_MONITOR,
			PROP_ENABLE_JSON_TEMPLATE, ENV_ENABLE_JSON_TEMPLATE_MONITOR,
			PROP_ENABLE_LOG_CLUSTERID, ENV_ENABLE_LOG_CLUSTERID_MONITOR,
			PROP_ENABLE_LOG_CLUSTERID_STRATEGY, ENV_ENABLE_LOG_CLUSTERID_STRATEGY_MONITOR,
			PROP_ENABLE_LOG_CLUSTERID_ENV, ENV_ENABLE_LOG_CLUSTERID_ENV_MONITOR
	);
	
	public static final String ENV_ENABLE_STDOUT_API_CONFIG = "GOVWAY_API_CONFIG_LOG_STDOUT";
	public static final String ENV_ENABLE_JSON_API_CONFIG = "GOVWAY_API_CONFIG_LOG_JSON";
	public static final String ENV_ENABLE_JSON_TEMPLATE_API_CONFIG = "GOVWAY_API_CONFIG_LOG_JSON_TEMPLATE";
	public static final String ENV_ENABLE_LOG_CLUSTERID_API_CONFIG = "GOVWAY_API_CONFIG_LOG_CLUSTER_ID";
	public static final String ENV_ENABLE_LOG_CLUSTERID_STRATEGY_API_CONFIG = "GOVWAY_API_CONFIG_LOG_CLUSTER_ID_STRATEGY";
	public static final String ENV_ENABLE_LOG_CLUSTERID_ENV_API_CONFIG = "GOVWAY_API_CONFIG_LOG_CLUSTER_ID_ENV";
	public static final java.util.Map<String, String> ENV_LOG_API_CONFIG = java.util.Map.of(
			PROP_ENABLE_STDOUT, ENV_ENABLE_STDOUT_API_CONFIG,
			PROP_ENABLE_JSON, ENV_ENABLE_JSON_API_CONFIG,
			PROP_ENABLE_JSON_TEMPLATE, ENV_ENABLE_JSON_TEMPLATE_API_CONFIG,
			PROP_ENABLE_LOG_CLUSTERID, ENV_ENABLE_LOG_CLUSTERID_API_CONFIG,
			PROP_ENABLE_LOG_CLUSTERID_STRATEGY, ENV_ENABLE_LOG_CLUSTERID_STRATEGY_API_CONFIG,
			PROP_ENABLE_LOG_CLUSTERID_ENV, ENV_ENABLE_LOG_CLUSTERID_ENV_API_CONFIG
	);
	
	public static final String ENV_ENABLE_STDOUT_API_MONITOR = "GOVWAY_API_MONITOR_LOG_STDOUT";	
	public static final String ENV_ENABLE_JSON_API_MONITOR = "GOVWAY_API_MONITOR_LOG_JSON";
	public static final String ENV_ENABLE_JSON_TEMPLATE_API_MONITOR = "GOVWAY_API_MONITOR_LOG_JSON_TEMPLATE";
	public static final String ENV_ENABLE_LOG_CLUSTERID_API_MONITOR = "GOVWAY_API_MONITOR_LOG_CLUSTER_ID";
	public static final String ENV_ENABLE_LOG_CLUSTERID_STRATEGY_API_MONITOR = "GOVWAY_API_MONITOR_LOG_CLUSTER_ID_STRATEGY";
	public static final String ENV_ENABLE_LOG_CLUSTERID_ENV_API_MONITOR = "GOVWAY_API_MONITOR_LOG_CLUSTER_ID_ENV";
	public static final java.util.Map<String, String> ENV_LOG_API_MONITOR = java.util.Map.of(
			PROP_ENABLE_STDOUT, ENV_ENABLE_STDOUT_API_MONITOR,
			PROP_ENABLE_JSON, ENV_ENABLE_JSON_API_MONITOR,
			PROP_ENABLE_JSON_TEMPLATE, ENV_ENABLE_JSON_TEMPLATE_API_MONITOR,
			PROP_ENABLE_LOG_CLUSTERID, ENV_ENABLE_LOG_CLUSTERID_API_MONITOR,
			PROP_ENABLE_LOG_CLUSTERID_STRATEGY, ENV_ENABLE_LOG_CLUSTERID_STRATEGY_API_MONITOR,
			PROP_ENABLE_LOG_CLUSTERID_ENV, ENV_ENABLE_LOG_CLUSTERID_ENV_API_MONITOR
	);
	

	

}
