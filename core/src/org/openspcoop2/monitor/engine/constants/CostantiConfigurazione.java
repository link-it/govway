/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.engine.constants;

/**
 * CostantiConfigurazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiConfigurazione {
	
	public static final String CONFIG_FILENAME = "openspcoop2.monitor.properties";
	public static final String LOG4J_FILENAME = "openspcoop2.monitor.log4j2.properties";
	
	public final static String PROPERTIES_LOCAL_PATH = "openspcoop2.monitor_local.properties";
	public final static String PROPERTIES = "OPENSPCOOP2_MONITOR_PROPERTIES";
	
	public static final String STAT_TIMEOUT = "org.openspcoop2.monitor.statistic.timeout";
	public static final String STAT_DEBUG = "org.openspcoop2.monitor.statistic.debug";
	public static final String STAT_CUSTOM_STATISTICS = "org.openspcoop2.monitor.statistic.custom.enabled";
	public static final String STAT_CUSTOM_TRANSACTION_STATISTICS = "org.openspcoop2.monitor.statistic.custom.transaction.enabled";
	public static final String STAT_HOURLY = "org.openspcoop2.monitor.statistic.hourly.enabled";
	public static final String STAT_DAILY = "org.openspcoop2.monitor.statistic.daily.enabled";
	public static final String STAT_WEEKLY = "org.openspcoop2.monitor.statistic.weekly.enabled";
	public static final String STAT_MONTHLY = "org.openspcoop2.monitor.statistic.monthly.enabled";
	public static final String STAT_HOURLY_LASTINT = "org.openspcoop2.monitor.statistic.hourly.lastinterval.enabled";
	public static final String STAT_DAILY_LASTINT = "org.openspcoop2.monitor.statistic.daily.lastinterval.enabled";
	public static final String STAT_WEEKLY_LASTINT = "org.openspcoop2.monitor.statistic.weekly.lastinterval.enabled";
	public static final String STAT_MONTHLY_LASTINT = "org.openspcoop2.monitor.statistic.monthly.lastinterval.enabled";
	
	public static final String FS_RECOVERY_TIMEOUT = "org.openspcoop2.monitor.fileSystemRecovery.timeout";
	public static final String FS_RECOVERY_DEBUG = "org.openspcoop2.monitor.fileSystemRecovery.debug";
	public static final String FS_RECOVERY_REPOSITORY_DIR = "org.openspcoop2.monitor.fileSystemRecovery.repository";
	public static final String FS_RECOVERY_MAX_ATTEMPTS = "org.openspcoop2.monitor.fileSystemRecovery.maxAttempts";
	public static final String FS_RECOVERY_EVENTS_ENABLED = "org.openspcoop2.monitor.fileSystemRecovery.events.enabled";
	public static final String FS_RECOVERY_TRANSACTION_ENABLED = "org.openspcoop2.monitor.fileSystemRecovery.transaction.enabled";
	
	public final static String PDD_MONITOR_FW_CONF_DIR = "org.openspcoop2.monitor.config_dir";
	public final static String PDD_MONITOR_DEFAULT_PROTOCOL = "org.openspcoop2.monitor.defaultProtocol";
	
}
