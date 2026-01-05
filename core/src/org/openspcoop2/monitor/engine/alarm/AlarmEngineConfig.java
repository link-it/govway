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

package org.openspcoop2.monitor.engine.alarm;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mail.SenderType;
import org.openspcoop2.utils.transport.http.HttpLibrary;
import org.openspcoop2.utils.transport.http.HttpLibraryConnection;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.SSLConfig;
import org.slf4j.Logger;

/**
 * AlarmEngineConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmEngineConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String activeAlarmServiceUrl;
	private String activeAlarmServiceUrlManagerUsername;
	private String activeAlarmServiceUrlManagerPassword;
	
	private Integer activeAlarmServiceUrlConnectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
	private Integer activeAlarmServiceUrlReadConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
	
	private boolean activeAlarmServiceUrlHttps = false;
	private boolean activeAlarmServiceUrlHttpsVerificaHostName = true;
	private boolean activeAlarmServiceUrlHttpsAutenticazioneServer = true;
	private String activeAlarmServiceUrlHttpsTruststorePath = null;
	private String activeAlarmServiceUrlHttpsTruststoreType = null;
	private String activeAlarmServiceUrlHttpsTruststorePassword = null;
	
	private HttpLibrary httpLibrary = HttpLibraryConnection.getDefaultLibrary();
	
	private boolean historyEnabled = true;
	
	private SenderType mailSenderType;
	private Integer mailSenderConnectionTimeout;
	private Integer mailSenderReadTimeout;
	private String mailHost;
	private Integer mailPort;
	private String mailUsername;
	private String mailPassword;
	private SSLConfig mailSSLConfig;
	private boolean mailStartTls;
	private String mailAgent;
	private String mailFrom;
	private String mailSubject;
	private String mailBody;
	private boolean mailDebug;
	private boolean mailSendChangeStatusOk;
	private boolean mailCheckAcknowledgedStatus;
	private boolean mailShowAllOptions;
	
	private String defaultScriptPath;
	private String defaultScriptArgs;
	private boolean scriptDebug;
	private boolean scriptSendChangeStatusOk;
	private boolean scriptCheckAcknowledgedStatus;
	private boolean scriptShowAllOptions;
	
	private boolean optionsUpdateStateActiveAlarm;
	private boolean optionsUpdateStatePassiveAlarm;
	private boolean optionsUpdateAckCriteriaActiveAlarm;
	private boolean optionsUpdateAckCriteriaPassiveAlarm;
	private boolean optionsAcknowledgedStatusAssociation;
	private boolean optionsGroupByApi;
	private boolean optionsFilterApi;
	private boolean optionsFilterApiOrganization;
	
	
	public HttpLibrary getHttpLibrary() {
		return this.httpLibrary;
	}

	public void setHttpLibrary(HttpLibrary httpLibrary) {
		this.httpLibrary = httpLibrary;
	}

	public boolean isHistoryEnabled() {
		return this.historyEnabled;
	}

	public void setHistoryEnabled(boolean historyEnabled) {
		this.historyEnabled = historyEnabled;
	}
	
	public SenderType getMailSenderType() {
		return this.mailSenderType;
	}

	public void setMailSenderType(SenderType mailSenderType) {
		this.mailSenderType = mailSenderType;
	}

	public Integer getMailSenderConnectionTimeout() {
		return this.mailSenderConnectionTimeout;
	}

	public void setMailSenderConnectionTimeout(Integer mailSenderConnectionTimeout) {
		this.mailSenderConnectionTimeout = mailSenderConnectionTimeout;
	}

	public Integer getMailSenderReadTimeout() {
		return this.mailSenderReadTimeout;
	}

	public void setMailSenderReadTimeout(Integer mailSenderReadTimeout) {
		this.mailSenderReadTimeout = mailSenderReadTimeout;
	}

	public String getMailHost() {
		return this.mailHost;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public Integer getMailPort() {
		return this.mailPort;
	}

	public void setMailPort(Integer mailPort) {
		this.mailPort = mailPort;
	}

	public String getMailUsername() {
		return this.mailUsername;
	}

	public void setMailUsername(String mailUsername) {
		this.mailUsername = mailUsername;
	}

	public String getMailPassword() {
		return this.mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public SSLConfig getMailSSLConfig() {
		return this.mailSSLConfig;
	}

	public void setMailSSLConfig(SSLConfig mailSSLConfig) {
		this.mailSSLConfig = mailSSLConfig;
	}
	
	public boolean isMailStartTls() {
		return this.mailStartTls;
	}

	public void setMailStartTls(boolean mailStartTls) {
		this.mailStartTls = mailStartTls;
	}

	public String getMailAgent() {
		return this.mailAgent;
	}

	public void setMailAgent(String mailAgent) {
		this.mailAgent = mailAgent;
	}
	
	public String getMailFrom() {
		return this.mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailSubject() {
		return this.mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailBody() {
		return this.mailBody;
	}

	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}

	public String getDefaultScriptPath() {
		return this.defaultScriptPath;
	}

	public void setDefaultScriptPath(String defaultScriptPath) {
		this.defaultScriptPath = defaultScriptPath;
	}

	public String getDefaultScriptArgs() {
		return this.defaultScriptArgs;
	}

	public void setDefaultScriptArgs(String defaultScriptArgs) {
		this.defaultScriptArgs = defaultScriptArgs;
	}

	public boolean isMailCheckAcknowledgedStatus() {
		return this.mailCheckAcknowledgedStatus;
	}

	public void setMailCheckAcknowledgedStatus(boolean mailCheckAcknowledgedStatus) {
		this.mailCheckAcknowledgedStatus = mailCheckAcknowledgedStatus;
	}

	public boolean isMailSendChangeStatusOk() {
		return this.mailSendChangeStatusOk;
	}

	public void setMailSendChangeStatusOk(boolean mailSendChangeStatusOk) {
		this.mailSendChangeStatusOk = mailSendChangeStatusOk;
	}
	
	public boolean isMailShowAllOptions() {
		return this.mailShowAllOptions;
	}

	public void setMailShowAllOptions(boolean mailShowAllOptions) {
		this.mailShowAllOptions = mailShowAllOptions;
	}
	
	public boolean isMailDebug() {
		return this.mailDebug;
	}

	public void setMailDebug(boolean mailDebug) {
		this.mailDebug = mailDebug;
	}

	public boolean isScriptDebug() {
		return this.scriptDebug;
	}

	public void setScriptDebug(boolean scriptDebug) {
		this.scriptDebug = scriptDebug;
	}
	
	public boolean isScriptCheckAcknowledgedStatus() {
		return this.scriptCheckAcknowledgedStatus;
	}

	public void setScriptCheckAcknowledgedStatus(boolean scriptCheckAcknowledgedStatus) {
		this.scriptCheckAcknowledgedStatus = scriptCheckAcknowledgedStatus;
	}
	
	public boolean isScriptSendChangeStatusOk() {
		return this.scriptSendChangeStatusOk;
	}

	public void setScriptSendChangeStatusOk(boolean scriptSendChangeStatusOk) {
		this.scriptSendChangeStatusOk = scriptSendChangeStatusOk;
	}
	
	public boolean isScriptShowAllOptions() {
		return this.scriptShowAllOptions;
	}

	public void setScriptShowAllOptions(boolean scriptShowAllOptions) {
		this.scriptShowAllOptions = scriptShowAllOptions;
	}
	
	public String getActiveAlarmServiceUrl() {
		return this.activeAlarmServiceUrl;
	}

	public void setActiveAlarmServiceUrl(String activeAlarmServiceUrl) {
		this.activeAlarmServiceUrl = activeAlarmServiceUrl;
	}
	
	public String getActiveAlarmServiceUrlManagerUsername() {
		return this.activeAlarmServiceUrlManagerUsername;
	}

	public void setActiveAlarmServiceUrlManagerUsername(String activeAlarmServiceUrlManagerUsername) {
		this.activeAlarmServiceUrlManagerUsername = activeAlarmServiceUrlManagerUsername;
	}

	public String getActiveAlarmServiceUrlManagerPassword() {
		return this.activeAlarmServiceUrlManagerPassword;
	}

	public void setActiveAlarmServiceUrlManagerPassword(String activeAlarmServiceUrlManagerPassword) {
		this.activeAlarmServiceUrlManagerPassword = activeAlarmServiceUrlManagerPassword;
	}
	
	public Integer getActiveAlarmServiceUrlConnectionTimeout() {
		return this.activeAlarmServiceUrlConnectionTimeout;
	}

	public void setActiveAlarmServiceUrlConnectionTimeout(Integer activeAlarmServiceUrlConnectionTimeout) {
		this.activeAlarmServiceUrlConnectionTimeout = activeAlarmServiceUrlConnectionTimeout;
	}

	public Integer getActiveAlarmServiceUrlReadConnectionTimeout() {
		return this.activeAlarmServiceUrlReadConnectionTimeout;
	}

	public void setActiveAlarmServiceUrlReadConnectionTimeout(Integer activeAlarmServiceUrlReadConnectionTimeout) {
		this.activeAlarmServiceUrlReadConnectionTimeout = activeAlarmServiceUrlReadConnectionTimeout;
	}

	public boolean isActiveAlarmServiceUrlHttps() {
		return this.activeAlarmServiceUrlHttps;
	}

	public void setActiveAlarmServiceUrlHttps(boolean activeAlarmServiceUrlHttps) {
		this.activeAlarmServiceUrlHttps = activeAlarmServiceUrlHttps;
	}

	public boolean isActiveAlarmServiceUrlHttpsVerificaHostName() {
		return this.activeAlarmServiceUrlHttpsVerificaHostName;
	}

	public void setActiveAlarmServiceUrlHttpsVerificaHostName(boolean activeAlarmServiceUrlHttpsVerificaHostName) {
		this.activeAlarmServiceUrlHttpsVerificaHostName = activeAlarmServiceUrlHttpsVerificaHostName;
	}

	public boolean isActiveAlarmServiceUrlHttpsAutenticazioneServer() {
		return this.activeAlarmServiceUrlHttpsAutenticazioneServer;
	}

	public void setActiveAlarmServiceUrlHttpsAutenticazioneServer(
			boolean activeAlarmServiceUrlHttpsAutenticazioneServer) {
		this.activeAlarmServiceUrlHttpsAutenticazioneServer = activeAlarmServiceUrlHttpsAutenticazioneServer;
	}

	public String getActiveAlarmServiceUrlHttpsTruststorePath() {
		return this.activeAlarmServiceUrlHttpsTruststorePath;
	}

	public void setActiveAlarmServiceUrlHttpsTruststorePath(String activeAlarmServiceUrlHttpsTruststorePath) {
		this.activeAlarmServiceUrlHttpsTruststorePath = activeAlarmServiceUrlHttpsTruststorePath;
	}

	public String getActiveAlarmServiceUrlHttpsTruststoreType() {
		return this.activeAlarmServiceUrlHttpsTruststoreType;
	}

	public void setActiveAlarmServiceUrlHttpsTruststoreType(String activeAlarmServiceUrlHttpsTruststoreType) {
		this.activeAlarmServiceUrlHttpsTruststoreType = activeAlarmServiceUrlHttpsTruststoreType;
	}

	public String getActiveAlarmServiceUrlHttpsTruststorePassword() {
		return this.activeAlarmServiceUrlHttpsTruststorePassword;
	}

	public void setActiveAlarmServiceUrlHttpsTruststorePassword(String activeAlarmServiceUrlHttpsTruststorePassword) {
		this.activeAlarmServiceUrlHttpsTruststorePassword = activeAlarmServiceUrlHttpsTruststorePassword;
	}
	
	public boolean isOptionsUpdateStateActiveAlarm() {
		return this.optionsUpdateStateActiveAlarm;
	}

	public void setOptionsUpdateStateActiveAlarm(boolean optionsUpdateState) {
		this.optionsUpdateStateActiveAlarm = optionsUpdateState;
	}
	
	public boolean isOptionsUpdateStatePassiveAlarm() {
		return this.optionsUpdateStatePassiveAlarm;
	}

	public void setOptionsUpdateStatePassiveAlarm(boolean optionsUpdateState) {
		this.optionsUpdateStatePassiveAlarm = optionsUpdateState;
	}
	
	public boolean isOptionsUpdateAckCriteriaActiveAlarm() {
		return this.optionsUpdateAckCriteriaActiveAlarm;
	}

	public void setOptionsUpdateAckCriteriaActiveAlarm(boolean optionsUpdateAckCriteria) {
		this.optionsUpdateAckCriteriaActiveAlarm = optionsUpdateAckCriteria;
	}
	
	public boolean isOptionsUpdateAckCriteriaPassiveAlarm() {
		return this.optionsUpdateAckCriteriaPassiveAlarm;
	}

	public void setOptionsUpdateAckCriteriaPassiveAlarm(boolean optionsUpdateAckCriteria) {
		this.optionsUpdateAckCriteriaPassiveAlarm = optionsUpdateAckCriteria;
	}

	public boolean isOptionsAcknowledgedStatusAssociation() {
		return this.optionsAcknowledgedStatusAssociation;
	}

	public void setOptionsAcknowledgedStatusAssociation(boolean optionsAcknowledgedStatusAssociation) {
		this.optionsAcknowledgedStatusAssociation = optionsAcknowledgedStatusAssociation;
	}

	public boolean isOptionsGroupByApi() {
		return this.optionsGroupByApi;
	}

	public void setOptionsGroupByApi(boolean optionsGroupByApi) {
		this.optionsGroupByApi = optionsGroupByApi;
	}

	public boolean isOptionsFilterApi() {
		return this.optionsFilterApi;
	}

	public void setOptionsFilterApi(boolean optionsFilterApi) {
		this.optionsFilterApi = optionsFilterApi;
	}

	public boolean isOptionsFilterApiOrganization() {
		return this.optionsFilterApiOrganization;
	}

	public void setOptionsFilterApiOrganization(boolean optionsFilterApiOrganization) {
		this.optionsFilterApiOrganization = optionsFilterApiOrganization;
	}
	
	protected static AlarmEngineConfig readAlarmEngineConfig(Logger log,AlarmConfigProperties alarmConfigProperties) throws UtilsException {
		
		if(log!=null) {
			// nop
		}
		
		AlarmEngineConfig config = new AlarmEngineConfig();
		
		config.setActiveAlarmServiceUrl(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL, true, true));
		config.setActiveAlarmServiceUrlManagerUsername(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_MANAGER_USERNAME, true, true));
		config.setActiveAlarmServiceUrlManagerPassword(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_MANAGER_PASSWORD, true, true));
		
		String readConnectionTimeout = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_READ_CONNECTION_TIMEOUT, false, true);
		if(StringUtils.isNotEmpty(readConnectionTimeout)) {
			config.setActiveAlarmServiceUrlReadConnectionTimeout(Integer.valueOf(readConnectionTimeout));
		}
		String connectionTimeout = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_CONNECTION_TIMEOUT, false, true);
		if(StringUtils.isNotEmpty(connectionTimeout)) {
			config.setActiveAlarmServiceUrlReadConnectionTimeout(Integer.valueOf(connectionTimeout));
		}
		
		String https = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_HTTPS, false, true);
		if(https!=null) {
			config.setActiveAlarmServiceUrlHttps(Boolean.parseBoolean(https));
		}
		if(config.isActiveAlarmServiceUrlHttps()) {
			
			String hostnameVerifier = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_HTTPS_HOSTNAME_VERIFIER, false, true);
			if(hostnameVerifier!=null) {
				config.setActiveAlarmServiceUrlHttpsVerificaHostName(Boolean.parseBoolean(hostnameVerifier));
			}
			
			String serverAuth = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_HTTPS_SERVER_AUTH, false, true);
			if(serverAuth!=null) {
				config.setActiveAlarmServiceUrlHttpsAutenticazioneServer(Boolean.parseBoolean(serverAuth));
			}
			if(config.isActiveAlarmServiceUrlHttpsAutenticazioneServer()) {
				config.setActiveAlarmServiceUrlHttpsTruststorePath(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_HTTPS_SERVER_AUTH_TRUSTSTORE_PATH, true, true));
				config.setActiveAlarmServiceUrlHttpsTruststoreType(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_HTTPS_SERVER_AUTH_TRUSTSTORE_TYPE, true, true));
				config.setActiveAlarmServiceUrlHttpsTruststorePassword(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_HTTPS_SERVER_AUTH_TRUSTSTORE_PASSWORD, true, true));
			}
		
		}
		
		String httpLib = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_HTTP_LIBRARY, false, true);
		if (httpLib != null) {
			config.setHttpLibrary(HttpLibrary.fromName(httpLib));
		}
		
		String alarmHistoryEnabled = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_HISTORY_ENABLED, false, true);
		if(alarmHistoryEnabled!=null) {
			config.setHistoryEnabled(Boolean.parseBoolean(alarmHistoryEnabled));
		}
		
		String mailSenderHost = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_HOST, false, true);
		if(mailSenderHost!=null){
			config.setMailHost(mailSenderHost);
			
			String mailSenderType = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SENDER_TYPE, true, true);
			config.setMailSenderType(SenderType.valueOf(mailSenderType));
			
			String mailSenderConnectionTimeout = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SENDER_CONNECTION_TIMEOUT, false, true);
			if(mailSenderConnectionTimeout!=null){
				config.setMailSenderConnectionTimeout(Integer.parseInt(mailSenderConnectionTimeout));
			}
			
			String mailSenderReadTimeout = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SENDER_READ_TIMEOUT, false, true);
			if(mailSenderReadTimeout!=null){
				config.setMailSenderReadTimeout(Integer.parseInt(mailSenderReadTimeout));
			}
			
			config.setMailPort(Integer.parseInt(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_PORT, true, true)));
			
			String mailSenderUsername = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_USERNAME, false, true);
			if(mailSenderUsername!=null){
				config.setMailUsername(mailSenderUsername);
			}
			
			String mailSenderPassword = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_PASSWORD, false, true);
			if(mailSenderPassword!=null){
				config.setMailPassword(mailSenderPassword);
			}
			
			String mailSenderSSLType = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_TYPE, false, true);
			if(mailSenderSSLType!=null){
				SSLConfig mailSSLConfig = new SSLConfig();
			
				mailSSLConfig.setSslType(mailSenderSSLType);
				
				mailSSLConfig.setHostnameVerifier(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_HOSTNAME_VERIFIER, true, true)));
				boolean serverAuth = Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_SERVER_AUTH, true, true));
				if(serverAuth) {
					mailSSLConfig.setTrustStoreLocation(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_TRUSTSTORE_LOCATION, true, true));
					mailSSLConfig.setTrustStoreType(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_TRUSTSTORE_TYPE, true, true));
					mailSSLConfig.setTrustStorePassword(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_TRUSTSTORE_PASSWORD, true, true));
					mailSSLConfig.setTrustManagementAlgorithm(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_TRUSTSTORE_MANAGEMENT_ALGORITHM, true, true));
				}
				else {
					mailSSLConfig.setTrustAllCerts(true);
				}
				config.setMailStartTls(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_START_TLS, true, true)));
				
				config.setMailSSLConfig(mailSSLConfig);
			}
			
			config.setMailAgent(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_AGENT, false, true));
			
			config.setMailFrom(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_FROM, true, true));
			config.setMailSubject(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SUBJECT, true, true));
			config.setMailBody(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_BODY, true, true));
		
			config.setMailDebug(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_DEBUG, true, true)));
			
			config.setMailCheckAcknowledgedStatus(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_CHECK_ACKNOWLEDGED_STATUS, true, true)));
			
			config.setMailSendChangeStatusOk(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SEND_CHANGE_STATUS_OK, true, true)));
			
			config.setMailShowAllOptions(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SHOW_ALL_OPTIONS, true, true)));

		}
		
		String alarmScript = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_PATH, false, true);
		if(alarmScript!=null){
			config.setDefaultScriptPath(alarmScript);
			config.setDefaultScriptArgs(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_ARGS, true, true));
		}
		// L'ack mode è obbligatorio (lo script può essere impostato anche dentro l'allarme)
		config.setScriptDebug(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_DEBUG, true, true)));
		config.setScriptCheckAcknowledgedStatus(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_CHECK_ACKNOWLEDGED_STATUS, true, true)));
		config.setScriptSendChangeStatusOk(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_SEND_CHANGE_STATUS_OK, true, true)));
		config.setScriptShowAllOptions(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_SHOW_ALL_OPTIONS, true, true)));

		
		config.setOptionsUpdateStateActiveAlarm(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ADVANCED_OPTIONS_UPDATE_STATE_ACTIVE_ALARM, true, true)));
		config.setOptionsUpdateStatePassiveAlarm(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ADVANCED_OPTIONS_UPDATE_STATE_PASSIVE_ALARM, true, true)));
		
		config.setOptionsAcknowledgedStatusAssociation(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ADVANCED_OPTIONS_ACK_STATUS_ASSOCIATION, true, true)));
		
		String s = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ADVANCED_OPTIONS_UPDATE_ACK_CRITERIA_ACTIVE_ALARM, false, true);
		if(StringUtils.isNotEmpty(s)) {
			config.setOptionsUpdateAckCriteriaActiveAlarm(Boolean.parseBoolean(s));
		}
		else {
			config.setOptionsUpdateAckCriteriaActiveAlarm(true);
		}
		s = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ADVANCED_OPTIONS_UPDATE_ACK_CRITERIA_PASSIVE_ALARM, false, true);
		if(StringUtils.isNotEmpty(s)) {
			config.setOptionsUpdateAckCriteriaPassiveAlarm(Boolean.parseBoolean(s));
		}
		else {
			config.setOptionsUpdateAckCriteriaPassiveAlarm(false);
		}
		
		config.setOptionsGroupByApi(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ADVANCED_OPTIONS_GROUP_BY_API, true, true)));
		config.setOptionsFilterApi(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ADVANCED_OPTIONS_FILTER_API, true, true)));
		config.setOptionsFilterApiOrganization(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ADVANCED_OPTIONS_FILTER_API_ORGANIZATION, true, true)));
		
		return config;
	}
}
