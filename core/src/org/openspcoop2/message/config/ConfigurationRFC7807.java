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
package org.openspcoop2.message.config;

import java.io.Serializable;

/**
 * ConfigurationRFC7807
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConfigurationRFC7807 implements Serializable  {

	private static final long serialVersionUID = 1L;



	protected boolean useAcceptHeader;

	protected boolean details;

	protected boolean instance;

	protected boolean govwayStatus;
	
	protected boolean govwayType;

	protected boolean govwayTransactionId;

	protected boolean type = true;

	protected java.lang.String typeFormat = null;


	public ConfigurationRFC7807() {
	}

	public boolean isUseAcceptHeader() {
		return this.useAcceptHeader;
	}

	public boolean getUseAcceptHeader() {
		return this.useAcceptHeader;
	}

	public void setUseAcceptHeader(boolean useAcceptHeader) {
		this.useAcceptHeader = useAcceptHeader;
	}

	public boolean isDetails() {
		return this.details;
	}

	public boolean getDetails() {
		return this.details;
	}

	public void setDetails(boolean details) {
		this.details = details;
	}

	public boolean isInstance() {
		return this.instance;
	}

	public boolean getInstance() {
		return this.instance;
	}

	public void setInstance(boolean instance) {
		this.instance = instance;
	}

	public boolean isGovwayStatus() {
		return this.govwayStatus;
	}

	public boolean getGovwayStatus() {
		return this.govwayStatus;
	}
	
	public void setGovwayStatus(boolean govwayStatus) {
		this.govwayStatus = govwayStatus;
	}

	public boolean isGovwayType() {
		return this.govwayType;
	}

	public boolean getGovwayType() {
		return this.govwayType;
	}
	
	public void setGovwayType(boolean govwayType) {
		this.govwayType = govwayType;
	}
	
	public boolean isGovwayTransactionId() {
		return this.govwayTransactionId;
	}

	public void setGovwayTransactionId(boolean govwayTransactionId) {
		this.govwayTransactionId = govwayTransactionId;
	}
	
	public boolean isType() {
		return this.type;
	}

	public boolean getType() {
		return this.type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public java.lang.String getTypeFormat() {
		return this.typeFormat;
	}

	public void setTypeFormat(java.lang.String typeFormat) {
		this.typeFormat = typeFormat;
	}

}
