/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.utils;

/**
 * Eccezione lanciata quando un semaforo non può essere acquisito entro il timeout configurato.
 *
 * NOTA: Questa eccezione NON estende InterruptedException perché rappresenta un timeout
 * (condizione di attesa scaduta) e non una vera interruzione del thread.
 *
 * Distinguere tra timeout e interrupt è importante per:
 * - Permettere operazioni successive che potrebbero essere sensibili allo stato di interrupt
 *   (es. generazione UUID che usa ArrayBlockingQueue.take())
 * - Preservare la semantica corretta: interrupt status dovrebbe essere impostato solo
 *   quando il thread è realmente interrotto, non quando semplicemente scade un timeout
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemaphoreTimeoutException extends Exception {

	private static final long serialVersionUID = 1L;

	private String semaphoreName;
	private long timeoutMs;

	public SemaphoreTimeoutException(String semaphoreName, long timeoutMs) {
		super("Could not acquire semaphore ["+semaphoreName+"] after "+timeoutMs+"ms");
		this.semaphoreName = semaphoreName;
		this.timeoutMs = timeoutMs;
	}

	public SemaphoreTimeoutException(String message) {
		super(message);
	}

	public String getSemaphoreName() {
		return this.semaphoreName;
	}

	public long getTimeoutMs() {
		return this.timeoutMs;
	}
}
