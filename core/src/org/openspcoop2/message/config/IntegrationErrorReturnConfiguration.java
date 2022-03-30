/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.message.config;

/**
 * IntegrationErrorReturnCodeConfiguration
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IntegrationErrorReturnConfiguration implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int httpReturnCode;
	private int govwayReturnCode;
	
	private boolean retry;
	private int retryAfterSeconds = 60; // default
	private int retryRandomBackoffSeconds = 0; // default
	
	private boolean genericDetails;
	
	public int getHttpReturnCode() {
		return this.httpReturnCode;
	}
	public void setHttpReturnCode(int httpReturnCode) {
		this.httpReturnCode = httpReturnCode;
	}
	public int getGovwayReturnCode() {
		return this.govwayReturnCode;
	}
	public void setGovwayReturnCode(int govwayReturnCode) {
		this.govwayReturnCode = govwayReturnCode;
	}
	
	public boolean isRetry() {
		return this.retry;
	}
	public void setRetry(boolean retry) {
		this.retry = retry;
	}
	public int getRetryAfterSeconds() {
		return this.retryAfterSeconds;
	}
	public void setRetryAfterSeconds(int retryAfterSeconds) {
		this.retryAfterSeconds = retryAfterSeconds;
	}
	public int getRetryRandomBackoffSeconds() {
		return this.retryRandomBackoffSeconds;
	}
	public void setRetryRandomBackoffSeconds(int retryRandomBackoffSeconds) {
		this.retryRandomBackoffSeconds = retryRandomBackoffSeconds;
	}
	
	public void setGenericDetails(boolean genericDetails) {
		this.genericDetails = genericDetails;
	}
	public boolean isGenericDetails() {
		return this.genericDetails;
	}
}





