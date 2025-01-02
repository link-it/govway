/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils.transport.jms;

import jakarta.jms.JMSException;

/**
 * Gestisce eventuali errori del broker JMS per rinegoziare la connessione
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ExceptionListenerJMS implements jakarta.jms.ExceptionListener {

	private Exception exception = null;
	private boolean connessioneCorrotta = false;

	public ExceptionListenerJMS() {

	}

	@Override
	public void onException(JMSException arg0) {
		this.connessioneCorrotta = true;
		this.exception = arg0;
	}

	public Exception getException() {
		return this.exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public boolean isConnessioneCorrotta() {
		return this.connessioneCorrotta;
	}

	public void setConnessioneCorrotta(boolean connessioneCorrotta) {
		this.connessioneCorrotta = connessioneCorrotta;
	}

}
