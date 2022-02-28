package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_con_notifiche;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_OK;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla;
import org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception;
import org.openspcoop2.pdd.services.cxf.MessageBox;
import org.openspcoop2.pdd.services.cxf.MessageBoxService;
import org.openspcoop2.pdd.services.skeleton.IdentificativoIM;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;

public class IntegrationManagerTest extends ConfigLoader {
	
	final static QName SERVICE_NAME_MessageBox = new QName("http://services.pdd.openspcoop2.org", "MessageBoxService");

	final static String url 	= System.getProperty("connettori.message_box.url");

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
	
	private MessageBox getMessageBox(String username, String password) throws MalformedURLException {
		URL wsdlLocation							= new URL(url+ "?wsdl");
		MessageBoxService imMessageBoxService	= new MessageBoxService(wsdlLocation, SERVICE_NAME_MessageBox);
		MessageBox imMessageBoxPort 						= imMessageBoxService.getMessageBox();
		BindingProvider imProviderMessageBox 		= (BindingProvider) imMessageBoxPort;
		
		imProviderMessageBox.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		imProviderMessageBox.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
		imProviderMessageBox.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		
		return imMessageBoxPort;
	}
	
	@Test
	public void consegnaConNotifiche() throws MalformedURLException, IntegrationManagerException_Exception {
		// Solo il connettore0 usa il servizio di message box, gli altri completano con 
		// 200 e soap fault

		String erogazione 							= "TestConsegnaConNotificheIntegrationManager";
		String connettoreMessageBox 	= Common.CONNETTORE_0;
		String username	= "UtenteTestIntegrationManager";
		String password 	= "YRpyf8)Zq4Z34kv27vJD";
		Set<String> connettoriSuccesso = Set.of(Common.CONNETTORE_1,Common.CONNETTORE_2,Common.CONNETTORE_3);
		
		MessageBox imMessageBoxPort = getMessageBox(username, password);
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);

		var responsesSoap1 = Common.makeParallelRequests(request1, 10);
		var responsesSoap2 = Common.makeParallelRequests(request2, 10);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var response : responsesSoap1) {
			assertEquals(200, response.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(response, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(response, Common.setConnettoriAbilitati);
			
			// check consegna_im
			String query = "select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? and consegna_im = ? ";
			
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
			Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettoreMessageBox, true);
			assertEquals(Integer.valueOf(1), count);
		}
		
		// Ripeto per soap2
		for (var response : responsesSoap2) {
			assertEquals(200, response.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(response, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(response, Common.setConnettoriAbilitati);	
			
			// check consegna_im
			String query = "select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? and consegna_im = ? ";
			
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
			Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettoreMessageBox, true);
			assertEquals(Integer.valueOf(1), count);
		}
	
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);

		// Controllo che le notifiche siano completate sui connettori non message box
		for (var response : responsesSoap1) {
			CommonConsegnaMultipla.checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			for (var connettore : connettoriSuccesso) {
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, connettore,ESITO_OK, 200, "" , "");
			}
			// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati, ci metto anche quello che fa da MessageBox
			CommonConsegnaMultipla.checkConnettoriRaggiuntiEsclusivamente(response, Common.setConnettoriAbilitati);
		}
		
		// ripeto per soap2
		for (var response : responsesSoap2) {
			CommonConsegnaMultipla.checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			for (var connettore : connettoriSuccesso) {
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, connettore,ESITO_OK, 200, "" , "");			
			}
			// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati, ci metto anche quello che fa da MessageBox
			CommonConsegnaMultipla.checkConnettoriRaggiuntiEsclusivamente(response, Common.setConnettoriAbilitati);		
		}
		
		// Recupero i messaggi
		// ID messaggi
		checkMessageBox(connettoreMessageBox, imMessageBoxPort);

	}

	
	@Test
	public void consegnaMultipla() throws MalformedURLException, IntegrationManagerException_Exception {
		/**
		 * Diversamente dalla consegna con notifiche, In questa erogazione la messageBox è fornita da un applicativo server.
		 */
		
		String erogazione 							= "TestConsegnaMultiplaIntegrationManager";
		String connettoreMessageBox 	= Common.CONNETTORE_0;
		Set<String> connettoriSuccesso = Set.of(Common.CONNETTORE_1,Common.CONNETTORE_2,Common.CONNETTORE_3);
		
		String username	= "UtenteTestApplicativoMessageBox";
		String password 	= "JzpvsjyUU63z5RB38#4F";
		
		MessageBox imMessageBoxPort = getMessageBox(username, password);
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);

		var responsesSoap1 = Common.makeParallelRequests(request1, 10);
		var responsesSoap2 = Common.makeParallelRequests(request2, 10);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var response : responsesSoap1) {
			assertEquals(200, response.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(response, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(response, Common.setConnettoriAbilitati);
			
			// check consegna_im
			String query = "select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? and consegna_im = ? ";
			
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
			Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettoreMessageBox, true);
			assertEquals(Integer.valueOf(1), count);
		}
		
		// Ripeto per soap2
		for (var response : responsesSoap2) {
			assertEquals(200, response.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(response, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(response, Common.setConnettoriAbilitati);	
			
			// check consegna_im
			String query = "select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? and consegna_im = ? ";
			
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
			Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettoreMessageBox, true);
			assertEquals(Integer.valueOf(1), count);
		}
	
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);

		// Controllo che le notifiche siano completate sui connettori non message box
		for (var response : responsesSoap1) {
			CommonConsegnaMultipla.checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			for (var connettore : connettoriSuccesso) {
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, connettore,ESITO_OK, 200, "" , "");
			}
			// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati, ci metto anche quello che fa da MessageBox
			CommonConsegnaMultipla.checkConnettoriRaggiuntiEsclusivamente(response, Common.setConnettoriAbilitati);
		}
		
		// ripeto per soap2
		for (var response : responsesSoap2) {
			CommonConsegnaMultipla.checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			for (var connettore : connettoriSuccesso) {
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, connettore,ESITO_OK, 200, "" , "");			
			}
			// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati, ci metto anche quello che fa da MessageBox
			CommonConsegnaMultipla.checkConnettoriRaggiuntiEsclusivamente(response, Common.setConnettoriAbilitati);		
		}
		
		checkMessageBox(connettoreMessageBox, imMessageBoxPort);
	}
	
	
	@Test
	public void testIntegrationManager() throws MalformedURLException, IntegrationManagerException_Exception {
		
		String erogazione 							= "TestIntegrationManager";
		String connettoreMessageBox 	= "Default";	
		
		String username	= "UtenteTestIntegrationManagerNoConnettoriMultipli";
		String password 	= "9J1rQBrW1)sl99Lsq3rS";
		
		MessageBox imMessageBoxPort = getMessageBox(username, password);
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		
		Common.makeParallelRequests(request1, 10);
		Common.makeParallelRequests(request2, 10);
		
		checkMessageBox(connettoreMessageBox, imMessageBoxPort);
	}
	
	
	private void checkMessageBox(String connettoreMessageBox, MessageBox imMessageBoxPort)
			throws IntegrationManagerException_Exception {
		List<String> ids = imMessageBoxPort.getAllMessagesId();
		assertFalse(ids.isEmpty());
		
		// Recupero il  messaggio, verifico che sia incrementato il contatore dei prelievi e che la data di prelievo sia aggiornata
		String query = "Select data_primo_prelievo_im from transazioni_sa where identificativo_messaggio = ? and connettore_nome = ? and data_primo_prelievo_im >= ? and data_registrazione >= ? and data_prelievo_im = data_primo_prelievo_im and numero_prelievi_im = 1 and data_eliminazione_im is null";
		getLoggerCore().info("Checking db for idMessaggio: " + ids.get(0));
		IdentificativoIM idAndDate = org.openspcoop2.pdd.services.skeleton.IdentificativoIM.getIdentificativoIM(ids.get(0), getLoggerCore());

		String idMessaggio = idAndDate.getId();
		Timestamp dataRegistrazioneMessageBox = Timestamp.from(idAndDate.getData().toInstant());
		Timestamp dataRiferimentoTest = Timestamp.from(Instant.now());

		imMessageBoxPort.getMessage(idMessaggio);

		getLoggerCore().info("Checking stato per connettore messageBox: " + idMessaggio + " " + connettoreMessageBox + " " + dataRiferimentoTest.toString() );
		
		Timestamp dataPrimoPrelievo = ConfigLoader.getDbUtils().readValue(query, Timestamp.class, idMessaggio,	connettoreMessageBox, dataRiferimentoTest, dataRegistrazioneMessageBox); 
		
		// scarico nuovamente il singolo messaggio per verificare aggiornamenti dei contatori e delle data, verificando che sia mantenuta la data di primo prelievo
		query = "Select count(*) from transazioni_sa where identificativo_messaggio = ? and connettore_nome = ? and data_primo_prelievo_im = ? and data_prelievo_im >= ? and numero_prelievi_im = 2 and data_eliminazione_im is null";
		dataRiferimentoTest = Timestamp.from(Instant.now());

		imMessageBoxPort.getMessage(idMessaggio);

		getLoggerCore().info("Checking secondo prelievo per connettore messageBox: " + idMessaggio + " " + connettoreMessageBox + " " + dataRiferimentoTest.toString() );

		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, idMessaggio,	connettoreMessageBox, dataPrimoPrelievo, dataRiferimentoTest);
		assertEquals(Integer.valueOf(1), count);
				
		// elimino il messaggio, verifico che la data di eliminazione sia valorizzata e la consegna completata.

		dataRiferimentoTest = Timestamp.from(Instant.now());
		imMessageBoxPort.deleteMessage(idMessaggio);
		 
		query = "Select count(*) from transazioni_sa where identificativo_messaggio = ? and connettore_nome = ? and data_primo_prelievo_im = ? and data_eliminazione_im >= ? and  consegna_terminata = ? and numero_prelievi_im = 2";
		count = ConfigLoader.getDbUtils().readValue(query, Integer.class, idMessaggio,connettoreMessageBox, dataPrimoPrelievo, dataRiferimentoTest,true);
		assertEquals(Integer.valueOf(1), count);
	}

}
