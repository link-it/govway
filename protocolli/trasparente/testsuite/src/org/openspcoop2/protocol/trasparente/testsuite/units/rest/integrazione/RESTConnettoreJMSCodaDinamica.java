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
package org.openspcoop2.protocol.trasparente.testsuite.units.rest.integrazione;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import jakarta.jms.JMSException;
import jakarta.jms.Message;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.testsuite.core.EmbeddedJMSBroker;
import org.openspcoop2.testsuite.core.JMSTestUtilities;
import org.openspcoop2.testsuite.core.JMSTestUtilities.JMSConsumer;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.db.VerificatoreTransazioni;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Test sul connettore JMS con nome della coda dinamico per le API REST.
 *
 * La trasformazione della richiesta, di tipo 'Alimentazione Contesto (Freemarker Template)', legge tramite jsonPath
 * il nome della coda presente nel payload e lo salva nel contesto; il connettore JMS utilizza come nome della coda
 * 'queue/openspcoop2TestQueue${context:jmsCoda}': payload differenti vengono quindi pubblicati su code differenti.
 *
 * Su WildFly le code 'openspcoop2TestQueueA' e 'openspcoop2TestQueueB' devono essere definite nel broker interno
 * (protocolli/spcoop/testsuite/deploy/code_jms/wildfly_ActiveMQArtemis/testsuite-destinations-activemq-jms.xml); su tomcat, o in modalità jenkins
 * (opzione '-Djenkins=true'), viene utilizzato il broker embedded della testsuite (EmbeddedJMSBroker) e il binding JNDI
 * della coda è definito tramite una proprietà del connettore il cui nome e valore vengono risolti a runtime.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RESTConnettoreJMSCodaDinamica {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "RESTConnettoreJMSCodaDinamica";

	private static final String PORTA_APPLICATIVA = "APIMinisteroErogatore/RESTJMS_CodaDinamica";
	private static final String SERVIZIO_APPLICATIVO = "RESTJMSQueueDinamicaAsText";
	private static final String QUEUE_PREFIX = "queue/openspcoop2TestQueue";
	private static final String CODA_A = "A";
	private static final String CODA_B = "B";

	private static final String HEADER_ID_TRANSAZIONE = "GovWay-Transaction-ID";

	private static final long RECEIVE_TIMEOUT_MS = 10000;
	/** Attesa utilizzata per verificare che il messaggio non sia stato pubblicato sull'altra coda */
	private static final long RECEIVE_TIMEOUT_ALTRA_CODA_MS = 2000;
	private static final long DB_TIMEOUT_MS = 10000;

	private static final int ESITO_OK = 0;
	/** Severita' massima dei diagnostici di errore (fatal, errorProtocol, errorIntegration) */
	private static final int SEVERITA_ERRORE = 2;
	/** Codice HTTP restituito dal connettore JMS per le API REST */
	private static final int CODICE_HTTP_JMS_REST = 204;



	private Date dataAvvioGruppoTest = null;
	private JMSTestUtilities jms = null;

	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws TestSuiteException {
		this.dataAvvioGruppoTest = DateManager.getDate();

		String versionJbossas = null;
		try{
			versionJbossas = Utilities.readApplicationServerVersion();
		}catch(Exception e){
			System.err.println("Identificazione A.S. non riuscita: "+e.getMessage());
			e.printStackTrace(System.out);
		}

		// Su tomcat, o in modalità jenkins, le code sono gestite dal broker embedded della testsuite
		if(EmbeddedJMSBroker.isRequired(versionJbossas)){
			EmbeddedJMSBroker.start();
		}

		TestSuiteProperties prop = TestSuiteProperties.getInstance();
		Properties jndiContext = prop.getJMS_JNDIContext();
		if(EmbeddedJMSBroker.isStarted()) {
			EmbeddedJMSBroker.addQueue(jndiContext, QUEUE_PREFIX+CODA_A);
			EmbeddedJMSBroker.addQueue(jndiContext, QUEUE_PREFIX+CODA_B);
		}
		this.jms = new JMSTestUtilities(jndiContext, prop.getJMSConnectionFactory(), prop.getJMSUsername(), prop.getJMSPassword());
	}
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws TestSuiteException {
		try {
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}catch(TestSuiteException e) {
			throw e;
		}catch(Exception e) {
			throw new TestSuiteException("Verifica openspcoop2_core.log non riuscita", e);
		}
	}





	// UTILITIES

	private static String getJMSPropertyName(String headerTrasporto) {
		return headerTrasporto.replace("X-", "").replace("-", "");
	}

	private void svuotaCoda(String queue) throws TestSuiteException {
		try {
			this.jms.svuotaCoda(queue, getJMSPropertyName(HEADER_ID_TRANSAZIONE));
		}catch(UtilsException | JMSException e) {
			throw new TestSuiteException("Svuotamento della coda '"+queue+"' non riuscito", e);
		}
	}

	private static HttpResponse invoke(String contenuto) throws TestSuiteException {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(contenuto.getBytes(StandardCharsets.UTF_8));
		request.setReadTimeout(org.openspcoop2.testsuite.core.CostantiTestSuite.READ_TIMEOUT);
		String url = Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore()+PORTA_APPLICATIVA;
		request.setUrl(url);
		Reporter.log("URL: "+url+" (richiesta: "+contenuto+")");
		try {
			HttpResponse response = HttpUtilities.httpInvoke(request);
			Reporter.log("Ricevuto codice HTTP '"+response.getResultHTTPOperation()+"' (id transazione: "+response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE)+")");
			return response;
		}catch(UtilsException e) {
			throw new TestSuiteException("Invocazione della porta '"+PORTA_APPLICATIVA+"' non riuscita", e);
		}
	}

	/**
	 * Legge dalla coda il messaggio pubblicato dal test, individuato tramite l'identificativo univoco presente nel contenuto.
	 *
	 * @return il messaggio letto o null se entro il timeout non è stato pubblicato
	 */
	private Message readMessaggioPubblicato(String queue, String idUnivoco, long timeoutMs) throws TestSuiteException {
		try (JMSConsumer consumer = this.jms.createConsumer(queue)){
			long scadenza = System.currentTimeMillis() + timeoutMs;
			while(System.currentTimeMillis() < scadenza) {
				Message msg = consumer.receive(Math.max(1, scadenza - System.currentTimeMillis()));
				if(msg==null) {
					break;
				}
				String contenuto = new String(JMSTestUtilities.readContent(msg, true), StandardCharsets.UTF_8);
				if(contenuto.contains(idUnivoco)) {
					return msg;
				}
				System.out.println("WARNING: scartato messaggio non atteso (id transazione: "+msg.getStringProperty(getJMSPropertyName(HEADER_ID_TRANSAZIONE))+") letto da '"+queue+"'");
			}
			return null;
		}catch(UtilsException | JMSException e) {
			throw new TestSuiteException("Lettura del messaggio con identificativo '"+idUnivoco+"' dalla coda '"+queue+"' non riuscita", e);
		}
	}

	/**
	 * Verifica su database l'esito della transazione e il diagnostico di consegna, che riporta il nome della coda risolto a runtime.
	 */
	private static void verificaTransazione(String idTransazione, String queue) throws TestSuiteException {
		DatabaseComponent db = null;
		DatabaseMsgDiagnosticiComponent dbDiag = null;
		try {
			db = DatabaseProperties.getDatabaseComponentErogatore();
			dbDiag = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
			VerificatoreTransazioni verificatore = db.getVerificatoreTransazioni();

			String diagConsegna = "consegnato al servizio applicativo ["+SERVIZIO_APPLICATIVO+"] mediante connettore [jms] (location: "+queue+
					") con codice di trasporto: "+CODICE_HTTP_JMS_REST;

			// la transazione e i diagnostici vengono registrati al termine della gestione della richiesta
			List<String[]> diagnostici = null;
			long scadenza = System.currentTimeMillis() + DB_TIMEOUT_MS;
			boolean registrata = false;
			while(!registrata && System.currentTimeMillis() < scadenza) {
				diagnostici = dbDiag.getDiagnosticiByIdTransazione(idTransazione);
				registrata = verificatore.isTraced(idTransazione) && containsDiagnostico(diagnostici, diagConsegna);
				if(!registrata) {
					org.openspcoop2.utils.Utilities.sleep(200);
				}
			}
			Assert.assertTrue(verificatore.isTraced(idTransazione), "Transazione '"+idTransazione+"' non registrata");
			Assert.assertTrue(verificatore.isTracedEsito(idTransazione, ESITO_OK), "Esito della transazione '"+idTransazione+"' diverso da "+ESITO_OK);
			Assert.assertTrue(containsDiagnostico(diagnostici, diagConsegna), "Diagnostico di consegna non presente: '"+diagConsegna+"'; diagnostici: "+toString(diagnostici));
			for (String[] d : diagnostici) {
				Assert.assertTrue(Integer.parseInt(d[1]) > SEVERITA_ERRORE, "Diagnostico di errore non atteso: "+d[0]+" "+d[2]);
			}
		}finally {
			if(db!=null) {
				db.close();
			}
			if(dbDiag!=null) {
				dbDiag.close();
			}
		}
	}
	private static boolean containsDiagnostico(List<String[]> diagnostici, String messaggio) {
		if(diagnostici!=null) {
			for (String[] d : diagnostici) {
				if(d[2]!=null && d[2].contains(messaggio)) {
					return true;
				}
			}
		}
		return false;
	}
	private static String toString(List<String[]> diagnostici) {
		StringBuilder sb = new StringBuilder();
		if(diagnostici!=null) {
			for (String[] d : diagnostici) {
				sb.append("\n").append(d[0]).append(" [").append(d[1]).append("] ").append(d[2]);
			}
		}
		return sb.toString();
	}

	private void test(String coda, String altraCoda) throws TestSuiteException {
		String queue = QUEUE_PREFIX+coda;
		String altraQueue = QUEUE_PREFIX+altraCoda;
		svuotaCoda(queue);
		svuotaCoda(altraQueue);

		String idUnivoco = UUID.randomUUID().toString();
		String richiesta = "{\"coda\":\""+coda+"\",\"id\":\""+idUnivoco+"\"}";
		HttpResponse response = invoke(richiesta);
		Assert.assertEquals(response.getResultHTTPOperation(), CODICE_HTTP_JMS_REST, "Codice HTTP della risposta: "+
				(response.getContent()!=null ? new String(response.getContent(), StandardCharsets.UTF_8) : null));
		String idTransazione = response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE);
		Assert.assertNotNull(idTransazione, "Header '"+HEADER_ID_TRANSAZIONE+"' non presente nella risposta");

		// Il messaggio viene pubblicato, senza modifiche, sulla coda indicata nel payload
		Message msg = readMessaggioPubblicato(queue, idUnivoco, RECEIVE_TIMEOUT_MS);
		Assert.assertNotNull(msg, "Messaggio con identificativo '"+idUnivoco+"' non pubblicato su '"+queue+"'");
		String contenuto = null;
		try {
			contenuto = new String(JMSTestUtilities.readContent(msg, true), StandardCharsets.UTF_8);
		}catch(JMSException e) {
			throw new TestSuiteException("Lettura del contenuto del messaggio JMS non riuscita", e);
		}
		Assert.assertEquals(contenuto, richiesta, "Il messaggio pubblicato non corrisponde al contenuto della richiesta");

		// e non sull'altra coda
		Message msgAltraCoda = readMessaggioPubblicato(altraQueue, idUnivoco, RECEIVE_TIMEOUT_ALTRA_CODA_MS);
		Assert.assertNull(msgAltraCoda, "Messaggio con identificativo '"+idUnivoco+"' pubblicato anche su '"+altraQueue+"'");

		verificaTransazione(idTransazione, queue);
	}






	/***
	 * Payload con coda 'A': pubblicazione su queue/openspcoop2TestQueueA
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE,RESTConnettoreJMSCodaDinamica.ID_GRUPPO,RESTConnettoreJMSCodaDinamica.ID_GRUPPO+".CODA_A"})
	public void codaA() throws TestSuiteException {
		test(CODA_A, CODA_B);
	}

	/***
	 * Payload con coda 'B': pubblicazione su queue/openspcoop2TestQueueB
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE,RESTConnettoreJMSCodaDinamica.ID_GRUPPO,RESTConnettoreJMSCodaDinamica.ID_GRUPPO+".CODA_B"})
	public void codaB() throws TestSuiteException {
		test(CODA_B, CODA_A);
	}

}
