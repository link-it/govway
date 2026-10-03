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
package org.openspcoop2.protocol.spcoop.testsuite.units.connettori;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Test sul connettore JMS con nome della coda dinamico.
 *
 * La trasformazione della richiesta, di tipo 'Alimentazione Contesto (Freemarker Template)', legge tramite xpath
 * il nome della coda presente nel payload e lo salva nel contesto; il connettore JMS utilizza come nome della coda
 * 'queue/openspcoop2TestQueue${context:jmsCoda}': payload differenti vengono quindi pubblicati su code differenti.
 *
 * Su WildFly le code 'openspcoop2TestQueueA' e 'openspcoop2TestQueueB' devono essere definite nel broker interno
 * (deploy/code_jms/wildfly_ActiveMQArtemis/testsuite-destinations-activemq-jms.xml); su tomcat, o in modalità jenkins
 * (opzione '-Djenkins=true'), viene utilizzato il broker embedded della testsuite (EmbeddedJMSBroker) e il binding JNDI
 * della coda è definito tramite una proprietà del connettore il cui nome e valore vengono risolti a runtime.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreJMSCodaDinamica {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "ConnettoreJMSCodaDinamica";

	private static final String SERVIZIO_APPLICATIVO = "PubbTestQueueDinamicaAsText";
	private static final String QUEUE_PREFIX = "queue/openspcoop2TestQueue";
	private static final String CODA_A = "A";
	private static final String CODA_B = "B";
	private static final String NAMESPACE_RICHIESTA = "http://www.govway.org/testsuite/jms";

	private static final long RECEIVE_TIMEOUT_MS = 10000;
	/** Attesa utilizzata per verificare che il messaggio non sia stato pubblicato sull'altra coda */
	private static final long RECEIVE_TIMEOUT_ALTRA_CODA_MS = 2000;
	private static final long DB_TIMEOUT_MS = 10000;

	private static final int ESITO_OK = 0;
	/** Severita' massima dei diagnostici di errore (fatal, errorProtocol, errorIntegration) */
	private static final int SEVERITA_ERRORE = 2;
	private static final String CODICE_HTTP_OK = "200";
	private static final String PDD_RUOLO_APPLICATIVA = "applicativa";



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
			this.jms.svuotaCoda(queue,
					getJMSPropertyName(org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance().getIdMessaggioTrasporto()));
		}catch(UtilsException | JMSException e) {
			throw new TestSuiteException("Svuotamento della coda '"+queue+"' non riuscito", e);
		}
	}

	private static HttpResponse invoke(String coda, String idUnivoco) throws TestSuiteException {
		String richiesta = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>"+
				"<ns1:pubblicazione xmlns:ns1=\""+NAMESPACE_RICHIESTA+"\"><coda>"+coda+"</coda><id>"+idUnivoco+"</id></ns1:pubblicazione>"+
				"</soapenv:Body></soapenv:Envelope>";
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\""+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_CODA_DINAMICA+"\"");
		request.setContent(richiesta.getBytes(StandardCharsets.UTF_8));
		request.setReadTimeout(org.openspcoop2.testsuite.core.CostantiTestSuite.READ_TIMEOUT);
		String url = Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_CODA_DINAMICA;
		request.setUrl(url);
		Reporter.log("URL: "+url+" (coda: "+coda+", id: "+idUnivoco+")");
		try {
			HttpResponse response = HttpUtilities.httpInvoke(request);
			Reporter.log("Ricevuto codice HTTP '"+response.getResultHTTPOperation()+"'");
			return response;
		}catch(UtilsException e) {
			throw new TestSuiteException("Invocazione della porta delegata '"+CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_CODA_DINAMICA+"' non riuscita", e);
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
				System.out.println("WARNING: scartato messaggio non atteso letto da '"+queue+"'");
			}
			return null;
		}catch(UtilsException | JMSException e) {
			throw new TestSuiteException("Lettura del messaggio con identificativo '"+idUnivoco+"' dalla coda '"+queue+"' non riuscita", e);
		}
	}

	/**
	 * La trasformazione di tipo 'Alimentazione Contesto' non modifica il messaggio: il SOAP Body pubblicato deve contenere l'elemento della richiesta originale.
	 */
	private static void verificaContenutoNonModificato(Message msg, String coda, String idUnivoco) throws TestSuiteException {
		try {
			byte[] contenuto = JMSTestUtilities.readContent(msg, true);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			Document doc = dbf.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(contenuto));
			NodeList body = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
			Assert.assertEquals(body.getLength(), 1, "SOAP Body non presente nel messaggio pubblicato: "+new String(contenuto, StandardCharsets.UTF_8));
			NodeList richiesta = ((Element)body.item(0)).getElementsByTagNameNS(NAMESPACE_RICHIESTA, "pubblicazione");
			Assert.assertEquals(richiesta.getLength(), 1, "Elemento della richiesta non presente nel SOAP Body pubblicato: "+new String(contenuto, StandardCharsets.UTF_8));
			Element pubblicazione = (Element) richiesta.item(0);
			Assert.assertEquals(pubblicazione.getElementsByTagName("coda").item(0).getTextContent(), coda, "Elemento 'coda' del messaggio pubblicato");
			Assert.assertEquals(pubblicazione.getElementsByTagName("id").item(0).getTextContent(), idUnivoco, "Elemento 'id' del messaggio pubblicato");
		}catch(TestSuiteException e) {
			throw e;
		}catch(Exception e) {
			throw new TestSuiteException("Verifica del contenuto del messaggio pubblicato non riuscita: "+e.getMessage(), e);
		}
	}

	/**
	 * Verifica su database l'esito della transazione di erogazione e il diagnostico di consegna, che riporta il nome della coda risolto a runtime.
	 */
	private static void verificaTransazione(String idEGov, String queue) throws TestSuiteException {
		DatabaseComponent dbErogatore = null;
		DatabaseMsgDiagnosticiComponent dbDiagErogatore = null;
		try {
			dbErogatore = DatabaseProperties.getDatabaseComponentErogatore();
			dbDiagErogatore = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
			VerificatoreTransazioni verificatoreErogatore = dbErogatore.getVerificatoreTransazioni();

			String diagConsegna = "consegnato al servizio applicativo ["+SERVIZIO_APPLICATIVO+"] mediante connettore [jms] (location: "+queue+") con codice di trasporto: "+CODICE_HTTP_OK;

			// la transazione e i diagnostici vengono registrati al termine della gestione della richiesta
			long scadenza = System.currentTimeMillis() + DB_TIMEOUT_MS;
			boolean registrata = false;
			while(!registrata && System.currentTimeMillis() < scadenza) {
				registrata = verificatoreErogatore.getValuesTracedByIdMessaggio(idEGov, PDD_RUOLO_APPLICATIVA, "esito")!=null &&
						dbDiagErogatore.isTracedMessaggioWithLike(idEGov, diagConsegna);
				if(!registrata) {
					org.openspcoop2.utils.Utilities.sleep(200);
				}
			}

			String[] esito = verificatoreErogatore.getValuesTracedByIdMessaggio(idEGov, PDD_RUOLO_APPLICATIVA, "esito");
			Assert.assertNotNull(esito, "Transazione di erogazione del messaggio '"+idEGov+"' non registrata");
			Assert.assertEquals(esito[0], ESITO_OK+"", "Esito della transazione di erogazione del messaggio '"+idEGov+"'");
			Assert.assertTrue(dbDiagErogatore.isTracedMessaggioWithLike(idEGov, diagConsegna), "Diagnostico di consegna non presente: '"+diagConsegna+"'; diagnostici: "+
					dbDiagErogatore.getMessaggiDiagnostici(idEGov));
			Assert.assertEquals(dbDiagErogatore.countSeveritaLessEquals(idEGov, SEVERITA_ERRORE), 0, "Diagnostici di errore non attesi: "+dbDiagErogatore.getMessaggiDiagnostici(idEGov));
		}catch(TestSuiteException e) {
			throw e;
		}catch(Exception e) {
			throw new TestSuiteException("Verifica della transazione del messaggio '"+idEGov+"' non riuscita", e);
		}finally {
			if(dbErogatore!=null) {
				dbErogatore.close();
			}
			if(dbDiagErogatore!=null) {
				dbDiagErogatore.close();
			}
		}
	}

	private void test(String coda, String altraCoda) throws TestSuiteException {
		String queue = QUEUE_PREFIX+coda;
		String altraQueue = QUEUE_PREFIX+altraCoda;
		svuotaCoda(queue);
		svuotaCoda(altraQueue);

		String idUnivoco = UUID.randomUUID().toString();
		HttpResponse response = invoke(coda, idUnivoco);
		Assert.assertEquals(response.getResultHTTPOperation(), 200, "Codice HTTP della risposta: "+
				(response.getContent()!=null ? new String(response.getContent(), StandardCharsets.UTF_8) : null));

		// Il messaggio viene pubblicato sulla coda indicata nel payload
		Message msg = readMessaggioPubblicato(queue, idUnivoco, RECEIVE_TIMEOUT_MS);
		Assert.assertNotNull(msg, "Messaggio con identificativo '"+idUnivoco+"' non pubblicato su '"+queue+"'");

		// la trasformazione (Alimentazione Contesto) non modifica il messaggio pubblicato
		verificaContenutoNonModificato(msg, coda, idUnivoco);

		// e non sull'altra coda
		Message msgAltraCoda = readMessaggioPubblicato(altraQueue, idUnivoco, RECEIVE_TIMEOUT_ALTRA_CODA_MS);
		Assert.assertNull(msgAltraCoda, "Messaggio con identificativo '"+idUnivoco+"' pubblicato anche su '"+altraQueue+"'");

		String idEGov = null;
		try {
			idEGov = msg.getStringProperty(getJMSPropertyName(org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance().getIdMessaggioTrasporto()));
		}catch(JMSException e) {
			throw new TestSuiteException("Lettura dell'identificativo del messaggio non riuscita", e);
		}
		Assert.assertNotNull(idEGov, "Identificativo del messaggio non presente tra le proprietà del messaggio JMS");
		verificaTransazione(idEGov, queue);
	}






	/***
	 * Payload con coda 'A': pubblicazione su queue/openspcoop2TestQueueA
	 */
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSCodaDinamica.ID_GRUPPO,ConnettoreJMSCodaDinamica.ID_GRUPPO+".CODA_A"})
	public void codaA() throws TestSuiteException {
		test(CODA_A, CODA_B);
	}

	/***
	 * Payload con coda 'B': pubblicazione su queue/openspcoop2TestQueueB
	 */
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSCodaDinamica.ID_GRUPPO,ConnettoreJMSCodaDinamica.ID_GRUPPO+".CODA_B"})
	public void codaB() throws TestSuiteException {
		test(CODA_B, CODA_A);
	}

}
