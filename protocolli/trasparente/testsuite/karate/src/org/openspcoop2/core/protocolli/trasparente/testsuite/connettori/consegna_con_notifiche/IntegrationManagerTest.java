package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_con_notifiche;

import static org.junit.Assert.assertEquals;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_OK;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;

public class IntegrationManagerTest extends ConfigLoader {
	
	final static QName SERVICE_NAME_MessageBox = new QName("http://services.pdd.openspcoop2.org", "MessageBoxService");
	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
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
	public void test() throws MalformedURLException, IntegrationManagerException_Exception {
		// Solo il connettore0 usa il servizio di message box, gli altri completano con 
		// 200 e soap fault
		//	PD imPDPort = null;	// TODO: Sembra questo non serva, rimuovi il jar.

		String erogazione 							= "TestConsegnaConNotificheIntegrationManager";
		String connettoreMessageBox 	= Common.CONNETTORE_0;
		Set<String> connettoriSuccesso = Set.of(Common.CONNETTORE_1,Common.CONNETTORE_2,Common.CONNETTORE_3);
		
		String username		= "UtenteTestIntegrationManager";
		String password 		= "YRpyf8)Zq4Z34kv27vJD";
		String url 					=  "http://localhost:8080/govway/IntegrationManager/MessageBox";
		URL wsdlLocation	= new URL(url+ "?wsdl");
		
		MessageBox imMessageBoxPort = null;
		MessageBoxService imMessageBoxService = new MessageBoxService(wsdlLocation, SERVICE_NAME_MessageBox);
		
		imMessageBoxPort = imMessageBoxService.getMessageBox();
		BindingProvider imProviderMessageBox = (BindingProvider)imMessageBoxPort;
		imProviderMessageBox.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		imProviderMessageBox.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
		imProviderMessageBox.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);

		
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		
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
		List<String> ids = imMessageBoxPort.getAllMessagesId();
		
		// Recupera messaggi
		imMessageBoxPort.getMessage("");
		// a questo punto sulla base dati dovresti verificare che sia incrementato il contatore dei prelievi e che la data di prelievo sia aggiornata
		
		
		// scarico nuovamente il singolo messaggio per verificare aggiornamenti dei contatori e delle data, verificando che sia mantenuta la data di primo prelievo
		imMessageBoxPort.getMessage("");
		
		// elimino il messaggio
		
		 imMessageBoxPort.deleteMessage("");

		 // una volta eliminato verifichi che la data di eliminazione sia valorizzata e la consegna completata.
	}

}
