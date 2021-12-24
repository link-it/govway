/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.logger.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.logger.IContext;
import org.openspcoop2.utils.logger.ILogger;
import org.openspcoop2.utils.logger.LoggerFactory;
import org.openspcoop2.utils.logger.beans.Attachment;
import org.openspcoop2.utils.logger.beans.Event;
import org.openspcoop2.utils.logger.beans.Message;
import org.openspcoop2.utils.logger.beans.Property;
import org.openspcoop2.utils.logger.beans.context.application.ApplicationContext;
import org.openspcoop2.utils.logger.beans.context.application.ApplicationTransaction;
import org.openspcoop2.utils.logger.beans.context.batch.BatchContext;
import org.openspcoop2.utils.logger.beans.context.batch.BatchTransaction;
import org.openspcoop2.utils.logger.beans.context.core.AbstractContext;
import org.openspcoop2.utils.logger.beans.context.core.AbstractContextWithClient;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransaction;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransactionWithClient;
import org.openspcoop2.utils.logger.beans.context.core.Actor;
import org.openspcoop2.utils.logger.beans.context.core.ConnectionMessage;
import org.openspcoop2.utils.logger.beans.context.core.HttpClient;
import org.openspcoop2.utils.logger.beans.context.core.HttpServer;
import org.openspcoop2.utils.logger.beans.context.core.Identifier;
import org.openspcoop2.utils.logger.beans.context.core.Operation;
import org.openspcoop2.utils.logger.beans.context.core.Role;
import org.openspcoop2.utils.logger.beans.context.core.Service;
import org.openspcoop2.utils.logger.beans.context.proxy.ProxyContext;
import org.openspcoop2.utils.logger.beans.context.proxy.ProxyTransaction;
import org.openspcoop2.utils.logger.config.DiagnosticConfig;
import org.openspcoop2.utils.logger.config.Log4jConfig;
import org.openspcoop2.utils.logger.config.MultiLoggerConfig;
import org.openspcoop2.utils.logger.constants.LowSeverity;
import org.openspcoop2.utils.logger.constants.MessageType;
import org.openspcoop2.utils.logger.constants.Severity;
import org.openspcoop2.utils.logger.constants.context.FlowMode;
import org.openspcoop2.utils.logger.constants.context.Result;
import org.openspcoop2.utils.logger.constants.context.ServerEndpointType;
import org.openspcoop2.utils.logger.log4j.Log4JLoggerWithBatchContext;
import org.openspcoop2.utils.logger.log4j.Log4JLoggerWithProxyContext;
import org.openspcoop2.utils.logger.log4j.Log4jLoggerWithApplicationContext;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * Test
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Test {

	public static void main(String[] args) throws Exception {
		
		String tipo = null;
		if(args!=null && args.length>0) {
			tipo = args[0];
		}
		
		
		// DIAGNOSTIC CONFIGURATION
		DiagnosticConfig diagnosticConfig = new DiagnosticConfig();
		diagnosticConfig.setDiagnosticConfigURL(Test.class.getResource("/org/openspcoop2/utils/logger/test/example.msgDiagnostici.properties"));
		diagnosticConfig.setThrowExceptionPlaceholderFailedResolution(true);
		
		
		// LOG4J CONFIGURATION
		Log4jConfig log4jConfig = new Log4jConfig();
		boolean exampleWithProperties = true;
		if(exampleWithProperties){
			log4jConfig.setLog4jConfigURL(Test.class.getResource("/org/openspcoop2/utils/logger/test/example.log4j2.properties"));
		}
		else{
			log4jConfig.setLog4jConfigURL(Test.class.getResource("/org/openspcoop2/utils/logger/test/example.log4j2.xml"));
		}
		
		
		// MULTILOGGER
		
		MultiLoggerConfig mConfig = new MultiLoggerConfig();
		
		mConfig.setDiagnosticConfig(diagnosticConfig);
		
		mConfig.setDiagnosticSeverityFilter(Severity.DEBUG_HIGH);
//		mConfig.setEventSeverityFilter(Severity.INFO);
		
		mConfig.setLog4jLoggerEnabled(true);
		mConfig.setLog4jConfig(log4jConfig);
		
		mConfig.setDbLoggerEnabled(false);
		//mConfig.setDatabaseConfig(dbConfig);
		
		
		
		if(tipo==null || "proxy".equalsIgnoreCase(tipo)) {
		
			// ** PROXY **
			
			System.out.println("*** Test Proxy ***");
			
			LoggerFactory.initialize(Log4JLoggerWithProxyContext.class.getName(),
					mConfig);
			
			//Log4JLogger.setDiagnosticSeverity(Severity.INFO);
			//Log4JLogger.setEventSeverity(Severity.ERROR);
			
			test(LoggerFactory.newLogger(), "PROXY");
			
		}
		
		
		if(tipo==null || "application".equalsIgnoreCase(tipo)) {
		
			// ** APPLICATION **
			
			System.out.println("*** Test Application ***");
			
			//LoggerFactory.initialize(Log4jLoggerWithApplicationContext.class.getName(),
			//		mConfig);
			
			//Log4JLogger.setDiagnosticSeverity(Severity.INFO);
			//Log4JLogger.setEventSeverity(Severity.ERROR);
			
			test(LoggerFactory.newLogger(Log4jLoggerWithApplicationContext.class.getName()), "APPLICATION");
		
		}
		
		
		if(tipo==null || "batch".equalsIgnoreCase(tipo)) {
		
			// ** BATCH **
			
			System.out.println("*** Test Batch ***");
			
			//LoggerFactory.initialize(Log4JLoggerWithBatchContext.class.getName(),
			//		mConfig);
			
			//Log4JLogger.setDiagnosticSeverity(Severity.INFO);
			//Log4JLogger.setEventSeverity(Severity.ERROR);
			
			test(LoggerFactory.newLogger(Log4JLoggerWithBatchContext.class), "BATCH");
			
		}
	}
	
	
	private static void test(ILogger logger, String tipo) throws Exception {
		
		logger.log("================= "+tipo+" ===================",LowSeverity.DEBUG_HIGH);
		
		logger.log("Prova altro log con messaggio inserito qua",LowSeverity.DEBUG_HIGH);
		logger.log("Prova altro log con messaggio inserito qua con function",LowSeverity.DEBUG_MEDIUM,"core");
		
		logger.log("000002");
		logger.log("core.inizializzazione"); 
		
		logger.log("001001","21 Ottobre 2015","GovPay");
		
		
		
		IContext context = logger.getContext();
		AbstractTransaction transaction = ((AbstractContext)context).getTransaction();
		
		AbstractTransactionWithClient transactionWithClient = null;
		AbstractContextWithClient contextWithClient = null;
		
		ProxyContext proxyContext = null;
		if(context instanceof ProxyContext) {
			proxyContext = (ProxyContext) context;
			transactionWithClient = (AbstractTransactionWithClient) transaction;
			contextWithClient = (AbstractContextWithClient) context;
		}
		
		ApplicationContext applicationContext = null;
		if(context instanceof ApplicationContext) {
			applicationContext = (ApplicationContext) context;
			transactionWithClient = (AbstractTransactionWithClient) transaction;
			contextWithClient = (AbstractContextWithClient) context;
		}
		
		BatchContext batchContext = null;
		if(context instanceof BatchContext) {
			batchContext = (BatchContext) context;
		}
				
		transaction.setClusterId("Cluster1-TIPO-"+tipo);
		
		logger.log("000003",transaction);		
		
		try{
			
			long httpRequestSize = 2345;
			if(contextWithClient!=null) {
				contextWithClient.getRequest().setDate(new Date());
				contextWithClient.getRequest().setSize(httpRequestSize);
			}
		
			// .... TODO DUMP RICHIESTA
			boolean dump = true;
			if(dump){
				
				byte[] content = "<prova>CIAO</PROVA>".getBytes();
				
				Message message = new Message();
				message.setIdMessage("TIPO-"+tipo);
				message.setType(MessageType.REQUEST_IN);
				message.setContent(content);
				message.setContentType("text/xml");
				
				Property header1 = new Property("Transfer-Encoding","Chunked");
				message.addHeader(header1);
				Property header2 = new Property("WebClient","Firefox");
				message.addHeader(header2);
				
				Property xpath1 = new Property("RisorsaNome","Rossi");
				message.addResource(xpath1);
				
				Attachment att = new Attachment();
				att.setContent("HELLO WORLD".getBytes());
				att.setContentType("text/plain");
				att.setContentId("cid:XXX");
				message.addAttachment(att);
				
				logger.log(message);
				
			}
			
			
		
			// ... TODO Processo di autenticazione ...
	
			HttpClient client = null;
			if(transactionWithClient!=null) {
				client = new HttpClient();
				client.setPrincipal("C=IT,O=ComuneRoma");
				client.setName("ServizioApplicativoAutenticato");
				transactionWithClient.setClient(client);
			}
			
			// ... TODO PRoceso di identificaizone funzione smistatore (es. PortaDlegata)
			if(transactionWithClient!=null) {
				transactionWithClient.getClient().setInvocationEndpoint("/govway/out/NomePortaDelegta?prova=si");
				transactionWithClient.getClient().setInterfaceName("NomePortaDelegata");
			}
			
			// .... TODO DOMINIO PER CUI AGISCO
			transaction.setDomain("GovPayFunction");
			transaction.setRole(Role.CLIENT);
			
			
			// ... TODO Identificazione attori, servizio, azione
		
			Actor from = new Actor();
			from.setType("SPC");
			from.setName("MinisteroFruitore");
			from.setAddress("http://pddMinisteroFruitore");
			transaction.setFrom(from);
			
			Actor to = new Actor();
			to.setType("SPC");
			to.setName("MinisteroErogatore");
			to.setAddress("http://pddMinisteroErogatore");
			transaction.setTo(to);
			
			Service service = new Service();
			service.setType("SPC");
			service.setName("VariazioneAnagrafica");
			service.setVersion(1);
			transaction.setService(service);
			
			Operation operation = new Operation();
			operation.setName("aggiornamento");
			operation.setMode(FlowMode.INPUT_OUTPUT);
			transaction.setOperation(operation);
		
	
			

						
			// .... TODO LOGICA INTERNA
			
			Identifier idRequest = null;
			if(contextWithClient!=null) {
				
				logger.log("001002");
				
				idRequest = new Identifier();
				idRequest.setId("Prova_238231232_3232");
				
				contextWithClient.getRequest().setIdentifier(idRequest);
				contextWithClient.getRequest().setCorrelationIdentifier("ID_CORRELAZIONE_APPLICATIVA");

				Property p1 = new Property("Prova","TestProva");
				Property p2 = new Property("Prova2","TestProva2");
				Property p3 = new Property("Prova3","TestProva3");
				contextWithClient.getRequest().addGenericProperty(p1);
				contextWithClient.getRequest().addGenericProperty(p2);
				contextWithClient.getRequest().addGenericProperty(p3);
				
				logger.log("002001");
			}
			
			Contenitore c = new Contenitore();
			
			List<String> listPrimitive = new ArrayList<String>();
			listPrimitive.add("EsempioListPrimitiveValue1");
			listPrimitive.add("EsempioListPrimitiveValue2");
			c.setListPrimitive(listPrimitive);
			
			List<Property> listProperty = new ArrayList<Property>();
			Property p1 = new Property("Prova","TestProva");
			Property p2 = new Property("Prova2","TestProva2");
			Property p3 = new Property("Prova3","TestProva3");
			listProperty.add(p1);
			listProperty.add(p2);
			listProperty.add(p3);
			c.setListProperty(listProperty);
			
			Integer [] arrayPrimitive = new Integer[3];
			arrayPrimitive[0] = 11;
			arrayPrimitive[1] = 22;
			arrayPrimitive[2] = 33;
			c.setArrayPrimitive(arrayPrimitive);
			
			Property [] arrayProperty = new Property[3];
			arrayProperty[0] = p1;
			arrayProperty[1] = p2;
			arrayProperty[2] = p3;
			c.setArrayProperty(arrayProperty);
			
			Map<String, Long> mapPrimitive = new java.util.HashMap<String,Long>(); 
			mapPrimitive.put("K1", 555l);
			mapPrimitive.put("K2", 666l);
			mapPrimitive.put("K3", 777l);
			c.setMapPrimitive(mapPrimitive);
			
			Map<String, Property> mapProperty = new java.util.HashMap<String,Property>(); 
			mapProperty.put("K1", p1);
			mapProperty.put("K2", p2);
			mapProperty.put("K3", p3);
			c.setMapProperty(mapProperty);
			
			logger.log("002002",c);
			logger.log("002003",c);
			logger.log("002004",c);

			
			List<String> servers = new ArrayList<>();
			if(proxyContext!=null) {
				servers.add("ApplicativoServer");
			}
			else {
				servers.add("ApplicativoServer1");
				servers.add("ApplicativoServer2");
			}
			
			for (String serverName : servers) {
				
				String idOperation = "OperationXX";  // serve in caso di nome del server uguale per differenziare l'operazione
				
				// .... TODO DUMP RICHIESTA USCITA
				if(dump){
					
					byte[] content = "<prova>CIAO</PROVA>".getBytes();
					
					Message message = new Message();
					message.setType(MessageType.REQUEST_OUT);
					message.setContent(content);
					message.setContentType("text/xml");
					message.setIdServer(serverName);
					message.setIdOperation(idOperation);
					if(idRequest!=null) {
						message.setIdMessage(idRequest.getId());
					}
					
					Property header1 = new Property("Transfer-Encoding","Chunked");
					message.addHeader(header1);
					Property header2 = new Property("WebClient","Firefox");
					message.addHeader(header2);
					
					Property xpath1 = new Property("RisorsaNome","Rossi");
					message.addResource(xpath1);
					
					Attachment att = new Attachment();
					att.setContent("HELLO WORLD".getBytes());
					att.setContentType("text/plain");
					att.setContentId("cid:XXX");
					message.addAttachment(att);
					
					logger.log(message);
					
				}
				
				
				// .... TODO INFORMAZIONI SERVER A CUI DEVO INOLTRARE IL PACCHETTO
				
				HttpServer server = new HttpServer();
				server.setEndpoint("http://127.0.0.1:8080/server");
				server.setEndpointType(ServerEndpointType.HTTP.name());
				server.setName(serverName);
				server.setIdOperation(idOperation);
				if(proxyContext!=null) {
					((ProxyTransaction)transaction).setServer(server);
				}
				else if(applicationContext!=null) {
					((ApplicationTransaction)transaction).addServer(server);
				}
				else if(batchContext!=null) {
					((BatchTransaction)transaction).addServer(server);
				}
				
				if(proxyContext!=null) {
					logger.log("003001");
				}
				else if(applicationContext!=null) {
					logger.log("003011", server);
				}
				else {
					logger.log("003021", server);
				}
				
				long httpRequestModifySize = 231232;
				ConnectionMessage requestConnection = new ConnectionMessage();
				requestConnection.setDate(new Date());
				requestConnection.setSize(httpRequestModifySize);
				requestConnection.setIdMessage("ID-SERVER-REQ");
				server.setRequest(requestConnection);
				
				// .... TODO SEND E VERIFICA RISPOSTA RITORNATA
				
				int returnCode = 500;
				server.setResponseStatusCode(returnCode);
				server.setTransportRequestMethod(HttpRequestMethod.POST);
				if(returnCode == 200){
					if(proxyContext!=null) {
						logger.log("003002");
					}
					else if(applicationContext!=null) {
						logger.log("003012", server);
					}
					else {
						logger.log("003022", server);
					}
				}
				else{
					if(proxyContext!=null) {
						logger.log("003003");
					}
					else if(applicationContext!=null) {
						logger.log("003013", server);
					}
					else {
						logger.log("003023", server);
					}
				}
				
				ConnectionMessage responseConnection = new ConnectionMessage();
				responseConnection.setDate(new Date());
				responseConnection.setSize(httpRequestSize);
				responseConnection.setIdMessage("ID-SERVER-RESP");
				server.setResponse(responseConnection);
		
				// .... TODO DUMP IN RISPOSTA
				if(dump){
					
					byte[] content = "<prova>CIAO</PROVA>".getBytes();
					
					Message message = new Message();
					message.setType(MessageType.RESPONSE_IN);
					message.setContent(content);
					message.setContentType("text/xml");
					message.setIdServer(serverName);
					message.setIdOperation(idOperation);
					
					Property header1 = new Property("Transfer-Encoding","Chunked");
					message.addHeader(header1);
					Property header2 = new Property("WebClient","Firefox");
					message.addHeader(header2);
					
					Property xpath1 = new Property("RisorsaNome","Rossi");
					message.addResource(xpath1);
					
					Attachment att = new Attachment();
					att.setContent("HELLO WORLD".getBytes());
					att.setContentType("text/plain");
					att.setContentId("cid:XXX");
					message.addAttachment(att);
					
					logger.log(message);
				}
				
				Property pServer1 = new Property("Prova-"+serverName,"TestProva"+serverName);
				Property pServer2 = new Property("Prova2"+serverName,"TestProva2"+serverName);
				Property pServer3 = new Property("Prova3","TestProva3"+serverName); // NomeProva3 e' usato nei diagnostici
				
				server.addGenericProperty(pServer1);
				server.addGenericProperty(pServer2);
				server.addGenericProperty(pServer3);
				
			}
			
			if(proxyContext!=null) {
				logger.log("003004");
			}
			else if(applicationContext!=null) {
				logger.log("003014");
			}
			else {
				logger.log("003024");
			}
			
			
			
			
			
			
			
			
			
			// .... TODO LOGICA INTERNA PER ANALISI RISPOSTA
			
			boolean fault = true; // SIMULAZIONE TODO...
			
			Identifier idResponse = null;
			if(contextWithClient!=null) {
				idResponse = new Identifier();
				idResponse.setId("ProvaResponse_238231232_3232");
				contextWithClient.getResponse().setIdentifier(idResponse);
				contextWithClient.getResponse().setCorrelationIdentifier("ID_CORRELAZIONE_APPLICATIVA_RISP");
			
				int returnCodeClient = 200;
				if(fault){
					returnCodeClient = 500;
				}
				
				
				
				
				// .... TODO DUMP OUT RISPOSTA
				if(dump){
					
					byte[] content = "<prova>CIAO</PROVA>".getBytes();
					
					Message message = new Message();
					message.setType(MessageType.RESPONSE_OUT);
					message.setContent(content);
					message.setContentType("text/xml");
					
					Property header1 = new Property("Transfer-Encoding","Chunked");
					message.addHeader(header1);
					Property header2 = new Property("WebClient","Firefox");
					message.addHeader(header2);
					
					Property xpath1 = new Property("RisorsaNome","Rossi");
					message.addResource(xpath1);
					
					Attachment att = new Attachment();
					att.setContent("HELLO WORLD".getBytes());
					att.setContentType("text/plain");
					att.setContentId("cid:XXX");
					message.addAttachment(att);
					
					logger.log(message);
				}
				
				
				// TODO IF FAULT
	
				if(fault){
					byte[] faultRicevuto = null;
					contextWithClient.getResponse().setFault(faultRicevuto);
					
				}
				
				((HttpClient)transactionWithClient.getClient()).setResponseStatusCode(returnCodeClient);
				((HttpClient)transactionWithClient.getClient()).setTransportRequestMethod(HttpRequestMethod.POST);
				((HttpClient)transactionWithClient.getClient()).setSocketClientAddress("127.0.0.1");
				((HttpClient)transactionWithClient.getClient()).setTransportClientAddress("10.0.0.1");
				
				transactionWithClient.getClient().addGenericProperty(p1);
				transactionWithClient.getClient().addGenericProperty(p2);
				transactionWithClient.getClient().addGenericProperty(p3);
			}
				
			if(fault){
				transaction.setResult(Result.SERVER_ERROR);
			}
			else{
				transaction.setResult(Result.SUCCESS);
			}
			
			// Ritorno risposta al client su http post
			
			if(contextWithClient!=null) {
				
				if(fault)
					logger.log("001004");
				else
					logger.log("001003");
				
				contextWithClient.getResponse().setDate(new Date());
				contextWithClient.getResponse().setSize(httpRequestSize);
			}
			
			logger.log();
			
			
			Event event = new Event();
			event.setDate(DateManager.getDate());
			event.setClusterId("ClusteId-TIPO-"+tipo);
			event.setCode("XXX");
			event.setSource("ModuloControllo");
			event.setSeverity(Severity.ERROR);
			logger.log(event);
			
		}catch(Exception e){
			logger.log("000001",e.getMessage()!=null?e.getMessage():"NullPointer");
			e.printStackTrace(System.out);
		}
	}

}
