/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

/**
 * SemaphoreConfiguration
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemaphoreConfiguration {

	private String idNode;
	
	private long maxIdleTime;
	private long maxLife;
	private boolean emitEvent; 
	private ISemaphoreEventGenerator eventGenerator;
	
	private long serializableTimeWaitMs = 60000; // tempo massimo di attesa in millisecondi
	private int serializableNextIntervalTimeMs = 100; // nuovo tentativo ogni 100 millisecondi (random 0-100)
	
	// E' possibile attivare anche il next interval time ms increment mode.
	// Se abilitato il nuovo tenntativo viene effettuato nell'intervallo (0 - (serializableNextIntervalTimeMs+(serializableNextIntervalTimeMsIncrement*iterazione))) 
	// fino ad un massimo intervallo destro di maxSerializableNextIntervalTimeMs
	private boolean serializableNextIntervalTimeMsIncrementMode = true;
	private int serializableNextIntervalTimeMsIncrement = 200;
	private int maxSerializableNextIntervalTimeMs = 2000;
	
	// Non e' necessario se la riga utilizzata gia' esiste (richiede una init)
	private boolean serializableLevel = true;
	
	public String getIdNode() {
		return this.idNode;
	}
	public void setIdNode(String idNode) {
		this.idNode = idNode;
	}
	
	public ISemaphoreEventGenerator getEventGenerator() {
		return this.eventGenerator;
	}
	public void setEventGenerator(ISemaphoreEventGenerator eventGenerator) {
		this.eventGenerator = eventGenerator;
	}
	public long getMaxIdleTime() {
		return this.maxIdleTime;
	}
	public void setMaxIdleTime(long maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}
	public long getMaxLife() {
		return this.maxLife;
	}
	public void setMaxLife(long maxLife) {
		this.maxLife = maxLife;
	}
	public boolean isEmitEvent() {
		return this.emitEvent;
	}
	public void setEmitEvent(boolean emitEvent) {
		this.emitEvent = emitEvent;
	}
	
	public long getSerializableTimeWaitMs() {
		return this.serializableTimeWaitMs;
	}
	public void setSerializableTimeWaitMs(long serializableTimeWaitMs) {
		this.serializableTimeWaitMs = serializableTimeWaitMs;
	}
	
	public int getSerializableNextIntervalTimeMs() {
		return this.serializableNextIntervalTimeMs;
	}
	public void setSerializableNextIntervalTimeMs(int serializableNextIntervalTimeMs) {
		this.serializableNextIntervalTimeMs = serializableNextIntervalTimeMs;
	}
	
	public boolean isSerializableNextIntervalTimeMsIncrementMode() {
		return this.serializableNextIntervalTimeMsIncrementMode;
	}
	public void setSerializableNextIntervalTimeMsIncrementMode(boolean serializableNextIntervalTimeMsIncrementMode) {
		this.serializableNextIntervalTimeMsIncrementMode = serializableNextIntervalTimeMsIncrementMode;
	}
	public int getSerializableNextIntervalTimeMsIncrement() {
		return this.serializableNextIntervalTimeMsIncrement;
	}
	public void setSerializableNextIntervalTimeMsIncrement(int serializableNextIntervalTimeMsIncrement) {
		this.serializableNextIntervalTimeMsIncrement = serializableNextIntervalTimeMsIncrement;
	}
	public int getMaxSerializableNextIntervalTimeMs() {
		return this.maxSerializableNextIntervalTimeMs;
	}
	public void setMaxSerializableNextIntervalTimeMs(int maxSerializableNextIntervalTimeMs) {
		this.maxSerializableNextIntervalTimeMs = maxSerializableNextIntervalTimeMs;
	}
	
	public boolean isSerializableLevel() {
		return this.serializableLevel;
	}
	public void setSerializableLevel(boolean serializableLevel) {
		this.serializableLevel = serializableLevel;
	}
}
