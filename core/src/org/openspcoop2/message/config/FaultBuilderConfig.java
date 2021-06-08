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

package org.openspcoop2.message.config;

import javax.xml.namespace.QName;

/**
 * FaultBuilderConfig
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FaultBuilderConfig {

	private Integer httpReturnCode;
	private Integer govwayReturnCode;
	
	private boolean rfc7807Type = true;
	private String rfc7807WebSite;
	private String rfc7807Title;
	private String rfc7807GovWayTypeHeaderErrorTypeName;
	private String rfc7807GovWayTypeHeaderErrorTypeValue;
	
	private String actor;
	private String prefixSoap;
	
	private QName errorCode;
	
	private String headerErrorTypeName;
	private String headerErrorTypeValue;
	
	private String details;
	
	public boolean isRfc7807Type() {
		return this.rfc7807Type;
	}
	public void setRfc7807Type(boolean rfc7807type) {
		this.rfc7807Type = rfc7807type;
	}
	public String getRfc7807WebSite() {
		return this.rfc7807WebSite;
	}
	public void setRfc7807WebSite(String rfc7807WebSite) {
		this.rfc7807WebSite = rfc7807WebSite;
	}
	public String getRfc7807Title() {
		return this.rfc7807Title;
	}
	public void setRfc7807Title(String rfc7807Title) {
		this.rfc7807Title = rfc7807Title;
	}
	public String getRfc7807GovWayTypeHeaderErrorTypeName() {
		return this.rfc7807GovWayTypeHeaderErrorTypeName;
	}
	public void setRfc7807GovWayTypeHeaderErrorTypeName(String rfc7807GovWayTypeHeaderErrorTypeName) {
		this.rfc7807GovWayTypeHeaderErrorTypeName = rfc7807GovWayTypeHeaderErrorTypeName;
	}
	public String getRfc7807GovWayTypeHeaderErrorTypeValue() {
		return this.rfc7807GovWayTypeHeaderErrorTypeValue;
	}
	public void setRfc7807GovWayTypeHeaderErrorTypeValue(String rfc7807GovWayTypeHeaderErrorTypeValue) {
		this.rfc7807GovWayTypeHeaderErrorTypeValue = rfc7807GovWayTypeHeaderErrorTypeValue;
	}
	
	public String getActor() {
		return this.actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getPrefixSoap() {
		return this.prefixSoap;
	}
	public void setPrefixSoap(String prefixSoap) {
		this.prefixSoap = prefixSoap;
	}
	public QName getErrorCode() {
		return this.errorCode;
	}
	public void setErrorCode(QName errorCode) {
		this.errorCode = errorCode;
	}
	public Integer getHttpReturnCode() {
		return this.httpReturnCode;
	}
	public void setHttpReturnCode(Integer httpReturnCode) {
		this.httpReturnCode = httpReturnCode;
	}
	public Integer getGovwayReturnCode() {
		return this.govwayReturnCode;
	}
	public void setGovwayReturnCode(Integer govwayReturnCode) {
		this.govwayReturnCode = govwayReturnCode;
	}

	public String getHeaderErrorTypeName() {
		return this.headerErrorTypeName;
	}
	public void setHeaderErrorTypeName(String headerErrorTypeName) {
		this.headerErrorTypeName = headerErrorTypeName;
	}
	public String getHeaderErrorTypeValue() {
		return this.headerErrorTypeValue;
	}
	public void setHeaderErrorTypeValue(String headerErrorTypeValue) {
		this.headerErrorTypeValue = headerErrorTypeValue;
	}
	
	public String getDetails() {
		return this.details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
}
