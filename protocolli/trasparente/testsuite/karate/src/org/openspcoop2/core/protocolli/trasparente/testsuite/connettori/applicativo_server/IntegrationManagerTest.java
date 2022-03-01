package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.applicativo_server;

import java.io.File;
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

public class IntegrationManagerTest extends ConfigLoader {

	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		cartellaRisposte.mkdir();
		if (!cartellaRisposte.isDirectory()|| !cartellaRisposte.canWrite()) {
			throw new RuntimeException("E' necessario creare la cartella per scrivere le richieste dei connettori, indicata dalla poprietà: <connettori.consegna_multipla.connettore_file.path> ");
		}
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
		
		org.openspcoop2.utils.Utilities.sleep(CommonConsegnaMultipla.intervalloControllo);
		
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
