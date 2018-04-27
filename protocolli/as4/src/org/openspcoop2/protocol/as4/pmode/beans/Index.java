/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

package org.openspcoop2.protocol.as4.pmode.beans;

/**
 * Index
 * 
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: apoli $
 * @version $Rev: 13800 $, $Date: 2018-04-04 16:39:08 +0200 (Wed, 04 Apr 2018) $
 * 
 */
public class Index {

	private int legId = 0;
	private int processId = 0;
	private int serviceId = 0;
	private int actionId = 0;
	
	public synchronized int getNextLegId() {
		this.legId++;
		return this.legId;
	}
	
	public synchronized int getNextProcessId() {
		this.processId++;
		return this.processId;
	}
	
	public synchronized int getNextServiceId() {
		this.serviceId++;
		return this.serviceId;
	}
	
	public synchronized int getNextActionId() {
		this.actionId++;
		return this.actionId;
	}
	
}
