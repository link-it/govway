/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.slf4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;
import org.openspcoop2.utils.mail.SenderType;
import org.openspcoop2.utils.transport.http.SSLConfig;

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
	
	private String activeAlarm_serviceUrl;
	private String activeAlarm_serviceUrl_SuffixStartAlarm = CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_DEFAULT_VALUE_START_ALARM;
	private String activeAlarm_serviceUrl_SuffixStopAlarm = CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_DEFAULT_VALUE_STOP_ALARM;
	private String activeAlarm_serviceUrl_SuffixReStartAlarm = CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_DEFAULT_VALUE_RESTART_ALARM;
	private String activeAlarm_serviceUrl_SuffixUpdateStateOkAlarm = CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_DEFAULT_VALUE_UPDATE_STATE_ALARM_OK;
	private String activeAlarm_serviceUrl_SuffixUpdateStateWarningAlarm = CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_DEFAULT_VALUE_UPDATE_STATE_ALARM_WARNING;
	private String activeAlarm_serviceUrl_SuffixUpdateStateErrorAlarm = CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_DEFAULT_VALUE_UPDATE_STATE_ALARM_ERROR;
	private String activeAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm = CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_DEFAULT_VALUE_UPDATE_ACK_ENABLED;
	private String activeAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm = CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_DEFAULT_VALUE_UPDATE_ACK_DISABLED;
	
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
	private String mailFrom;
	private String mailSubject;
	private String mailBody;
	private boolean mailAckMode;
	private boolean mailDebug;
	private boolean mailSendChangeStatusOk;
	
	private String defaultScriptPath;
	private String defaultScriptArgs;
	private boolean scriptAckMode;
	private boolean scriptDebug;
	private boolean scriptSendChangeStatusOk;
	
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
	
	public boolean isMailAckMode() {
		return this.mailAckMode;
	}

	public void setMailAckMode(boolean mailAckMode) {
		this.mailAckMode = mailAckMode;
	}

	public boolean isMailSendChangeStatusOk() {
		return this.mailSendChangeStatusOk;
	}

	public void setMailSendChangeStatusOk(boolean mailSendChangeStatusOk) {
		this.mailSendChangeStatusOk = mailSendChangeStatusOk;
	}
	
	public boolean isScriptAckMode() {
		return this.scriptAckMode;
	}

	public void setScriptAckMode(boolean scriptAckMode) {
		this.scriptAckMode = scriptAckMode;
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
	
	public boolean isScriptSendChangeStatusOk() {
		return this.scriptSendChangeStatusOk;
	}

	public void setScriptSendChangeStatusOk(boolean scriptSendChangeStatusOk) {
		this.scriptSendChangeStatusOk = scriptSendChangeStatusOk;
	}
	
	public String getActiveAlarm_serviceUrl() {
		return this.activeAlarm_serviceUrl;
	}

	public void setActiveAlarm_serviceUrl(String activeAlarm_serviceUrl) {
		this.activeAlarm_serviceUrl = activeAlarm_serviceUrl;
	}

	public String getActiveAlarm_serviceUrl_SuffixStartAlarm() {
		return this.activeAlarm_serviceUrl_SuffixStartAlarm;
	}

	public void setActiveAlarm_serviceUrl_SuffixStartAlarm(String activeAlarm_serviceUrl_SuffixStartAlarm) {
		this.activeAlarm_serviceUrl_SuffixStartAlarm = activeAlarm_serviceUrl_SuffixStartAlarm;
	}

	public String getActiveAlarm_serviceUrl_SuffixStopAlarm() {
		return this.activeAlarm_serviceUrl_SuffixStopAlarm;
	}

	public void setActiveAlarm_serviceUrl_SuffixStopAlarm(String activeAlarm_serviceUrl_SuffixStopAlarm) {
		this.activeAlarm_serviceUrl_SuffixStopAlarm = activeAlarm_serviceUrl_SuffixStopAlarm;
	}

	public String getActiveAlarm_serviceUrl_SuffixReStartAlarm() {
		return this.activeAlarm_serviceUrl_SuffixReStartAlarm;
	}

	public void setActiveAlarm_serviceUrl_SuffixReStartAlarm(String activeAlarm_serviceUrl_SuffixReStartAlarm) {
		this.activeAlarm_serviceUrl_SuffixReStartAlarm = activeAlarm_serviceUrl_SuffixReStartAlarm;
	}

	public String getActiveAlarm_serviceUrl_SuffixUpdateStateOkAlarm() {
		return this.activeAlarm_serviceUrl_SuffixUpdateStateOkAlarm;
	}

	public void setActiveAlarm_serviceUrl_SuffixUpdateStateOkAlarm(String activeAlarm_serviceUrl_SuffixUpdateStateOkAlarm) {
		this.activeAlarm_serviceUrl_SuffixUpdateStateOkAlarm = activeAlarm_serviceUrl_SuffixUpdateStateOkAlarm;
	}

	public String getActiveAlarm_serviceUrl_SuffixUpdateStateWarningAlarm() {
		return this.activeAlarm_serviceUrl_SuffixUpdateStateWarningAlarm;
	}

	public void setActiveAlarm_serviceUrl_SuffixUpdateStateWarningAlarm(
			String activeAlarm_serviceUrl_SuffixUpdateStateWarningAlarm) {
		this.activeAlarm_serviceUrl_SuffixUpdateStateWarningAlarm = activeAlarm_serviceUrl_SuffixUpdateStateWarningAlarm;
	}

	public String getActiveAlarm_serviceUrl_SuffixUpdateStateErrorAlarm() {
		return this.activeAlarm_serviceUrl_SuffixUpdateStateErrorAlarm;
	}

	public void setActiveAlarm_serviceUrl_SuffixUpdateStateErrorAlarm(
			String activeAlarm_serviceUrl_SuffixUpdateStateErrorAlarm) {
		this.activeAlarm_serviceUrl_SuffixUpdateStateErrorAlarm = activeAlarm_serviceUrl_SuffixUpdateStateErrorAlarm;
	}

	public String getActiveAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm() {
		return this.activeAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm;
	}

	public void setActiveAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm(
			String activeAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm) {
		this.activeAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm = activeAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm;
	}

	public String getActiveAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm() {
		return this.activeAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm;
	}

	public void setActiveAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm(
			String activeAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm) {
		this.activeAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm = activeAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm;
	}
	
	protected static AlarmEngineConfig readAlarmEngineConfig(Logger log,AlarmConfigProperties alarmConfigProperties) throws Exception{
		
		AlarmEngineConfig config = new AlarmEngineConfig();
		
		config.setActiveAlarm_serviceUrl(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL, true, true));
		
		String activeAlarm_serviceUrl_SuffixStartAlarm = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_SUFFIX_START_ALARM, false, true);
		if(activeAlarm_serviceUrl_SuffixStartAlarm!=null && StringUtils.isNotEmpty(activeAlarm_serviceUrl_SuffixStartAlarm)) {
			config.setActiveAlarm_serviceUrl_SuffixStartAlarm(activeAlarm_serviceUrl_SuffixStartAlarm);
		}
		String activeAlarm_serviceUrl_SuffixStopAlarm = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_SUFFIX_STOP_ALARM, false, true);
		if(activeAlarm_serviceUrl_SuffixStopAlarm!=null && StringUtils.isNotEmpty(activeAlarm_serviceUrl_SuffixStopAlarm)) {
			config.setActiveAlarm_serviceUrl_SuffixStopAlarm(activeAlarm_serviceUrl_SuffixStopAlarm);
		}
		String activeAlarm_serviceUrl_SuffixReStartAlarm = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_SUFFIX_RESTART_ALARM, false, true);
		if(activeAlarm_serviceUrl_SuffixReStartAlarm!=null && StringUtils.isNotEmpty(activeAlarm_serviceUrl_SuffixReStartAlarm)) {
			config.setActiveAlarm_serviceUrl_SuffixReStartAlarm(activeAlarm_serviceUrl_SuffixReStartAlarm);
		}
		
		String activeAlarm_serviceUrl_SuffixUpdateStateOkAlarm = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_SUFFIX_UPDATE_STATE_ALARM_OK, false, true);
		if(activeAlarm_serviceUrl_SuffixUpdateStateOkAlarm!=null && StringUtils.isNotEmpty(activeAlarm_serviceUrl_SuffixUpdateStateOkAlarm)) {
			config.setActiveAlarm_serviceUrl_SuffixUpdateStateOkAlarm(activeAlarm_serviceUrl_SuffixUpdateStateOkAlarm);
		}
		String activeAlarm_serviceUrl_SuffixUpdateStateWarningAlarm = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_SUFFIX_UPDATE_STATE_ALARM_WARNING, false, true);
		if(activeAlarm_serviceUrl_SuffixUpdateStateWarningAlarm!=null && StringUtils.isNotEmpty(activeAlarm_serviceUrl_SuffixUpdateStateWarningAlarm)) {
			config.setActiveAlarm_serviceUrl_SuffixUpdateStateWarningAlarm(activeAlarm_serviceUrl_SuffixUpdateStateWarningAlarm);
		}
		String activeAlarm_serviceUrl_SuffixUpdateStateErrorAlarm = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_SUFFIX_UPDATE_STATE_ALARM_ERROR, false, true);
		if(activeAlarm_serviceUrl_SuffixUpdateStateErrorAlarm!=null && StringUtils.isNotEmpty(activeAlarm_serviceUrl_SuffixUpdateStateErrorAlarm)) {
			config.setActiveAlarm_serviceUrl_SuffixUpdateStateErrorAlarm(activeAlarm_serviceUrl_SuffixUpdateStateErrorAlarm);
		}
		
		String activeAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_SUFFIX_UPDATE_ACK_ENABLED, false, true);
		if(activeAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm!=null && StringUtils.isNotEmpty(activeAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm)) {
			config.setActiveAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm(activeAlarm_serviceUrl_SuffixUpdateAcknoledgementEnabledAlarm);
		}
		String activeAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_ACTIVE_SERVICE_URL_SUFFIX_UPDATE_ACK_DISABLED, false, true);
		if(activeAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm!=null && StringUtils.isNotEmpty(activeAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm)) {
			config.setActiveAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm(activeAlarm_serviceUrl_SuffixUpdateAcknoledgementDisabledAlarm);
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
				
				mailSSLConfig.setTrustStoreLocation(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_TRUSTSTORE_LOCATION, true, true));
				mailSSLConfig.setTrustStoreType(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_TRUSTSTORE_TYPE, true, true));
				mailSSLConfig.setTrustStorePassword(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_TRUSTSTORE_PASSWORD, true, true));
				mailSSLConfig.setTrustManagementAlgorithm(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_TRUSTSTORE_MANAGEMENT_ALGORITHM, true, true));
				config.setMailStartTls(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SSL_START_TLS, true, true)));
				
				config.setMailSSLConfig(mailSSLConfig);
			}
			
			config.setMailFrom(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_FROM, true, true));
			config.setMailSubject(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SUBJECT, true, true));
			config.setMailBody(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_BODY, true, true));
		
			config.setMailAckMode(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_ACK_MODE, true, true)));
			
			config.setMailDebug(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_DEBUG, true, true)));
			
			config.setMailSendChangeStatusOk(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_MAIL_SEND_CHANGE_STATUS_OK, true, true)));

		}
		
		String alarmScript = alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_PATH, false, true);
		if(alarmScript!=null){
			config.setDefaultScriptPath(alarmScript);
			config.setDefaultScriptArgs(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_ARGS, true, true));
		}
		// L'ack mode è obbligatorio (lo script può essere impostato anche dentro l'allarme)
		config.setScriptAckMode(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_ACK_MODE, true, true)));
		config.setScriptDebug(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_DEBUG, true, true)));
		config.setScriptSendChangeStatusOk(Boolean.parseBoolean(alarmConfigProperties.getProperty(CostantiConfigurazione.ALARM_SCRIPT_SEND_CHANGE_STATUS_OK, true, true)));

		return config;
	}
}
