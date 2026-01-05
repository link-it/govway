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

package org.openspcoop2.core.commons;

import java.util.Properties;

import org.slf4j.Logger;

/**
 * PropertiesEnvUtils - Utility for reading and checking environment variables from properties files
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertiesEnvUtils {

	private PropertiesEnvUtils() {}

	// Environment variable names
	public static final String ENV_GOVWAY_CONF = "GOVWAY_CONF";
	public static final String ENV_GOVWAY_LOG = "GOVWAY_LOG";
	public static final String ENV_GOVWAY_DB_TYPE = "GOVWAY_DB_TYPE";
	public static final String ENV_GOVWAY_DEFAULT_ENTITY_NAME = "GOVWAY_DEFAULT_ENTITY_NAME";

	// Property names
	public static final String PROP_CONF_REQUIRED = "conf.required";
	public static final String PROP_LOG_REQUIRED = "log.required";
	public static final String PROP_DB_TYPE_REQUIRED = "dbType.required";
	public static final String PROP_DEFAULT_ENTITY_NAME_REQUIRED = "defaultEntityName.required";

	// Source types
	private static final String SOURCE_SYSTEM = "system";
	private static final String SOURCE_JAVA = "java";

	/**
	 * Checks if required environment variables are present based on configuration properties.
	 *
	 * @param props the Properties object with prefix already removed (e.g., obtained via readPropertiesConvertEnvProperties)
	 * @param log the logger to use for logging variable status
	 * @param archiveName the name of the archive/application for error reporting (e.g., "govway.ear", "govwayConsole.war")
	 * @throws CoreException if a required environment variable is not found
	 */
	public static void checkRequiredEnvProperties(Properties props, Logger log, String archiveName) throws CoreException {
		if (props == null) {
			throw new CoreException("Properties is null");
		}

		// Check GOVWAY_CONF
		checkEnvVariable(props, PROP_CONF_REQUIRED, ENV_GOVWAY_CONF, log, archiveName);

		// Check GOVWAY_LOG
		checkEnvVariable(props, PROP_LOG_REQUIRED, ENV_GOVWAY_LOG, log, archiveName);

		// Check GOVWAY_DB_TYPE
		checkEnvVariable(props, PROP_DB_TYPE_REQUIRED, ENV_GOVWAY_DB_TYPE, log, archiveName);

		// Check GOVWAY_DEFAULT_ENTITY_NAME
		checkEnvVariable(props, PROP_DEFAULT_ENTITY_NAME_REQUIRED, ENV_GOVWAY_DEFAULT_ENTITY_NAME, log, archiveName);
	}

	private static void checkEnvVariable(Properties props, String propertyName, String envVarName, Logger log, String archiveName) throws CoreException {
		String checkEnabled = props.getProperty(propertyName);

		if (checkEnabled != null && "true".equalsIgnoreCase(checkEnabled.trim())) {
			// Check system environment variable first (aligned with GovWay behavior)
			String value = System.getenv(envVarName);
			if (value != null && !value.trim().isEmpty()) {
				logEnvVariableFound(log, envVarName, SOURCE_SYSTEM);
				return;
			}

			// Check Java system property
			value = System.getProperty(envVarName);
			if (value != null && !value.trim().isEmpty()) {
				logEnvVariableFound(log, envVarName, SOURCE_JAVA);
				return;
			}

			// Not found anywhere
			logEnvVariableNotFound(log, envVarName, archiveName);
			throw new CoreException("Required environment variable '" + envVarName + "' not found (neither as system environment variable nor as Java system property)");
		}
	}

	private static void logEnvVariableFound(Logger log, String envVarName, String source) {
		if (log != null) {
			log.info("Environment variable '{}' found as {} variable", envVarName, source);
		}
	}

	private static void logEnvVariableNotFound(Logger log, String envVarName, String archiveName) {
		String errorMsg = "[" + archiveName + "] Environment variable '" + envVarName + "' not found (neither as system environment variable nor as Java system property)";
		System.err.println(errorMsg);
		System.err.flush();
		if (log != null) {
			log.error(errorMsg);
		}
	}

	/**
	 * Gets the value of an environment variable, checking both system environment variables and Java system properties.
	 * System environment variables take precedence (aligned with GovWay behavior).
	 *
	 * @param envVarName the name of the environment variable
	 * @return the value, or null if not found
	 */
	public static String getEnvValue(String envVarName) {
		// Check system environment variable first (aligned with GovWay behavior)
		String value = System.getenv(envVarName);
		if (value != null && !value.trim().isEmpty()) {
			return value;
		}

		// Check Java system property
		value = System.getProperty(envVarName);
		if (value != null && !value.trim().isEmpty()) {
			return value;
		}

		return null;
	}

	/**
	 * Checks if an environment variable exists (either as system environment variable or Java system property).
	 *
	 * @param envVarName the name of the environment variable
	 * @return true if the variable exists and is not empty
	 */
	public static boolean existsEnvValue(String envVarName) {
		return getEnvValue(envVarName) != null;
	}

	/**
	 * Resolves the 4 GovWay environment variables in all values of a Properties object.
	 *
	 * @param props the Properties object whose values may contain environment variable placeholders
	 */
	public static void resolveGovWayEnvVariables(Properties props) {
		if (props == null) {
			return;
		}

		for (String key : props.stringPropertyNames()) {
			String value = props.getProperty(key);
			String resolved = resolveGovWayEnvVariables(value);
			if (value!=null && !value.equals(resolved)) {
				props.setProperty(key, resolved);
			}
		}
	}

	/**
	 * Resolves the 4 GovWay environment variables in a string.
	 * Replaces ${GOVWAY_CONF}, ${GOVWAY_LOG}, ${GOVWAY_DB_TYPE}, ${GOVWAY_DEFAULT_ENTITY_NAME}
	 * with their actual values if defined, otherwise leaves them unchanged.
	 *
	 * @param value the string that may contain environment variable placeholders
	 * @return the string with resolved variables, or the original string if variables are not defined
	 */
	public static String resolveGovWayEnvVariables(String value) {
		if (value == null) {
			return null;
		}

		String result = value;
		result = resolveEnvVariable(result, ENV_GOVWAY_CONF);
		result = resolveEnvVariable(result, ENV_GOVWAY_LOG);
		result = resolveEnvVariable(result, ENV_GOVWAY_DB_TYPE);
		result = resolveEnvVariable(result, ENV_GOVWAY_DEFAULT_ENTITY_NAME);

		return result;
	}

	private static String resolveEnvVariable(String value, String envVarName) {
		String placeholder = "${" + envVarName + "}";
		if (value.contains(placeholder)) {
			String envValue = getEnvValue(envVarName);
			if (envValue != null) {
				return value.replace(placeholder, envValue);
			}
		}
		return value;
	}
}
