/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.utils.logger;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.utils.logger.constants.SortOrder;
import org.openspcoop2.utils.logger.beans.IdentifierSearch;
import org.openspcoop2.utils.logger.constants.Severity;

/**
 * IEventSearchContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IEventSearchContext extends Serializable {

	public SortOrder getSortOrder();
	public void setSortOrder(SortOrder sortOrder);
	
	public IdentifierSearch getConfigurationId();
	public void setConfigurationId(IdentifierSearch configurationId);
	
	public Date getLeftIntervalDate();
	public void setLeftIntervalDate(Date date);
	
	public Date getRightIntervalDate();
	public void setRightIntervalDate(Date date);
	
	public String getSource();
	public void setSource(String source);
	
	public String getCode();
	public void setCode(String code);
	
	public Severity getSeverity();
	public void setSeverity(Severity severity);
	
	public IdentifierSearch getCorrelationIdentifier();
	public void setCorrelationIdentifier(IdentifierSearch correlationIdentifier);
	
	public IdentifierSearch getClusterId();
	public void setClusterId(IdentifierSearch clusterId);
	
}
