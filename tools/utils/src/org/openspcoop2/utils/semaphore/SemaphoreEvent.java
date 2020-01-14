/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.semaphore;

import java.util.Date;

/**
 * SemaphoreEvent
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemaphoreEvent {

	private String details;
	private Date date;
	private SemaphoreEventSeverity severity;
	private SemaphoreOperationType operationType;
	private boolean lock;
	private String idNode;
	
	public boolean isLock() {
		return this.lock;
	}
	public void setLock(boolean lock) {
		this.lock = lock;
	}
	public String getIdNode() {
		return this.idNode;
	}
	public void setIdNode(String idNode) {
		this.idNode = idNode;
	}
	public SemaphoreOperationType getOperationType() {
		return this.operationType;
	}
	public void setOperationType(SemaphoreOperationType operationType) {
		this.operationType = operationType;
	}
	public String getDetails() {
		return this.details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public Date getDate() {
		return this.date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public SemaphoreEventSeverity getSeverity() {
		return this.severity;
	}
	public void setSeverity(SemaphoreEventSeverity severity) {
		this.severity = severity;
	}
}
