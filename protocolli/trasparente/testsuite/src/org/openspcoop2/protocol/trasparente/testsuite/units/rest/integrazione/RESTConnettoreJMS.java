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
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.testsuite.core.JMSTestUtilities;
import org.openspcoop2.testsuite.core.JMSTestUtilities.JMSConsumer;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.db.VerificatoreTransazioni;
import org.openspcoop2.testsuite.units.CooperazioneBase;
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
 * Test sulla risposta prodotta dal connettore JMS per le API REST.
 *
 * Il connettore JMS pubblica il messaggio e non riceve alcuna risposta dal destinatario:
 * al client viene restituita una risposta senza contenuto con codice HTTP 204,
 * personalizzabile tramite la proprietà 'connettori.jms.response.rest.returnCode'
 * ed eventualmente modificabile tramite una trasformazione della risposta.
 *
 * I test richiedono il broker JMS interno all'application server e vengono quindi eseguiti solamente su WildFly
 * e non in ambiente Jenkins.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RESTConnettoreJMS {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "RESTConnettoreJMS";

	private static final String PORTA_APPLICATIVA_PREFIX = "APIMinisteroErogatore/";

	private static final String TIPO_SOGGETTO = "gw";
	private static final String SOGGETTO_EROGATORE = "MinisteroErogatore";
	private static final String TIPO_SERVIZIO = "gw";
	private static final String SERVIZIO = "API";
	private static final String VERSIONE_SERVIZIO = "1";

	private static final String HEADER_ID_TRANSAZIONE = "GovWay-Transaction-ID";

	/** Contenuto prodotto dalla trasformazione della risposta configurata sulla porta applicativa */
	private static final String TRASFORMAZIONE_ESITO = "Messaggio pubblicato";

	private static final long RECEIVE_TIMEOUT_MS = 10000;
	private static final long DB_TIMEOUT_MS = 10000;
	
	private static final int ESITO_OK = 0;
	/** Severita' massima dei diagnostici di errore (fatal, errorProtocol, errorIntegration) */
	private static final int SEVERITA_ERRORE = 2;
	
	private static final String QUEUE = "queue/openspcoop2TestQueue";
	private static final String TOPIC = "topic/openspcoop2TestTopic";



	private Date dataAvvioGruppoTest = null;
	private boolean doTestJMS = true;
	private JMSTestUtilities jms = null;
	private JMSConsumer topicConsumer = null;

	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws TestSuiteException {
		this.dataAvvioGruppoTest = DateManager.getDate();

		try{
			String versionJbossas = Utilities.readApplicationServerVersion();
			if(versionJbossas.startsWith("tomcat")){
				System.out.println("WARNING: Verifiche risposta connettore JMS disabilitate per Tomcat");
				this.doTestJMS = false;
			}
		}catch(Exception e){
			System.err.println("Identificazione A.S. non riuscita: "+e.getMessage());
			e.printStackTrace(System.out);
		}
		if(this.doTestJMS && CooperazioneBase.isJenkins()) {
			System.out.println("WARNING: Verifiche risposta connettore JMS disabilitate in ambiente jenkins");
			this.doTestJMS = false;
		}

		if(!this.doTestJMS){
			return;
		}

		TestSuiteProperties prop = TestSuiteProperties.getInstance();
		this.jms = new JMSTestUtilities(prop.getJMS_JNDIContext(), prop.getJMSConnectionFactory(), prop.getJMSUsername(), prop.getJMSPassword());

		// La sottoscrizione al topic deve precedere la pubblicazione dei messaggi
		try {
			this.topicConsumer = this.jms.createConsumer(prop.getJMSTopic());
		}catch(UtilsException | JMSException e) {
			throw new TestSuiteException("Sottoscrizione al topic '"+prop.getJMSTopic()+"' non riuscita", e);
		}
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

		if(this.topicConsumer!=null) {
			this.topicConsumer.close();
		}
	}





	// UTILITIES

	private static String getJMSPropertyName(String headerTrasporto) {
		return headerTrasporto.replace("X-", "").replace("-", "");
	}

	private void svuotaCoda() throws TestSuiteException {
		String queue = TestSuiteProperties.getInstance().getJMSQueue();
		try {
			this.jms.svuotaCoda(queue, getJMSPropertyName(HEADER_ID_TRANSAZIONE));
		}catch(UtilsException | JMSException e) {
			throw new TestSuiteException("Svuotamento della coda '"+queue+"' non riuscito", e);
		}
	}

	private static HttpResponse invoke(String portaApplicativa, String contenuto) throws TestSuiteException {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(contenuto.getBytes(StandardCharsets.UTF_8));
		request.setReadTimeout(org.openspcoop2.testsuite.core.CostantiTestSuite.READ_TIMEOUT);
		String url = Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore()+PORTA_APPLICATIVA_PREFIX+portaApplicativa;
		request.setUrl(url);
		Reporter.log("URL: "+url);
		try {
			HttpResponse response = HttpUtilities.httpInvoke(request);
			Reporter.log("Ricevuto codice HTTP '"+response.getResultHTTPOperation()+"' (id transazione: "+response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE)+")");
			return response;
		}catch(UtilsException e) {
			throw new TestSuiteException("Invocazione della porta '"+portaApplicativa+"' non riuscita", e);
		}
	}

	/**
	 * Legge il messaggio pubblicato dal test, individuato tramite l'identificativo univoco presente nel contenuto,
	 * scartando eventuali messaggi pubblicati da altri test.
	 */
	private Message readMessaggioPubblicato(boolean queue, boolean textMessage, String idUnivoco) throws TestSuiteException {
		JMSConsumer queueConsumer = null;
		try {
			JMSConsumer consumer = null;
			if(queue) {
				queueConsumer = this.jms.createConsumer(TestSuiteProperties.getInstance().getJMSQueue());
				consumer = queueConsumer;
			}
			else {
				consumer = this.topicConsumer;
			}
			long scadenza = System.currentTimeMillis() + RECEIVE_TIMEOUT_MS;
			while(System.currentTimeMillis() < scadenza) {
				Message msg = consumer.receive(Math.max(1, scadenza - System.currentTimeMillis()));
				if(msg==null) {
					break;
				}
				String contenuto = null;
				try {
					contenuto = new String(JMSTestUtilities.readContent(msg, textMessage), StandardCharsets.UTF_8);
				}catch(TestSuiteException e) {
					// tipo di messaggio diverso da quello atteso: messaggio pubblicato da un altro test
				}
				if(contenuto!=null && contenuto.contains(idUnivoco)) {
					return msg;
				}
				System.out.println("WARNING: scartato messaggio non atteso (id transazione: "+msg.getStringProperty(getJMSPropertyName(HEADER_ID_TRANSAZIONE))+") letto da '"+consumer.getDestination()+"'");
			}
			throw new TestSuiteException("Messaggio con identificativo '"+idUnivoco+"' non pubblicato su '"+consumer.getDestination()+"'");
		}catch(UtilsException | JMSException e) {
			throw new TestSuiteException("Lettura del messaggio con identificativo '"+idUnivoco+"' non riuscita", e);
		}finally {
			if(queueConsumer!=null) {
				queueConsumer.close();
			}
		}
	}

	/**
	 * Verifica su database l'esito della transazione, i codici di risposta ricevuto dal connettore (ingresso) e restituito al client (uscita)
	 * e i diagnostici di consegna sulla coda/topic e di consegna della risposta al client.
	 */
	private static void verificaTransazione(String idTransazione, String servizioApplicativo, boolean queue, 
			int codiceConnettoreAtteso, int codiceClientAtteso) throws TestSuiteException {
		DatabaseComponent db = null;
		DatabaseMsgDiagnosticiComponent dbDiag = null;
		try {
			db = DatabaseProperties.getDatabaseComponentErogatore();
			dbDiag = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
			VerificatoreTransazioni verificatore = db.getVerificatoreTransazioni();
			
			String diagConsegna = "consegnato al servizio applicativo ["+servizioApplicativo+"] mediante connettore [jms] (location: "+(queue ? QUEUE : TOPIC)+
					") con codice di trasporto: "+codiceConnettoreAtteso;
			String diagRisposta = "Risposta consegnata al mittente con codice di trasporto: "+codiceClientAtteso;
			
			// la transazione e i diagnostici vengono registrati al termine della gestione della richiesta
			List<String[]> diagnostici = null;
			long scadenza = System.currentTimeMillis() + DB_TIMEOUT_MS;
			boolean registrata = false;
			while(!registrata && System.currentTimeMillis() < scadenza) {
				diagnostici = dbDiag.getDiagnosticiByIdTransazione(idTransazione);
				registrata = verificatore.isTraced(idTransazione) && containsDiagnostico(diagnostici, diagRisposta);
				if(!registrata) {
					org.openspcoop2.utils.Utilities.sleep(200);
				}
			}
			Assert.assertTrue(verificatore.isTraced(idTransazione), "Transazione '"+idTransazione+"' non registrata");
			
			Assert.assertTrue(verificatore.isTracedEsito(idTransazione, ESITO_OK), "Esito della transazione '"+idTransazione+"' diverso da "+ESITO_OK);
			String[] codiceIngresso = verificatore.getValuesTraced(idTransazione, "codice_risposta_ingresso");
			Assert.assertEquals(codiceIngresso!=null ? codiceIngresso[0] : null, codiceConnettoreAtteso+"", "Codice di risposta ricevuto dal connettore (codice_risposta_ingresso)");
			String[] codiceUscita = verificatore.getValuesTraced(idTransazione, "codice_risposta_uscita");
			Assert.assertEquals(codiceUscita!=null ? codiceUscita[0] : null, codiceClientAtteso+"", "Codice di risposta restituito al client (codice_risposta_uscita)");
			
			Assert.assertTrue(containsDiagnostico(diagnostici, diagConsegna), "Diagnostico di consegna non presente: '"+diagConsegna+"'; diagnostici: "+toString(diagnostici));
			Assert.assertTrue(containsDiagnostico(diagnostici, diagRisposta), "Diagnostico di risposta al client non presente: '"+diagRisposta+"'; diagnostici: "+toString(diagnostici));
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
	
	private HttpResponse test(String portaApplicativa, String azione, boolean queue, boolean textMessage) throws TestSuiteException {
		if(queue) {
			svuotaCoda();
		}

		String idUnivoco = UUID.randomUUID().toString();
		String richiesta = "{\"test\":\"connettoreJMS\",\"id\":\""+idUnivoco+"\"}";
		HttpResponse response = invoke(portaApplicativa, richiesta);
		String idTransazione = response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE);
		Assert.assertNotNull(idTransazione, "Header '"+HEADER_ID_TRANSAZIONE+"' non presente nella risposta");

		// Messaggio pubblicato
		Message msg = readMessaggioPubblicato(queue, textMessage, idUnivoco);
		String contenuto = null;
		try {
			contenuto = new String(JMSTestUtilities.readContent(msg, textMessage), StandardCharsets.UTF_8);
		}catch(JMSException e) {
			throw new TestSuiteException("Lettura del contenuto del messaggio JMS non riuscita", e);
		}
		Assert.assertEquals(contenuto, richiesta, "Il messaggio pubblicato non corrisponde al contenuto della richiesta");

		// Informazioni di integrazione propagate come proprietà JMS
		org.openspcoop2.testsuite.core.TestSuiteProperties testsuiteProperties = org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance();
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getTipoDestinatarioTrasporto()), TIPO_SOGGETTO);
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getDestinatarioTrasporto()), SOGGETTO_EROGATORE);
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getTipoServizioTrasporto()), TIPO_SERVIZIO);
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getNomeServizioTrasporto()), SERVIZIO);
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getVersioneServizioTrasporto()), VERSIONE_SERVIZIO);
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getAzioneTrasporto()), azione);
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(HEADER_ID_TRANSAZIONE), idTransazione);

		return response;
	}

	private static void verificaRispostaVuota(HttpResponse response, int returnCodeAtteso) {
		Assert.assertEquals(response.getResultHTTPOperation(), returnCodeAtteso, "Codice HTTP della risposta");
		byte[] content = response.getContent();
		Assert.assertTrue(content==null || content.length==0, "Attesa una risposta senza contenuto, ricevuti "+(content!=null ? content.length : 0)+" bytes: "+
				(content!=null ? new String(content, StandardCharsets.UTF_8) : null));
	}






	/***
	 * TextMessage su coda: risposta senza contenuto con codice HTTP 204
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE,RESTConnettoreJMS.ID_GRUPPO,RESTConnettoreJMS.ID_GRUPPO+".TEXT_QUEUE"})
	public void textQueue() throws TestSuiteException {
		if(!this.doTestJMS){
			return;
		}
		HttpResponse response = test("RESTJMS_TextQueue", "jmsTextQueue", true, true);
		verificaRispostaVuota(response, 204);
		verificaTransazione(response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE), "RESTJMSQueueAsText", true, 204, 204);
	}

	/***
	 * BytesMessage su coda: risposta senza contenuto con codice HTTP 204
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE,RESTConnettoreJMS.ID_GRUPPO,RESTConnettoreJMS.ID_GRUPPO+".BYTES_QUEUE"})
	public void bytesQueue() throws TestSuiteException {
		if(!this.doTestJMS){
			return;
		}
		HttpResponse response = test("RESTJMS_BytesQueue", "jmsBytesQueue", true, false);
		verificaRispostaVuota(response, 204);
		verificaTransazione(response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE), "RESTJMSQueueAsBytes", true, 204, 204);
	}

	/***
	 * TextMessage su topic: risposta senza contenuto con codice HTTP 204
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE,RESTConnettoreJMS.ID_GRUPPO,RESTConnettoreJMS.ID_GRUPPO+".TEXT_TOPIC"})
	public void textTopic() throws TestSuiteException {
		if(!this.doTestJMS){
			return;
		}
		HttpResponse response = test("RESTJMS_TextTopic", "jmsTextTopic", false, true);
		verificaRispostaVuota(response, 204);
		verificaTransazione(response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE), "RESTJMSTopicAsText", false, 204, 204);
	}

	/***
	 * BytesMessage su topic: risposta senza contenuto con codice HTTP 204
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE,RESTConnettoreJMS.ID_GRUPPO,RESTConnettoreJMS.ID_GRUPPO+".BYTES_TOPIC"})
	public void bytesTopic() throws TestSuiteException {
		if(!this.doTestJMS){
			return;
		}
		HttpResponse response = test("RESTJMS_BytesTopic", "jmsBytesTopic", false, false);
		verificaRispostaVuota(response, 204);
		verificaTransazione(response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE), "RESTJMSTopicAsBytes", false, 204, 204);
	}

	/***
	 * Codice HTTP della risposta personalizzato tramite la proprietà 'connettori.jms.response.rest.returnCode'
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE,RESTConnettoreJMS.ID_GRUPPO,RESTConnettoreJMS.ID_GRUPPO+".RETURN_CODE"})
	public void returnCode() throws TestSuiteException {
		if(!this.doTestJMS){
			return;
		}
		HttpResponse response = test("RESTJMS_ReturnCode", "jmsReturnCode", true, true);
		verificaRispostaVuota(response, 202);
		verificaTransazione(response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE), "RESTJMSQueueAsText", true, 202, 202);
	}

	/***
	 * Risposta senza contenuto del connettore sostituita tramite una trasformazione della risposta con un esito json di avvenuta pubblicazione
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE,RESTConnettoreJMS.ID_GRUPPO,RESTConnettoreJMS.ID_GRUPPO+".TRASFORMAZIONE_RISPOSTA"})
	public void trasformazioneRisposta() throws TestSuiteException {
		if(!this.doTestJMS){
			return;
		}
		HttpResponse response = test("RESTJMS_TrasformazioneRisposta", "jmsTrasformazioneRisposta", true, true);
		Assert.assertEquals(response.getResultHTTPOperation(), 200, "Codice HTTP della risposta");
		Assert.assertNotNull(response.getContentType(), "Content-Type della risposta non presente");
		Assert.assertTrue(response.getContentType().startsWith(HttpConstants.CONTENT_TYPE_JSON), "Content-Type della risposta: "+response.getContentType());
		Assert.assertNotNull(response.getContent(), "Contenuto della risposta non presente");
		String contenuto = new String(response.getContent(), StandardCharsets.UTF_8);
		String atteso = "{\"esito\":\""+TRASFORMAZIONE_ESITO+"\",\"idTransazione\":\""+response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE)+"\"}";
		Assert.assertEquals(contenuto, atteso, "Contenuto della risposta prodotto dalla trasformazione");
		// il connettore genera la risposta vuota con il codice di default, sostituita dalla trasformazione
		verificaTransazione(response.getHeaderFirstValue(HEADER_ID_TRANSAZIONE), "RESTJMSQueueAsText", true, 204, 200);
	}

}
