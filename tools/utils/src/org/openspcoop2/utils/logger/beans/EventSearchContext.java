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
package org.openspcoop2.utils.logger.beans;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.utils.logger.constants.SortOrder;
import org.openspcoop2.utils.logger.IEventSearchContext;
import org.openspcoop2.utils.logger.constants.Severity;

/**
 * EventSearchContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EventSearchContext implements IEventSearchContext,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date leftIntervalDate;
	private Date rightIntervalDate;
	private String source;
	private String code;
	private Severity severity;
	private IdentifierSearch correlationIdentifier;
	private IdentifierSearch clusterId;
	private IdentifierSearch configurationId;
	private SortOrder sortOrder;
		
	@Override
	public Date getLeftIntervalDate() {
		return this.leftIntervalDate;
	}
	@Override
	public void setLeftIntervalDate(Date leftIntervalDate) {
		this.leftIntervalDate = leftIntervalDate;
	}
	@Override
	public Date getRightIntervalDate() {
		return this.rightIntervalDate;
	}
	@Override
	public void setRightIntervalDate(Date rightIntervalDate) {
		this.rightIntervalDate = rightIntervalDate;
	}
	
	@Override
	public IdentifierSearch getConfigurationId() {
		return this.configurationId;
	}
	@Override
	public void setConfigurationId(IdentifierSearch configurationId) {
		this.configurationId = configurationId;
	}
	
	@Override
	public String getSource() {
		return this.source;
	}
	@Override
	public void setSource(String source) {
		this.source = source;
	}
	
	@Override
	public String getCode() {
		return this.code;
	}
	@Override
	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public Severity getSeverity() {
		return this.severity;
	}
	@Override
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	@Override
	public IdentifierSearch getCorrelationIdentifier() {
		return this.correlationIdentifier;
	}
	@Override
	public void setCorrelationIdentifier(IdentifierSearch correlationIdentifier) {
		this.correlationIdentifier = correlationIdentifier;
	}
	
	@Override
	public IdentifierSearch getClusterId() {
		return this.clusterId;
	}
	@Override
	public void setClusterId(IdentifierSearch clusterId) {
		this.clusterId = clusterId;
	}
	
	@Override
	public SortOrder getSortOrder() {
		return this.sortOrder;
	}

	@Override
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

}
