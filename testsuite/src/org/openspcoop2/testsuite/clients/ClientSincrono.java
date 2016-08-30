/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.testsuite.clients;
import org.apache.axis.AxisFault;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;

/**
 * Client per la gestione del profilo di collaborazione Sincrono.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ClientSincrono extends ClientCore {

	public ClientSincrono(Repository rep) throws TestSuiteException{
		super();
		this.repository=rep;
	}


	public void run() throws TestSuiteException, AxisFault{

		// imposta la url e l'autenticazione per il soapEngine
		this.soapEngine.setConnectionParameters();
		// Effettua una invocazione sincrona
		invocazioneSincrona();
		// Messaggio spedito
		this.sentMessage=this.soapEngine.getRequestMessage();
		// Aggiungo nel repository il messaggio raccolto   
		this.repository.add(this.idMessaggio);
	}
}



