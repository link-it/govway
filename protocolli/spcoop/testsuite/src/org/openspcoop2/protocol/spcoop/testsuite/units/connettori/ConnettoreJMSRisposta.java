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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteTransformer;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.testsuite.clients.ClientOneWay;
import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.JMSTestUtilities;
import org.openspcoop2.testsuite.core.JMSTestUtilities.JMSConsumer;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.db.VerificatoreTransazioni;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test sulla risposta prodotta dal connettore JMS per le azioni con profilo diverso da oneway.
 *
 * Il connettore JMS pubblica il messaggio e non riceve alcuna risposta dal destinatario:
 * per le azioni con profilo sincrono viene restituito al client un SOAP Envelope con Body vuoto,
 * o contenente l'elemento wrapper della risposta dell'operazione (proprietà 'connettori.jms.response.soap.operationWrapper'),
 * eventualmente modificabile tramite una trasformazione della risposta.
 *
 * I test richiedono il broker JMS interno all'application server e vengono quindi eseguiti solamente su WildFly
 * e non in ambiente Jenkins.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreJMSRisposta {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "ConnettoreJMSRisposta";

	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
				false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);
	private CooperazioneBase collaborazioneSPCoopBase =
			new CooperazioneBase(false,MessageType.SOAP_11,  this.info,
					org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(),
					DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());

	private static boolean addIDUnivoco = true;

	/** Elemento wrapper atteso nella risposta: il primo elemento del Body della richiesta (soap1K.xml) con il suffisso 'Response' */
	private static final String OPERATION_WRAPPER_NAMESPACE = "urn:xmethods-delayed-quotes";
	private static final String OPERATION_WRAPPER_LOCAL_NAME = "getQuoteResponse";
	private static final String OPERATION_WRAPPER_PREFIX = "ns1";

	/** Contenuto prodotto dalla trasformazione della risposta configurata sulla porta applicativa */
	private static final String TRASFORMAZIONE_ESITO = "Messaggio pubblicato";

	private static final long RECEIVE_TIMEOUT_MS = 10000;
	private static final long DB_TIMEOUT_MS = 10000;
	
	private static final int ESITO_OK = 0;
	/** Severita' massima dei diagnostici di errore (fatal, errorProtocol, errorIntegration) */
	private static final int SEVERITA_ERRORE = 2;
	private static final String CODICE_HTTP_OK = "200";
	private static final String PDD_RUOLO_DELEGATA = "delegata";
	private static final String PDD_RUOLO_APPLICATIVA = "applicativa";



	private Date dataAvvioGruppoTest = null;
	private boolean doTestJMS = true;
	private JMSTestUtilities jms = null;
	private JMSConsumer topicConsumer = null;

	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws TestSuiteException {
		this.dataAvvioGruppoTest = DateManager.getDate();

		TestSuiteTransformer.sequentialForced = true;

		try{
			String version_jbossas = Utilities.readApplicationServerVersion();
			if(version_jbossas.startsWith("tomcat")){
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
		return headerTrasporto.replace("X-", "").replaceAll("-", "");
	}

	private void svuotaCoda() throws TestSuiteException {
		String queue = TestSuiteProperties.getInstance().getJMSQueue();
		try {
			this.jms.svuotaCoda(queue,
					getJMSPropertyName(org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance().getIdMessaggioTrasporto()));
		}catch(UtilsException | JMSException e) {
			throw new TestSuiteException("Svuotamento della coda '"+queue+"' non riuscito", e);
		}
	}

	private ClientSincrono invocazioneSincrona(Repository repository,String portaDelegata) throws TestSuiteException, AxisFault {
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientSincrono client=new ClientSincrono(repository);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine(MessageType.SOAP_11);
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
			return client;
		}finally{
			if(dbComponentFruitore!=null) {
				dbComponentFruitore.close();
			}
			if(dbComponentErogatore!=null) {
				dbComponentErogatore.close();
			}
		}
	}

	private ClientOneWay invocazioneOneway(Repository repository,String portaDelegata) throws TestSuiteException, AxisFault {
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientOneWay client=new ClientOneWay(repository);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
			return client;
		}finally{
			if(dbComponentFruitore!=null) {
				dbComponentFruitore.close();
			}
			if(dbComponentErogatore!=null) {
				dbComponentErogatore.close();
			}
		}
	}

	/**
	 * Legge il messaggio pubblicato dal test, scartando eventuali messaggi con un identificativo diverso da quello atteso
	 * (es. pubblicati sul topic da altri test).
	 */
	private Message readMessaggioPubblicato(boolean queue, String idEGov) throws TestSuiteException {
		String idProperty = getJMSPropertyName(org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance().getIdMessaggioTrasporto());
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
				String id = msg.getStringProperty(idProperty);
				if(idEGov.equals(id)) {
					return msg;
				}
				System.out.println("WARNING: scartato messaggio non atteso (id: "+id+") letto da '"+consumer.getDestination()+"' (atteso: "+idEGov+")");
			}
			throw new TestSuiteException("Messaggio con id '"+idEGov+"' non pubblicato su '"+consumer.getDestination()+"'");
		}catch(UtilsException | JMSException e) {
			throw new TestSuiteException("Lettura del messaggio con id '"+idEGov+"' non riuscita", e);
		}finally {
			if(queueConsumer!=null) {
				queueConsumer.close();
			}
		}
	}

	private String verificaMessaggioPubblicato(boolean queue, boolean textMessage, boolean propagazioneEGov, String azione, String idEGov) throws TestSuiteException {

		Message msg = readMessaggioPubblicato(queue, idEGov);
		String contenuto = new String(readContent(msg, textMessage), StandardCharsets.UTF_8);

		// Header di trasporto
		org.openspcoop2.testsuite.core.TestSuiteProperties testsuiteProperties = org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance();
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getTipoMittenteTrasporto()), this.collaborazioneSPCoopBase.getMittente().getTipo());
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getMittenteTrasporto()), this.collaborazioneSPCoopBase.getMittente().getNome());
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getTipoDestinatarioTrasporto()), this.collaborazioneSPCoopBase.getDestinatario().getTipo());
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getDestinatarioTrasporto()), this.collaborazioneSPCoopBase.getDestinatario().getNome());
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getTipoServizioTrasporto()), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO);
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getNomeServizioTrasporto()), CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getVersioneServizioTrasporto()), "1");
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getAzioneTrasporto()), azione);
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(testsuiteProperties.getIdMessaggioTrasporto()), idEGov);
		String userAgent = null;
		try {
			userAgent = msg.getStringProperty(getJMSPropertyName(HttpConstants.USER_AGENT));
		}catch(JMSException e) {
			throw new TestSuiteException("Proprieta' di trasporto ["+getJMSPropertyName(HttpConstants.USER_AGENT)+"] non leggibile", e);
		}
		if(userAgent==null || !userAgent.contains(CostantiPdD.OPENSPCOOP2_PRODUCT)) {
			throw new TestSuiteException("Proprieta' di trasporto ["+getJMSPropertyName(HttpConstants.USER_AGENT)+"] presente nell'header di trasporto JMS ["+userAgent+"] non contiene "+CostantiPdD.OPENSPCOOP2_PRODUCT);
		}

		// Informazioni eGov propagate tramite le proprietà della porta applicativa
		if(propagazioneEGov) {
			String suffix = queue ? "QUEUE" : "TOPIC";
			JMSTestUtilities.checkProperty(msg, "tipoMitt"+suffix, this.collaborazioneSPCoopBase.getMittente().getTipo());
			JMSTestUtilities.checkProperty(msg, "mitt"+suffix, this.collaborazioneSPCoopBase.getMittente().getNome());
			JMSTestUtilities.checkProperty(msg, "tipoDestinatario"+suffix, this.collaborazioneSPCoopBase.getDestinatario().getTipo());
			JMSTestUtilities.checkProperty(msg, "destinatario"+suffix, this.collaborazioneSPCoopBase.getDestinatario().getNome());
			JMSTestUtilities.checkProperty(msg, "tipoServizio"+suffix, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO);
			JMSTestUtilities.checkProperty(msg, "servizio"+suffix, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
			JMSTestUtilities.checkProperty(msg, "azione"+suffix, azione);
			JMSTestUtilities.checkProperty(msg, "id"+suffix, idEGov);
		}

		return contenuto;
	}

	private static byte[] readContent(Message msg, boolean textMessage) throws TestSuiteException {
		try {
			return JMSTestUtilities.readContent(msg, textMessage);
		}catch(JMSException e) {
			throw new TestSuiteException("Lettura del contenuto del messaggio JMS non riuscita", e);
		}
	}

	private static List<Element> getBodyChildren(org.apache.axis.Message message) throws TestSuiteException {
		Assert.assertNotNull(message, "Messaggio di risposta non presente");
		javax.xml.soap.SOAPBody body = null;
		try {
			body = message.getSOAPBody();
		}catch(SOAPException e) {
			throw new TestSuiteException("Lettura del SOAP Body della risposta non riuscita", e);
		}
		Assert.assertNotNull(body, "SOAP Body della risposta non presente");
		Assert.assertFalse(body.hasFault(), "SOAP Fault non atteso nella risposta");
		List<Element> l = new ArrayList<>();
		NodeList nl = body.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n instanceof Element) {
				l.add((Element)n);
			}
		}
		return l;
	}

	private static void verificaRispostaBodyVuoto(org.apache.axis.Message message) throws TestSuiteException {
		List<Element> l = getBodyChildren(message);
		Assert.assertTrue(l.isEmpty(), "Atteso SOAP Body vuoto nella risposta, trovati "+l.size()+" elementi (primo: "+(l.isEmpty() ? null : l.get(0).getLocalName())+")");
	}

	private static Element verificaRispostaOperationWrapper(org.apache.axis.Message message) throws TestSuiteException {
		List<Element> l = getBodyChildren(message);
		Assert.assertEquals(l.size(), 1, "Atteso un solo elemento nel SOAP Body della risposta");
		Element e = l.get(0);
		Assert.assertEquals(e.getLocalName(), OPERATION_WRAPPER_LOCAL_NAME);
		Assert.assertEquals(e.getNamespaceURI(), OPERATION_WRAPPER_NAMESPACE);
		return e;
	}

	/** Contenuto testuale dell'elemento: Node.getTextContent() (DOM Level 3) non è supportato dagli elementi Axis 1.4 */
	private static String getText(Node n) {
		StringBuilder sb = new StringBuilder();
		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node child = nl.item(i);
			if(child instanceof org.w3c.dom.Text) {
				sb.append(((org.w3c.dom.Text)child).getData());
			}
			else if(child instanceof Element) {
				sb.append(getText(child));
			}
		}
		return sb.toString();
	}
	
	private static int countElementChildren(Element e) {
		int count = 0;
		NodeList nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i) instanceof Element) {
				count++;
			}
		}
		return count;
	}

	private DatabaseComponent[] dataProviderComponents() {
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return new DatabaseComponent[] {DatabaseProperties.getDatabaseComponentFruitore(), DatabaseProperties.getDatabaseComponentErogatore()};
	}

	private void verificaTracciamentoSincrono(DatabaseComponent data,String id,String azione) throws TestSuiteException {
		if(!this.doTestJMS){
			data.close();
			return;
		}
		try{
			this.collaborazioneSPCoopBase.testSincrono(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					azione, false,null);
		}finally{
			data.close();
		}
	}

	private String testSincrono(Repository repository, String portaDelegata, String azione,
			boolean queue, boolean textMessage, boolean propagazioneEGov) throws TestSuiteException, AxisFault {
		if(queue) {
			svuotaCoda();
		}
		ClientSincrono client = invocazioneSincrona(repository, portaDelegata);
		String idEGov = client.getIdMessaggio();
		Assert.assertNotNull(idEGov, "ID messaggio non presente nella risposta");

		String contenuto = verificaMessaggioPubblicato(queue, textMessage, propagazioneEGov, azione, idEGov);
		Assert.assertTrue(contenuto.contains(client.getLastIDUnivocoGenerato()), "Il messaggio pubblicato non contiene l'identificativo univoco della richiesta");

		if(CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_OPERATION_WRAPPER.equals(azione)) {
			Element e = verificaRispostaOperationWrapper(client.getResponseMessage());
			Assert.assertEquals(countElementChildren(e), 0, "Atteso elemento wrapper vuoto");
			Assert.assertEquals(e.getPrefix(), OPERATION_WRAPPER_PREFIX);
		}
		else if(CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_TRASFORMAZIONE_RISPOSTA.equals(azione)) {
			Element e = verificaRispostaOperationWrapper(client.getResponseMessage());
			String testo = getText(e);
			Assert.assertTrue(testo.contains(TRASFORMAZIONE_ESITO),
					"La risposta non contiene l'esito prodotto dalla trasformazione: "+testo);
		}
		else {
			verificaRispostaBodyVuoto(client.getResponseMessage());
		}
		
		boolean sbustamento = CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_SBUSTAMENTO_SOAP_QUEUE.equals(azione) || 
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_SBUSTAMENTO_SOAP_TOPIC.equals(azione);
		verificaTransazioni(idEGov, getServizioApplicativo(queue, textMessage, sbustamento), queue, true);
		
		return contenuto;
	}
	
	private static String getServizioApplicativo(boolean queue, boolean textMessage, boolean sbustamento) {
		return "PubbTest"+(queue ? "Queue" : "Topic")+"As"+(textMessage ? "Text" : "Bytes")+(sbustamento ? "Sbustato" : "");
	}
	
	/**
	 * Verifica su database l'esito delle transazioni di fruizione ed erogazione, i codici di risposta ricevuto dal connettore (ingresso)
	 * e restituito al client (uscita) e i diagnostici di consegna sulla coda/topic e di consegna della risposta al client.
	 */
	private static void verificaTransazioni(String idEGov, String servizioApplicativo, boolean queue, boolean sincrono) throws TestSuiteException {
		DatabaseComponent dbFruitore = null;
		DatabaseComponent dbErogatore = null;
		DatabaseMsgDiagnosticiComponent dbDiagFruitore = null;
		DatabaseMsgDiagnosticiComponent dbDiagErogatore = null;
		try {
			dbFruitore = DatabaseProperties.getDatabaseComponentFruitore();
			dbErogatore = DatabaseProperties.getDatabaseComponentErogatore();
			dbDiagFruitore = DatabaseProperties.getDatabaseComponentDiagnosticaFruitore();
			dbDiagErogatore = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
			VerificatoreTransazioni verificatoreFruitore = dbFruitore.getVerificatoreTransazioni();
			VerificatoreTransazioni verificatoreErogatore = dbErogatore.getVerificatoreTransazioni();
			
			TestSuiteProperties prop = TestSuiteProperties.getInstance();
			String diagConsegna = "consegnato al servizio applicativo ["+servizioApplicativo+"] mediante connettore [jms] (location: "+
					(queue ? prop.getJMSQueue() : prop.getJMSTopic())+") con codice di trasporto: "+CODICE_HTTP_OK;
			String diagRisposta = "Risposta applicativa consegnata al servizio applicativo con codice di trasporto: "+CODICE_HTTP_OK;
			
			// le transazioni e i diagnostici vengono registrati al termine della gestione della richiesta
			long scadenza = System.currentTimeMillis() + DB_TIMEOUT_MS;
			boolean registrate = false;
			while(!registrate && System.currentTimeMillis() < scadenza) {
				registrate = verificatoreFruitore.getValuesTracedByIdMessaggio(idEGov, PDD_RUOLO_DELEGATA, "esito")!=null &&
						verificatoreErogatore.getValuesTracedByIdMessaggio(idEGov, PDD_RUOLO_APPLICATIVA, "esito")!=null &&
						dbDiagErogatore.isTracedMessaggioWithLike(idEGov, diagConsegna) &&
						(!sincrono || dbDiagFruitore.isTracedMessaggioWithLike(idEGov, diagRisposta));
				if(!registrate) {
					org.openspcoop2.utils.Utilities.sleep(200);
				}
			}
			
			// Erogazione: consegna sulla coda/topic
			verificaTransazione(verificatoreErogatore, idEGov, PDD_RUOLO_APPLICATIVA, sincrono ? CODICE_HTTP_OK : null);
			Assert.assertTrue(dbDiagErogatore.isTracedMessaggioWithLike(idEGov, diagConsegna), "Diagnostico di consegna non presente: '"+diagConsegna+"'; diagnostici: "+
					dbDiagErogatore.getMessaggiDiagnostici(idEGov));
			
			// Fruizione: consegna della risposta al client
			verificaTransazione(verificatoreFruitore, idEGov, PDD_RUOLO_DELEGATA, null);
			if(sincrono) {
				String[] codiceUscita = verificatoreFruitore.getValuesTracedByIdMessaggio(idEGov, PDD_RUOLO_DELEGATA, "codice_risposta_uscita");
				Assert.assertEquals(codiceUscita[0], CODICE_HTTP_OK, "Codice di risposta restituito al client (codice_risposta_uscita, porta delegata)");
				Assert.assertTrue(dbDiagFruitore.isTracedMessaggioWithLike(idEGov, diagRisposta), "Diagnostico di risposta al client non presente: '"+diagRisposta+"'; diagnostici: "+
						dbDiagFruitore.getMessaggiDiagnostici(idEGov));
			}
			
			// Nessun diagnostico di errore
			Assert.assertEquals(dbDiagFruitore.countSeveritaLessEquals(idEGov, SEVERITA_ERRORE), 0, "Diagnostici di errore non attesi: "+dbDiagFruitore.getMessaggiDiagnostici(idEGov));
			Assert.assertEquals(dbDiagErogatore.countSeveritaLessEquals(idEGov, SEVERITA_ERRORE), 0, "Diagnostici di errore non attesi: "+dbDiagErogatore.getMessaggiDiagnostici(idEGov));
		}catch(TestSuiteException e) {
			throw e;
		}catch(Exception e) {
			throw new TestSuiteException("Verifica delle transazioni del messaggio '"+idEGov+"' non riuscita", e);
		}finally {
			if(dbFruitore!=null) {
				dbFruitore.close();
			}
			if(dbErogatore!=null) {
				dbErogatore.close();
			}
			if(dbDiagFruitore!=null) {
				dbDiagFruitore.close();
			}
			if(dbDiagErogatore!=null) {
				dbDiagErogatore.close();
			}
		}
	}
	private static void verificaTransazione(VerificatoreTransazioni verificatore, String idEGov, String pddRuolo, String codiceIngressoAtteso) throws TestSuiteException {
		String[] esito = verificatore.getValuesTracedByIdMessaggio(idEGov, pddRuolo, "esito");
		Assert.assertNotNull(esito, "Transazione ("+pddRuolo+") del messaggio '"+idEGov+"' non registrata");
		Assert.assertEquals(esito.length, 1, "Attesa una sola transazione ("+pddRuolo+") per il messaggio '"+idEGov+"'");
		Assert.assertEquals(esito[0], ESITO_OK+"", "Esito della transazione ("+pddRuolo+") del messaggio '"+idEGov+"'");
		if(codiceIngressoAtteso!=null) {
			String[] codiceIngresso = verificatore.getValuesTracedByIdMessaggio(idEGov, pddRuolo, "codice_risposta_ingresso");
			Assert.assertEquals(codiceIngresso[0], codiceIngressoAtteso, "Codice di risposta ricevuto dal connettore (codice_risposta_ingresso, "+pddRuolo+")");
			String[] codiceUscita = verificatore.getValuesTracedByIdMessaggio(idEGov, pddRuolo, "codice_risposta_uscita");
			Assert.assertEquals(codiceUscita[0], codiceIngressoAtteso, "Codice di risposta restituito (codice_risposta_uscita, "+pddRuolo+")");
		}
	}






	/***
	 * Profilo sincrono: TextMessage su coda
	 */
	Repository repositoryTextQueue=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_TEXT_QUEUE"})
	public void sincronoTextQueue() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		testSincrono(this.repositoryTextQueue, CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_TEXT_QUEUE,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_TEXT_QUEUE, true, true, false);
	}
	@DataProvider (name="sincronoTextQueue")
	public Object[][]testSincronoTextQueueProvider() throws TestSuiteException {
		String id=this.repositoryTextQueue.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_TEXT_QUEUE"},dataProvider="sincronoTextQueue",dependsOnMethods={"sincronoTextQueue"})
	public void testSincronoTextQueue(DatabaseComponent data,String id) throws TestSuiteException {
		verificaTracciamentoSincrono(data, id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_TEXT_QUEUE);
	}


	/***
	 * Profilo sincrono: BytesMessage su coda
	 */
	Repository repositoryBytesQueue=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_BYTES_QUEUE"})
	public void sincronoBytesQueue() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		testSincrono(this.repositoryBytesQueue, CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_BYTES_QUEUE,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_BYTES_QUEUE, true, false, false);
	}
	@DataProvider (name="sincronoBytesQueue")
	public Object[][]testSincronoBytesQueueProvider() throws TestSuiteException {
		String id=this.repositoryBytesQueue.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_BYTES_QUEUE"},dataProvider="sincronoBytesQueue",dependsOnMethods={"sincronoBytesQueue"})
	public void testSincronoBytesQueue(DatabaseComponent data,String id) throws TestSuiteException {
		verificaTracciamentoSincrono(data, id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_BYTES_QUEUE);
	}


	/***
	 * Profilo sincrono: TextMessage su topic
	 */
	Repository repositoryTextTopic=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_TEXT_TOPIC"})
	public void sincronoTextTopic() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		testSincrono(this.repositoryTextTopic, CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_TEXT_TOPIC,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_TEXT_TOPIC, false, true, false);
	}
	@DataProvider (name="sincronoTextTopic")
	public Object[][]testSincronoTextTopicProvider() throws TestSuiteException {
		String id=this.repositoryTextTopic.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_TEXT_TOPIC"},dataProvider="sincronoTextTopic",dependsOnMethods={"sincronoTextTopic"})
	public void testSincronoTextTopic(DatabaseComponent data,String id) throws TestSuiteException {
		verificaTracciamentoSincrono(data, id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_TEXT_TOPIC);
	}


	/***
	 * Profilo sincrono: BytesMessage su topic
	 */
	Repository repositoryBytesTopic=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_BYTES_TOPIC"})
	public void sincronoBytesTopic() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		testSincrono(this.repositoryBytesTopic, CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_BYTES_TOPIC,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_BYTES_TOPIC, false, false, false);
	}
	@DataProvider (name="sincronoBytesTopic")
	public Object[][]testSincronoBytesTopicProvider() throws TestSuiteException {
		String id=this.repositoryBytesTopic.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_BYTES_TOPIC"},dataProvider="sincronoBytesTopic",dependsOnMethods={"sincronoBytesTopic"})
	public void testSincronoBytesTopic(DatabaseComponent data,String id) throws TestSuiteException {
		verificaTracciamentoSincrono(data, id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_BYTES_TOPIC);
	}


	/***
	 * Profilo sincrono: propagazione delle informazioni eGov su coda
	 */
	Repository repositoryPropagazioneEGovQueue=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_PROPAGAZIONE_EGOV_QUEUE"})
	public void sincronoPropagazioneEGovQueue() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		testSincrono(this.repositoryPropagazioneEGovQueue, CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_INFO_EGOV_QUEUE,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_INFO_EGOV_QUEUE, true, true, true);
	}
	@DataProvider (name="sincronoPropagazioneEGovQueue")
	public Object[][]testSincronoPropagazioneEGovQueueProvider() throws TestSuiteException {
		String id=this.repositoryPropagazioneEGovQueue.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_PROPAGAZIONE_EGOV_QUEUE"},dataProvider="sincronoPropagazioneEGovQueue",dependsOnMethods={"sincronoPropagazioneEGovQueue"})
	public void testSincronoPropagazioneEGovQueue(DatabaseComponent data,String id) throws TestSuiteException {
		verificaTracciamentoSincrono(data, id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_INFO_EGOV_QUEUE);
	}


	/***
	 * Profilo sincrono: propagazione delle informazioni eGov su topic
	 */
	Repository repositoryPropagazioneEGovTopic=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_PROPAGAZIONE_EGOV_TOPIC"})
	public void sincronoPropagazioneEGovTopic() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		testSincrono(this.repositoryPropagazioneEGovTopic, CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_INFO_EGOV_TOPIC,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_INFO_EGOV_TOPIC, false, true, true);
	}
	@DataProvider (name="sincronoPropagazioneEGovTopic")
	public Object[][]testSincronoPropagazioneEGovTopicProvider() throws TestSuiteException {
		String id=this.repositoryPropagazioneEGovTopic.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_PROPAGAZIONE_EGOV_TOPIC"},dataProvider="sincronoPropagazioneEGovTopic",dependsOnMethods={"sincronoPropagazioneEGovTopic"})
	public void testSincronoPropagazioneEGovTopic(DatabaseComponent data,String id) throws TestSuiteException {
		verificaTracciamentoSincrono(data, id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_INFO_EGOV_TOPIC);
	}


	/***
	 * Profilo sincrono: sbustamento SOAP su coda
	 */
	Repository repositorySbustamentoQueue=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_SBUSTAMENTO_SOAP_QUEUE"})
	public void sincronoSbustamentoQueue() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		String contenuto = testSincrono(this.repositorySbustamentoQueue, CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_SBUSTAMENTO_SOAP_QUEUE,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_SBUSTAMENTO_SOAP_QUEUE, true, true, false);
		Assert.assertFalse(contenuto.contains("Envelope"), "Il messaggio pubblicato non doveva contenere il SOAP Envelope");
	}
	@DataProvider (name="sincronoSbustamentoQueue")
	public Object[][]testSincronoSbustamentoQueueProvider() throws TestSuiteException {
		String id=this.repositorySbustamentoQueue.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_SBUSTAMENTO_SOAP_QUEUE"},dataProvider="sincronoSbustamentoQueue",dependsOnMethods={"sincronoSbustamentoQueue"})
	public void testSincronoSbustamentoQueue(DatabaseComponent data,String id) throws TestSuiteException {
		verificaTracciamentoSincrono(data, id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_SBUSTAMENTO_SOAP_QUEUE);
	}


	/***
	 * Profilo sincrono: sbustamento SOAP su topic
	 */
	Repository repositorySbustamentoTopic=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_SBUSTAMENTO_SOAP_TOPIC"})
	public void sincronoSbustamentoTopic() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		String contenuto = testSincrono(this.repositorySbustamentoTopic, CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_SBUSTAMENTO_SOAP_TOPIC,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_SBUSTAMENTO_SOAP_TOPIC, false, true, false);
		Assert.assertFalse(contenuto.contains("Envelope"), "Il messaggio pubblicato non doveva contenere il SOAP Envelope");
	}
	@DataProvider (name="sincronoSbustamentoTopic")
	public Object[][]testSincronoSbustamentoTopicProvider() throws TestSuiteException {
		String id=this.repositorySbustamentoTopic.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_SBUSTAMENTO_SOAP_TOPIC"},dataProvider="sincronoSbustamentoTopic",dependsOnMethods={"sincronoSbustamentoTopic"})
	public void testSincronoSbustamentoTopic(DatabaseComponent data,String id) throws TestSuiteException {
		verificaTracciamentoSincrono(data, id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_SBUSTAMENTO_SOAP_TOPIC);
	}


	/***
	 * Profilo sincrono: risposta con l'elemento wrapper dell'operazione (proprietà 'connettori.jms.response.soap.operationWrapper')
	 */
	Repository repositoryOperationWrapper=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_OPERATION_WRAPPER"})
	public void sincronoOperationWrapper() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		testSincrono(this.repositoryOperationWrapper, CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_OPERATION_WRAPPER,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_OPERATION_WRAPPER, true, true, false);
	}
	@DataProvider (name="sincronoOperationWrapper")
	public Object[][]testSincronoOperationWrapperProvider() throws TestSuiteException {
		String id=this.repositoryOperationWrapper.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_OPERATION_WRAPPER"},dataProvider="sincronoOperationWrapper",dependsOnMethods={"sincronoOperationWrapper"})
	public void testSincronoOperationWrapper(DatabaseComponent data,String id) throws TestSuiteException {
		verificaTracciamentoSincrono(data, id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_OPERATION_WRAPPER);
	}


	/***
	 * Profilo sincrono: risposta vuota del connettore sostituita tramite una trasformazione della risposta
	 */
	Repository repositoryTrasformazioneRisposta=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_TRASFORMAZIONE_RISPOSTA"})
	public void sincronoTrasformazioneRisposta() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		testSincrono(this.repositoryTrasformazioneRisposta, CostantiTestSuite.PORTA_DELEGATA_JMS_SINCRONO_TRASFORMAZIONE_RISPOSTA,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_TRASFORMAZIONE_RISPOSTA, true, true, false);
	}
	@DataProvider (name="sincronoTrasformazioneRisposta")
	public Object[][]testSincronoTrasformazioneRispostaProvider() throws TestSuiteException {
		String id=this.repositoryTrasformazioneRisposta.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".SINCRONO_TRASFORMAZIONE_RISPOSTA"},dataProvider="sincronoTrasformazioneRisposta",dependsOnMethods={"sincronoTrasformazioneRisposta"})
	public void testSincronoTrasformazioneRisposta(DatabaseComponent data,String id) throws TestSuiteException {
		verificaTracciamentoSincrono(data, id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_JMS_TRASFORMAZIONE_RISPOSTA);
	}


	/***
	 * Profilo oneway: la proprietà 'connettori.jms.response.soap.operationWrapper' non produce alcuna risposta verso il client
	 */
	Repository repositoryOnewayOperationWrapper=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".ONEWAY_OPERATION_WRAPPER"})
	public void onewayOperationWrapper() throws TestSuiteException, AxisFault {
		if(!this.doTestJMS){
			return;
		}
		svuotaCoda();
		ClientOneWay client = invocazioneOneway(this.repositoryOnewayOperationWrapper, CostantiTestSuite.PORTA_DELEGATA_JMS_ONEWAY_OPERATION_WRAPPER);
		String idEGov = client.getIdMessaggio();

		Message msg = readMessaggioPubblicato(true, idEGov);
		String contenuto = new String(readContent(msg, true), StandardCharsets.UTF_8);
		Assert.assertTrue(contenuto.contains(client.getLastIDUnivocoGenerato()), "Il messaggio pubblicato non contiene l'identificativo univoco della richiesta");
		JMSTestUtilities.checkProperty(msg, getJMSPropertyName(org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance().getAzioneTrasporto()),
				CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_OPERATION_WRAPPER);

		// La risposta può contenere al massimo il messaggio OpenSPCoopOK, già verificato dal client, ma non l'elemento wrapper dell'operazione
		org.apache.axis.Message risposta = client.getResponseMessage();
		javax.xml.soap.SOAPBody body = null;
		try {
			body = risposta!=null ? risposta.getSOAPBody() : null;
		}catch(SOAPException e) {
			throw new TestSuiteException("Lettura del SOAP Body della risposta non riuscita", e);
		}
		if(body!=null) {
			NodeList nl = body.getElementsByTagNameNS(OPERATION_WRAPPER_NAMESPACE, OPERATION_WRAPPER_LOCAL_NAME);
			Assert.assertEquals(nl.getLength(), 0, "Elemento wrapper non atteso nella risposta di un'azione con profilo oneway");
		}
		
		verificaTransazioni(idEGov, getServizioApplicativo(true, true, false), true, false);
	}
	@DataProvider (name="onewayOperationWrapper")
	public Object[][]testOnewayOperationWrapperProvider() throws TestSuiteException {
		String id=this.repositoryOnewayOperationWrapper.getNext();
		DatabaseComponent[] dbs = dataProviderComponents();
		return new Object[][]{ {dbs[0],id}, {dbs[1],id} };
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,ConnettoreJMSRisposta.ID_GRUPPO,ConnettoreJMSRisposta.ID_GRUPPO+".ONEWAY_OPERATION_WRAPPER"},dataProvider="onewayOperationWrapper",dependsOnMethods={"onewayOperationWrapper"})
	public void testOnewayOperationWrapper(DatabaseComponent data,String id) throws TestSuiteException {
		if(!this.doTestJMS){
			data.close();
			return;
		}
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_OPERATION_WRAPPER, false,null);
		}finally{
			data.close();
		}
	}

}
