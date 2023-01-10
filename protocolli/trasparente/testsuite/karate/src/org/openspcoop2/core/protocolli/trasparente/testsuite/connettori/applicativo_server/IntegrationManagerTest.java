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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.applicativo_server;

import java.net.MalformedURLException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla;
import org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception;
import org.openspcoop2.pdd.services.cxf.MessageBox;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;

/**
* IntegrationManagerTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class IntegrationManagerTest extends ConfigLoader {

	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
	}
	
	
	@org.junit.AfterClass
	public static void After() {
		Common.fermaRiconsegne(dbUtils);
	}
	
	
	@org.junit.After
	public void AfterEach() {
		Common.fermaRiconsegne(dbUtils);		
	}
	

	@Test
	public void integrationManager() throws MalformedURLException, IntegrationManagerException_Exception {
		
		String erogazione 							= "TestIntegrationManager";
		String connettoreMessageBox 	= "Default";	
		
		String username	= "UtenteTestApplicativoMessageBox";
		String password 	= "JzpvsjyUU63z5RB38#4F";
		
		MessageBox imMessageBoxPort = CommonConsegnaMultipla.getMessageBox(username, password);
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		
		var responsesSoap1 = Common.makeParallelRequests(request1, 10);
		var responsesSoap2 = Common.makeParallelRequests(request2, 10);
		
		CommonConsegnaMultipla.checkMessageBox(connettoreMessageBox, imMessageBoxPort);
		
		// Adesso cancello tutti i messaggi in pancia in modo che la consegna risulti completata
		imMessageBoxPort.deleteAllMessages();
		
		for (var response : responsesSoap1) {
			CommonConsegnaMultipla.checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_MESSAGE_BOX, 0);
			CommonConsegnaMultipla.checkConsegnaTerminataNoStatusCode(response, connettoreMessageBox);
		}
		for (var response : responsesSoap2) {
			CommonConsegnaMultipla.checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_MESSAGE_BOX, 0);
			CommonConsegnaMultipla.checkConsegnaTerminataNoStatusCode(response, connettoreMessageBox);
		}
		
	}
	
	
	

}
