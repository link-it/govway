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

package org.openspcoop2.utils.mail;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.transport.http.SSLConfig;

/**
 * Mail
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Mail {

	private String serverHost;
	private int serverPort;
	private String username;
	private String password;
	private SSLConfig sslConfig;
	private boolean startTls;
	
	private String encoding = "UTF8";
	
	private String from;
	private String to;
	private List<String> cc = new ArrayList<String>();	
	private String subject;
	
	private String userAgent;
	private String contentLanguage;
	private String messageIdDomain;
	
	private MailBody body = new MailBody();
	
	public String getServerHost() {
		return this.serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public int getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SSLConfig getSslConfig() {
		return this.sslConfig;
	}

	public void setSslConfig(SSLConfig sslConfig) {
		this.sslConfig = sslConfig;
	}

	public boolean isStartTls() {
		return this.startTls;
	}

	public void setStartTls(boolean startTls) {
		this.startTls = startTls;
	}

	public String getFrom() {
		return this.from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return this.to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public List<String> getCc() {
		return this.cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public MailBody getBody() {
		return this.body;
	}

	public void setBody(MailBody body) {
		this.body = body;
	}
	
	public String getEncoding() {
		return this.encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String getUserAgent() {
		return this.userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public String getContentLanguage() {
		return this.contentLanguage;
	}

	public void setContentLanguage(String contentLanguage) {
		this.contentLanguage = contentLanguage;
	}
	
	public String getMessageIdDomain() {
		return this.messageIdDomain;
	}

	public void setMessageIdDomain(String messageIdDomain) {
		this.messageIdDomain = messageIdDomain;
	}
}
