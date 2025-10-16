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
package org.openspcoop2.pdd.template_scan.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * TemplateScanProperties
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TemplateScanProperties {
	
	private final String dbUsername;
	private final String dbPassword;
	private final String dbType;
	private final String dbUrl;
	private final String dbDriver;
	
	private final Boolean enableTransformationRequest; 
	private final Boolean enableTransformationResponse;
	private final Boolean enableConnectors;
	private final Boolean enableAtttributeAuthority; 
	private final Boolean enableTokenPolicy;
	
	public static final String PROPERTIES_FILE = "/template_scan.cli.properties";
	public static final String LOG_PROPERTIES_FILE = "/template_scan.cli.log4j2.properties";
	
	
	public static final String REGISTRY_SEGMENT = "Registro";
	public static final String CONFIG_SEGMENT = "Configurazione";
	public static final String CONFIGURATION_SEGMENT = "Configurazione";
	public static final String TRANSFORMATION_SEGMENT = "Transformazioni";
	public static final String TRANSFORMATION_REQUEST_SEGMENT = "Richiesta";
	public static final String TRANSFORMATION_RESPONSE_SEGMENT = "Risposte";
	public static final String TRANSFORMATION_REQUEST_CONTENT_SEGMENT = "Contenuto";
	public static final String TRANSFORMATION_RESPONSE_CONTENT_SEGMENT = "Contenuto";
	public static final String MISC_SEGMENT = "misc";
	public static final String EROGATION_SEGMENT = "Erogazioni";
	public static final String FRUITION_SEGMENT = "Fruizioni";
	public static final String TOKEN_POLICY_SEGMENT = "Token Policy";
	public static final String TOKEN_POLICY_TOKEN_ENDPOINT_SEGMENT = "Token Endpoint";
	public static final String ATTRIBUTE_AUTHORITY_SEGMENT = "Attribute Authority";
	public static final String ATTRIBUTE_AUTHORITY_REQUEST_SEGMENT = "Richiesta";
	
	private static class InstanceHolder {
		private static final TemplateScanProperties INSTANCE = createInstance();

		private static TemplateScanProperties createInstance() {
			try(InputStream is = TemplateScanProperties.class.getResourceAsStream(TemplateScanProperties.PROPERTIES_FILE)) {
				return new TemplateScanProperties(is);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public static TemplateScanProperties getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	private String readProperty(Properties p, String key, boolean required) {
		if (required && !p.containsKey(key))
			throw new IllegalStateException("property " + key + " necessaria");
		return p.getProperty(key);
	}
	
	private Boolean readPropertyBoolean(Properties p, String key, boolean required) {
		if (required && !p.containsKey(key))
			throw new IllegalStateException("property " + key + " necessaria");
		return Boolean.valueOf(p.getProperty(key));
	}
	
	private TemplateScanProperties(InputStream is) throws IOException {
		Properties p = new Properties();
		p.load(is);
		
		this.dbUsername = readProperty(p, "db.user", true);
		this.dbPassword = readProperty(p, "db.password", true);
		this.dbType = readProperty(p, "db.type", true);
		this.dbUrl = readProperty(p, "db.url", true);
		this.dbDriver = readProperty(p, "db.driver", true);
		
		this.enableAtttributeAuthority = readPropertyBoolean(p, "scan.attribute_authority.enable", true);
		this.enableConnectors = readPropertyBoolean(p, "scan.connettori.enable", true);
		this.enableTokenPolicy = readPropertyBoolean(p, "scan.token_policy.enable", true);
		this.enableTransformationRequest = readPropertyBoolean(p, "scan.trasformazioni.richiesta.enable", true);
		this.enableTransformationResponse = readPropertyBoolean(p, "scan.trasformazioni.risposta.enable", true);
	}

	public String getDbUsername() {
		return this.dbUsername;
	}

	public String getDbPassword() {
		return this.dbPassword;
	}

	public String getDbType() {
		return this.dbType;
	}

	public String getDbUrl() {
		return this.dbUrl;
	}
	
	public String getDbDriver() {
		return this.dbDriver;
	}

	public boolean isEnableTransformationRequest() {
		return this.enableTransformationRequest;
	}

	public boolean isEnableTransformationResponse() {
		return this.enableTransformationResponse;
	}

	public boolean isEnableConnectors() {
		return this.enableConnectors;
	}

	public boolean isEnableAtttributeAuthority() {
		return this.enableAtttributeAuthority;
	}

	public boolean isEnableTokenPolicy() {
		return this.enableTokenPolicy;
	}
	
	

}
