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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.checkAll200;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.checkResponsesStatus;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;
import jakarta.xml.ws.BindingProvider;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations.TipoFault;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception;
import org.openspcoop2.pdd.services.cxf.MessageBox;
import org.openspcoop2.pdd.services.cxf.MessageBoxService;
import org.openspcoop2.pdd.services.skeleton.IdentificativoIM;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilsException;
import org.slf4j.Logger;

/**
 * CommonConsegnaMultipla
 * 
 * @author Francesco Scarlato
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CommonConsegnaMultipla {
	
	public static final EsitiProperties esitiProperties = getEsitiProperties();
	
	final static QName SERVICE_NAME_MessageBox = new QName("http://services.pdd.openspcoop2.org", "MessageBoxService");
	
	public static final String PROTOCOL_NAME = "trasparente";

	public static final int ESITO_CONSEGNA_MULTIPLA = 38;

	public static final int ESITO_CONSEGNA_MULTIPLA_COMPLETATA = 39;

	public static final int ESITO_CONSEGNA_MULTIPLA_FALLITA = 40;

	public static final int ESITO_CONSEGNA_MULTIPLA_IN_CORSO = 48;
	
	public static final int ESITO_ERRORE_PROCESSAMENTO_PDD_4XX = 4;
	
	public static final int ESITO_MESSAGE_BOX = 47;
	
	public static final int ESITO_3XX = 28;
	public static final int ESITO_4XX = 29;
	public static final int ESITO_5XX = 30;
	public static final int ESITO_OK = 0;
	public static final int ESITO_ERRORE_INVOCAZIONE = 10;
	public static final int ESITO_ERRORE_APPLICATIVO = 2;
	public static final int ESITO_OK_PRESENZA_ANOMALIE = 12;
	
	public static final String FAULT_REST = "{\"type\":\"https://httpstatuses.com/500\",\"title\":\"Internal Server Error\",\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
	public static final String FORMATO_FAULT_REST = "JSON";
	
	public static final String FAULT_SOAP1_1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:Server.OpenSPCoo"
			+ "pExampleFault</faultcode><faultstring>Fault ritornato dalla servlet di trace, esempio di OpenSPCoop</faultstring><faultactor>OpenSPCoopTrace</faultactor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
	
	public static final String FAULT_SOAP1_2 = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header/><env:Body><env:Fault><env:Code><env:Value>env:Receiver</env:Value><env:Subcode><env:Value xmlns:ns1=\"http://www.openspcoop2.or"
			+ "g/example\">ns1:Server.OpenSPCoopExampleFault</env:Value></env:Subcode></env:Code><env:Reason><env:Text xml:lang=\"en-US\">Fault ritornato dalla servlet di trace, esempio di OpenSPCoop</env:Text></env:Reason><env:Role>OpenSPCoopTrace</env:Ro"
			+ "le></env:Fault></env:Body></env:Envelope>";
	
	public static final String FORMATO_FAULT_SOAP1_1 = "SOAP_11";
	public static final String FORMATO_FAULT_SOAP1_2 = "SOAP_12";
	
	public static final boolean attesaAttivaDebugLogger = Boolean
			.valueOf(System.getProperty("connettori.consegna_multipla.attesaAttiva.log"));
	public static final boolean attesaAttivaDebugSystemOut = Boolean
			.valueOf(System.getProperty("connettori.consegna_multipla.attesaAttiva.systemOut"));
	
	public static final int intervalloControllo = Integer
			.valueOf(System.getProperty("connettori.consegna_multipla.next_messages.intervallo_controllo")) * 1000;
	
	public static final int scheduleNewAfter = Integer
			.valueOf(System.getProperty("connettori.consegna_multipla.next_messages.schedule_new_after")) * 1000;
	
	// Sommo un altro secondo rispetto a quello indicato in govway, perchè a quanto pare non basta.
	public static final int _intervalloControlloFallite = Integer.valueOf(
			System.getProperty("connettori.consegna_multipla.next_messages.consegna_fallita.intervallo_controllo"))
			* 1000;
	
	public static final int _intervalloMinimoRiconsegna = Integer.valueOf(System
			.getProperty("connettori.consegna_multipla.next_messages.consegna_fallita.intervallo_minimo_riconsegna"))
			* 1000;
	
	public static int getIntervalloControlloFallite() {
		// devo tornare il max
		if(_intervalloControlloFallite<intervalloControllo) {
			return intervalloControllo;
		}
		return _intervalloControlloFallite;
	}
	
	public static int getIntervalloMinimoRiconsegna() {
		// devo tornare il max
		if(_intervalloMinimoRiconsegna<getIntervalloControlloFallite()) {
			return getIntervalloControlloFallite();
		}
		return _intervalloMinimoRiconsegna;
	}
	
	public static final Path connettoriFilePath = Paths
			.get(System.getProperty("connettori.consegna_multipla.connettore_file.path"));
	
	
	public static final Map<Integer, Set<String>> statusCode2xxVsConnettori  = Map
			.of(200, Set.of(CONNETTORE_1,CONNETTORE_2),
				  201, Set.of(CONNETTORE_1, CONNETTORE_3),
				  202, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
				  206, Set.of(CONNETTORE_1),
				  299, Set.of(CONNETTORE_1));
	
	
	public static final Map<Integer, Set<String>> statusCode3xxVsConnettori  = Map
			.of(300, Set.of(CONNETTORE_1,CONNETTORE_2),
				  301, Set.of(CONNETTORE_1, CONNETTORE_3),
				  302, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
				  306, Set.of(CONNETTORE_1),
				  399, Set.of(CONNETTORE_1));
	
	public static final Map<Integer, Set<String>> statusCode4xxVsConnettori  = Map
			.of(400, Set.of(CONNETTORE_1,CONNETTORE_2),
				  401, Set.of(CONNETTORE_1, CONNETTORE_3),
				  402, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
				  406, Set.of(CONNETTORE_1),
				  499, Set.of(CONNETTORE_1));
	
	public static final Map<Integer, Set<String>> statusCode5xxVsConnettori  = Map
			.of(500, Set.of(CONNETTORE_1,CONNETTORE_2),
				  501, Set.of(CONNETTORE_1, CONNETTORE_3),
				  502, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
				  506, Set.of(CONNETTORE_1),
			  	  599, Set.of(CONNETTORE_1));
	

	public static final Map<Integer,Set<String>> statusCode2xx4xxVsConnettori = new HashMap<>();
	static {
		statusCode2xx4xxVsConnettori.putAll(statusCode2xxVsConnettori);
		statusCode2xx4xxVsConnettori.putAll(statusCode4xxVsConnettori);
	}
	
	
	public static final Map<Integer,Set<String>> statusCode3xx5xxVsConnettori = new HashMap<>();
	static {
		statusCode3xx5xxVsConnettori.putAll(statusCode3xxVsConnettori);
		statusCode3xx5xxVsConnettori.putAll(statusCode5xxVsConnettori);
	}
	
	
	public static final Map<Integer,Set<String>> statusCodeVsConnettori = new HashMap<>();
	static {
		statusCodeVsConnettori.putAll(statusCode2xxVsConnettori);
		statusCodeVsConnettori.putAll(statusCode4xxVsConnettori);
		statusCodeVsConnettori.putAll(statusCode5xxVsConnettori);
	}
	
	public static final Map<Integer,Set<String>> statusCodeRestVsConnettori = new HashMap<>();
	static {
		statusCodeRestVsConnettori.putAll(statusCode2xxVsConnettori);
		statusCodeRestVsConnettori.putAll(statusCode3xxVsConnettori);
		statusCodeRestVsConnettori.putAll(statusCode4xxVsConnettori);
		statusCodeRestVsConnettori.putAll(statusCode5xxVsConnettori);
	}

	public static <T> Set<T> setIntersection(Set<T> lhs, Set<T> rhs) {
		Set<T> ret = new HashSet<T>();
		
		for(var el : lhs) {
			if (rhs.contains(el)) {
				ret.add(el);
			}
		}
				
		return ret;
	}
	
	public static <T> Set<T> setSum(Set<T> lhs, Set<T> rhs) {
		Set<T> ret = new HashSet<T>(lhs);
		ret.addAll(rhs);
		return ret;
	}
	
	
	public static <T> Set<T> setDifference(Set<T> lhs, Set<T> rhs) {
		Set<T> ret = new HashSet<T>(lhs);
		ret.removeAll(rhs);
		return ret;
	}
	
	
	private static EsitiProperties getEsitiProperties() {
		try {
			return EsitiProperties.getInstanceFromProtocolName(ConfigLoader.getLoggerCore(), CommonConsegnaMultipla.PROTOCOL_NAME);
		} catch (ProtocolException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static MessageBox getMessageBox(String username, String password) throws MalformedURLException {
		String url 	= System.getProperty("connettori.message_box.url");

		URL wsdlLocation							= new URL(url+ "?wsdl");
		MessageBoxService imMessageBoxService	= new MessageBoxService(wsdlLocation, SERVICE_NAME_MessageBox);
		MessageBox imMessageBoxPort 						= imMessageBoxService.getMessageBox();
		BindingProvider imProviderMessageBox 		= (BindingProvider) imMessageBoxPort;
		
		imProviderMessageBox.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		imProviderMessageBox.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
		imProviderMessageBox.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		
		return imMessageBoxPort;
	}
	
	
	public static void withBackoff(Runnable r) {
		int[] delays =  {100, 500, 2000, 5000};
		for (int i = 0; i < delays.length; i++) {
			int delay = delays[i];
			if(i>0) {
				ConfigLoader.getLoggerCore().debug("Attivo delay a '"+delay+"'");
			}
			Utilities.sleep(delay);
			try {
				r.run(); return;
			} catch (Throwable t) {
				ConfigLoader.getLoggerCore().debug("Errore che causa il prossimo delay: "+t.getMessage());
				//ConfigLoader.getLoggerCore().debug("Errore che causa il prossimo delay: "+t.getMessage(),t);
			}
		}
		throw new RuntimeException("Tentativi di backoff esauriti");
	}

	/**
	 * Esegue requests_per_batch richieste per ogni richiesta della lista.
	 * Restituisce la map delle risposte indicizzate per richiesta.
	 * 
	 */
	public static Map<RequestAndExpectations, List<HttpResponse>> makeRequestsByKind(List<RequestAndExpectations> requests, int requests_per_batch) {
		Map<RequestAndExpectations,  List<HttpResponse>> responses = new HashMap<>();
		
		int nThreads = Common.sogliaRichiesteSimultanee; 
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
		
		for (var request : requests) {			
			responses.put(request, new java.util.ArrayList<>());
			
			for (int i=0; i<requests_per_batch; i++) {
				executor.execute( () -> {
					responses.get(request).add(Utils.makeRequest(request.request));
				});
			}
		}
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			ConfigLoader.getLoggerCore().error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		return responses;
				
	}

	
	public  static void checkPresaInConsegna(List<HttpResponse> responses, int numConnettori) {
		for (var r : responses) {
			checkPresaInConsegna(r, numConnettori);
		}
	}
	
	
	/**
	 * Qui controlliamo anche l'esito sincrono, è per la consegna con notifiche.
	 */
	public  static void checkPresaInConsegnaNotifica(List<HttpResponse> responses, int numConnettori, int esitoSincrono) {
		for (var r : responses) {
			checkStatoConsegnaSincrona(r, ESITO_CONSEGNA_MULTIPLA, esitoSincrono, numConnettori);
		}
	}

	
	public static void checkPresaInConsegna(HttpResponse response, int numConnettori) {
		checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA, numConnettori);
	}
	
	public static void checkConsegnaCompletata(HttpResponse response) {
		checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA_COMPLETATA, 0);		
	}
	
    
	public static void checkStatoConsegna(List<HttpResponse> responses, int esito, int consegneMultipleRimanenti) {

		var id_transazioni = responses.stream()
				.map( response -> response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE));
		var sargs = Stream.of(esito, consegneMultipleRimanenti);
		
		Object[] args = Stream.concat(id_transazioni,sargs).toArray();
		
		var debug_transazioni = responses.stream()
				.map( response -> response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE))
				.collect(Collectors.toList());
		String debug_clause = String.join(",",debug_transazioni);  
		
		// Questo mi crea una stringa del tipo ( ?, ?, ? )
		String IN_CLAUSE = "( " + String.join(",", Collections.nCopies(responses.size(), "?")) + ") ";
		
		String query = "select count(*) from transazioni where id in "+IN_CLAUSE+" and esito = ? and consegne_multiple = ?";
		ConfigLoader.getLoggerCore().info("Checking stato consegna for transazioni:  " + debug_clause + " AND esito = " + esito + " AND consegne-rimanenti: " + consegneMultipleRimanenti);
		ConfigLoader.getLoggerCore().info("Query: " + query);
		Integer count = ConfigLoader.getDbUtils().readValueArray(query, Integer.class,  args);
		
		// debug for error
		if(count.intValue() != responses.size()) {
			ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa:");
			String query2 = "select esito, consegne_multiple from transazioni where id in "+IN_CLAUSE+"";
			Object [] argsDebug = new Object[responses.size()];
			int i = 0;
			for (HttpResponse r : responses) {
				argsDebug[i] = r.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				i++;
			}
			List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, argsDebug);
			for (var v : letto) {
				ConfigLoader.getLoggerCore().error(v.toString());
			}
		}
		
		assertEquals(Integer.valueOf(responses.size()), count);
	}
	
	
	public static void checkStatoConsegnaSincrona(HttpResponse response, int esito, int esitoSincrono, int consegneMultipleRimanenti) {
		String query = "select count(*) from transazioni where id=? and esito = ?  and esito_sincrono = ? and consegne_multiple = ?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		ConfigLoader.getLoggerCore().info("Checking stato consegna for transazione:  " + id_transazione + " AND esito = " + esito + " AND consegne-rimanenti: " + consegneMultipleRimanenti + " AND esito-sincrono: " + esitoSincrono);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class,  id_transazione, esito, esitoSincrono, consegneMultipleRimanenti);
		
		// debug for error
		if(count.intValue() != 1) {
			ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa:");
			String query2 = "select esito, esito_sincrono, consegne_multiple from transazioni where id = ?";
			List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione);
			for (var v : letto) {
				ConfigLoader.getLoggerCore().error(v.toString());
			}
		}
		
		assertEquals(Integer.valueOf(1), count);
	}
	

	/**
	 * Controlla che la transazione principale abbia un determinato esito e determinate consegne da fare
	 */
	public static void checkStatoConsegna(HttpResponse response, int esito, int consegneMultipleRimanenti) {
		
		// Provo 5 tentativi
		int tentativi = 5;
		int index = 0;
		Integer count = null;
		
		while(index<tentativi) {
			
			String query = "select count(*) from transazioni where id=? and esito = ? and consegne_multiple = ?";
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
			ConfigLoader.getLoggerCore().info("Checking stato consegna for transazione:  " + id_transazione + " AND esito = " + esito + " AND consegne-rimanenti: " + consegneMultipleRimanenti);
			count = ConfigLoader.getDbUtils().readValue(query, Integer.class,  id_transazione, esito, consegneMultipleRimanenti);
			
			// debug for error
			if(count.intValue() != 1) {
				ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa:");
				String query2 = "select esito, esito_sincrono, consegne_multiple from transazioni where id = ?";
				List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione);
				int row = 0;
				for (Map<String, Object> map : letto) {
					if(map!=null && !map.isEmpty()) {
						for (Map.Entry<String,Object> entry : map.entrySet()) {
							if(entry!=null) {
								ConfigLoader.getLoggerCore().error("Entry["+row+"] key["+entry.getKey()+"]=["+entry.getValue()+"]");
							}
							else {
								ConfigLoader.getLoggerCore().error("Entry["+row+"] null");
							}
						}
					}
					else {
						ConfigLoader.getLoggerCore().error("Entry["+row+"] empty");
					}
					row++;
				}
				
				index++;
				Utilities.sleep(1000);
				continue;
			}
			
			break;
			
		}
		
		assertEquals(Integer.valueOf(1), count);
		
	}

	
	/**
	 * Controlla che non sia partita nessuna consegna multipla, l'esito della transazione non deve essere uno delle
	 * consegne asincrone e l'esito sincrono è fermo a zero.
	 * @param response
	 */
	public static void checkNessunaNotifica(HttpResponse response) {
		String esitiConsegnaMultipla = "( 38, 39, 40, 48 )";
		String query = "select count(*) from transazioni where id=? and esito not in "+esitiConsegnaMultipla+" and esito_sincrono = 0 and consegne_multiple = 0";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		ConfigLoader.getLoggerCore().info("Checking nessuna consegna for transazione:  " + id_transazione);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class,  id_transazione);
		
		// debug for error
		if(count.intValue() != 1) {
			ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa:");
			String query2 = "select esito, esito_sincrono, consegne_multiple from transazioni where id = ?";
			List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione);
			for (var v : letto) {
				ConfigLoader.getLoggerCore().error(v.toString());
			}
		}
		
		assertEquals(Integer.valueOf(1), count);
	}
	
	
	public static void checkNessunoScheduling(HttpResponse response) {
		String query = "select count(*) from transazioni_sa where id_transazione=?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		
		ConfigLoader.getLoggerCore().info("Checking nessuno scheduling connettore in corso for transazione:  " + id_transazione);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione);
		
		// debug for error
		if(count.intValue() != 0) {
			ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa:");
			String query2 = "select connettore_nome from transazioni_sa where id_transazione = ?";
			List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione);
			for (var v : letto) {
				ConfigLoader.getLoggerCore().error(v.toString());
			}
		}
		
		assertEquals(Integer.valueOf(0), count);
	}

	
	public  static void checkSchedulingConnettoreIniziato(HttpResponse response, String connettore) {
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and numero_tentativi = 0 and identificativo_messaggio is not null";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		ConfigLoader.getLoggerCore().info("Checking scheduling connettore iniziato for transazione:  " + id_transazione + " AND connettore = " + connettore);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false);
		
		// debug for error
		if(count.intValue()!=1) {
			ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa:");
			String query2 = "select consegna_terminata,numero_tentativi,identificativo_messaggio from transazioni_sa where id_transazione=? and connettore_nome = ?";
			List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
			for (var v : letto) {
				ConfigLoader.getLoggerCore().error(v.toString());
			}
		}
		
		assertEquals(Integer.valueOf(1), count);
	}
	
	
	public static void checkSchedulingConnettoreInCorso(HttpResponse response, String connettore, int esitoConsegna, int _statusCode, String fault, String formatoFault) {
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		String statusCode = String.valueOf(_statusCode);

		ConfigLoader.getLoggerCore().info("Checking scheduling connettore in corso for transazione:  " + id_transazione
				+ " AND connettore = " + connettore + " AND dettaglio_esito = " + esitoConsegna + " fault = " + fault + " AND formato_fault= " + formatoFault + " AND codice_risposta=" + statusCode);

		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and numero_tentativi >=1 and dettaglio_esito = ? and codice_risposta = ? and identificativo_messaggio is not null";
		
		Integer count;
		if (fault.isEmpty()) {
			query += " and fault is null and formato_fault is null";
			count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false, esitoConsegna,statusCode);
		} else {
			query += " and fault LIKE '"+fault+"' and formato_fault = ?";
			count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false, esitoConsegna,statusCode, formatoFault);
		}
		
		// debug for error
		if(count.intValue()!=1) {
			ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa:");
			String query2 = "select consegna_terminata,numero_tentativi,dettaglio_esito,codice_risposta,identificativo_messaggio,fault,formato_fault from transazioni_sa where id_transazione=? and connettore_nome = ?";
			List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
			for (var v : letto) {
				ConfigLoader.getLoggerCore().error(v.toString());
			}
		}
		
		assertEquals(Integer.valueOf(1), count);
	}

	
	/**
	 * Il numero tentativi è a uno perchè per i connettori per cui si completa la richiesta, la si completa
	 * sempre al primo tentativo.
	 * 
	 */
	public static void checkSchedulingConnettoreCompletato(HttpResponse response, String connettore, int esitoConsegna, int _statusCode, String fault, String formatoFault) {
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		String statusCode = String.valueOf(_statusCode);
		
		ConfigLoader.getLoggerCore().info("Checking scheduling connettore completato for transazione:  "
				+ id_transazione + " AND connettore = " + connettore + " AND dettaglio_esito = " + esitoConsegna+ " fault = " + fault + " AND formato_fault= " + formatoFault + " AND codice_risposta=" + statusCode);
		
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and numero_tentativi = 1 and dettaglio_esito = ? and codice_risposta = ? and identificativo_messaggio is not null";
		
		Integer count;
		if (fault.isEmpty()) {
			query += " and fault is null and formato_fault is null";
			count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, true, esitoConsegna, statusCode);
		} else {
			query += " and fault LIKE '"+fault+"' and formato_fault = ?";
			count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, true, esitoConsegna, statusCode, formatoFault);
		}
		
		// debug for error
		if(count.intValue()!=1) {
			ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa:");
			String query2 = "select consegna_terminata,numero_tentativi,dettaglio_esito,codice_risposta,identificativo_messaggio,fault,formato_fault from transazioni_sa where id_transazione=? and connettore_nome = ?";
			List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
			for (var v : letto) {
				ConfigLoader.getLoggerCore().error(v.toString());
			}
		}
		
		assertEquals(Integer.valueOf(1), count);	
		
	}

	
	public static  int getNumeroTentativiSchedulingConnettore(HttpResponse response, String connettore) {
		String query = "select numero_tentativi from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		
		return ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false);
	}
	
	public static boolean is5XX(int statusCode) {
		return statusCode >= 500 && statusCode <= 599;
	}
	
	public static boolean is4XX(int statusCode) {
		return statusCode >= 400 && statusCode <= 499;
	}
	
	public static boolean is3XX(int statusCode) {
		return statusCode >= 300 && statusCode <= 399;
	}
	
	public static boolean isEsitoErrore(int statusCode) {
		return is5XX(statusCode) || is4XX(statusCode) || is3XX(statusCode);
	}
	
	public static void checkRequestExpectations(RequestAndExpectations requestAndExpectation, HttpResponse response) {
		checkRequestExpectations(requestAndExpectation, response, Set.of());
	}
	
	/**
	 * Controlla che la richiesta/rispsta soddisfi le condizioni attese.
	 * Per i connettori falliti controllo che lo scheduling sia ancora in corso e per i connettori con successo
	 * controllo che lo scheduling sia completato.
	 * 
	 * @param requestAndExpectation
	 * @param response
	 */
	public static void checkRequestExpectations(RequestAndExpectations requestAndExpectation, HttpResponse response, Set<String> connettoriFile) {
		String fault = "";
		String formatoFault = "";

		// Determino il formato e tipo fault, brutto, con un refactoring dei test si sistema
		if (requestAndExpectation instanceof RequestAndExpectationsFault) {
			fault = ((RequestAndExpectationsFault) requestAndExpectation).faultMessage;
			formatoFault = ((RequestAndExpectationsFault) requestAndExpectation).faultType;
		} else {
			switch (requestAndExpectation.tipoFault) {
			case REST:
				fault = FAULT_REST;
				formatoFault = FORMATO_FAULT_REST;
			break;
			case SOAP1_1:
				fault = FAULT_SOAP1_1;
				formatoFault = FORMATO_FAULT_SOAP1_1;
			break;
			case SOAP1_2:
				fault = FAULT_SOAP1_2;
				formatoFault = FORMATO_FAULT_SOAP1_2;
			break;
			default:
				break;
			}
		}
		
		for (var connettore : requestAndExpectation.connettoriFallimento) {
			int esitoNotifica;
			if (requestAndExpectation.tipoFault != TipoFault.NESSUNO) {
				esitoNotifica = ESITO_ERRORE_APPLICATIVO;				
			} else if (isEsitoErrore(requestAndExpectation.statusCodePrincipale)) {			
				esitoNotifica = ESITO_ERRORE_INVOCAZIONE;
			} else {
				esitoNotifica = esitoConsegnaFromStatusCode(requestAndExpectation.statusCodePrincipale);
			}
			checkSchedulingConnettoreInCorso(response, connettore, esitoNotifica,requestAndExpectation.statusCodePrincipale,fault, formatoFault);
		}
				
		for (var connettore : requestAndExpectation.connettoriSuccesso) {
			int esitoNotifica;
			if (connettoriFile.contains(connettore)) {
				esitoNotifica = ESITO_OK;
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica,200,"", "");
			} else	if (requestAndExpectation.tipoFault != TipoFault.NESSUNO) {
				esitoNotifica = ESITO_ERRORE_APPLICATIVO; 
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica, requestAndExpectation.statusCodePrincipale,fault, formatoFault);
			} else {
				esitoNotifica =  esitoConsegnaFromStatusCode(requestAndExpectation.statusCodePrincipale);
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica, requestAndExpectation.statusCodePrincipale,fault, formatoFault);
			}
		}
		
		Set<String> wholeConnettori = setSum(requestAndExpectation.connettoriSuccesso,requestAndExpectation.connettoriFallimento);
		checkConnettoriRaggiuntiEsclusivamente(response, wholeConnettori);
		
		checkStatoConsegna(response,requestAndExpectation.esitoPrincipale, requestAndExpectation.connettoriFallimento.size());
	}

	
	public static void checkRequestExpectationsFinal(RequestAndExpectations requestAndExpectation, HttpResponse response) {
		checkRequestExpectationsFinal(requestAndExpectation, response, Set.of());
	}

	
	public static void checkRequestExpectationsFinal(RequestAndExpectations requestAndExpectation, HttpResponse response, Set<String> connettoriFile) {
		String fault = "";
		String formatoFault = "";
		
		// Determino il formato e tipo fault, brutto, con un refactoring dei test si sistema
		if (requestAndExpectation instanceof RequestAndExpectationsFault) {
			fault = ((RequestAndExpectationsFault) requestAndExpectation).faultMessage;
			formatoFault = ((RequestAndExpectationsFault) requestAndExpectation).faultType;
		} else {
			switch (requestAndExpectation.tipoFault) {
			case REST:
				fault = FAULT_REST;
				formatoFault = FORMATO_FAULT_REST;
			break;
			case SOAP1_1:
				fault = FAULT_SOAP1_1;
				formatoFault = FORMATO_FAULT_SOAP1_1;
			break;
			case SOAP1_2:
				fault = FAULT_SOAP1_2;
				formatoFault = FORMATO_FAULT_SOAP1_2;
			break;
			default:
				break;
			}
		}
		
		for ( var connettore : requestAndExpectation.connettoriFallimento) {
			assertTrue( getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
		}
		
		for (var connettore : requestAndExpectation.connettoriSuccesso) {
			int esitoNotifica;
			if (connettoriFile.contains(connettore)) {
				esitoNotifica = ESITO_OK;
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica, 200, "", "");
			} else if (requestAndExpectation.tipoFault != TipoFault.NESSUNO) {
				esitoNotifica = ESITO_ERRORE_APPLICATIVO;
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica, requestAndExpectation.statusCodePrincipale, fault, formatoFault);
			} else {
				esitoNotifica = esitoConsegnaFromStatusCode(requestAndExpectation.statusCodePrincipale);
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica, requestAndExpectation.statusCodePrincipale, fault, formatoFault);
			}	
		}
		
		Set<String> wholeConnettori = setSum(requestAndExpectation.connettoriSuccesso,requestAndExpectation.connettoriFallimento);
		checkConnettoriRaggiuntiEsclusivamente(response, wholeConnettori);
		
		checkStatoConsegna(response,requestAndExpectation.esitoPrincipale, requestAndExpectation.connettoriFallimento.size());
	}
	
	
	
	
	public static int esitoConsegnaFromStatusCode(int statusCode) {
		if (statusCode >= 200 && statusCode <= 299) {
			return ESITO_OK;
		} else if (statusCode >= 300 && statusCode <= 399) {
			return ESITO_3XX;
		}  else if (statusCode >= 400 && statusCode <= 499) {
			return ESITO_4XX;
		}  else if (statusCode >= 500 && statusCode <= 599) {
			return ESITO_5XX;
		}
		else throw new IllegalArgumentException("Invalid StatusCode " + statusCode);
		
	}


	/**
	 * Costruisce la richiesta e le condizioni attese sull'esito della richiesta.
	 * 
	 * Se la richiesta ha uno status != 2xx e viene accettata da un connettore, lo stato diventa ESITO_CONSEGNA_MULTIPLA_FALLITA
	 * 
	 */
	public static RequestAndExpectations buildRequestAndExpectations(String erogazione, int statusCode, Set<String> connettoriOk, Set<String> connettoriErrore, String soapContentType) {
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		
		int esito;
		
		// Se qualsiasi connettore accetta uno status code non OK, la consegna multipla va segnata come fallita.
		if ( ( statusCode < 200 || statusCode > 299) && !connettoriOk.isEmpty()) {
			esito = ESITO_CONSEGNA_MULTIPLA_FALLITA;
		} else {
			esito =  connettoriErrore.isEmpty() ? ESITO_CONSEGNA_MULTIPLA_COMPLETATA : ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		}
		
		return new RequestAndExpectations(request, connettoriOk, connettoriErrore, esito,  statusCode, true);
	}
	
	/*@Deprecated
	public  static void checkSchedulingConnettoriCompletato(HttpResponse response, Set<String> connettori) {
		checkSchedulingConnettoriCompletato(response,connettori,0, "", "");
	}*/
	
	@Deprecated
	public  static void checkSchedulingConnettoriCompletato(HttpResponse response, Set<String> connettori, int esitoNotifica, String fault, String formatoFault) {
		throw new RuntimeException("NotImplemented");
	}
	
	@Deprecated
	public  static void checkSchedulingConnettoreCompletato(HttpResponse response, String connettore, int esitoNotifica, String fault, String formatoFault) {
		throw new RuntimeException("NotImplemented");
	}
	
	
	public  static void checkSchedulingConnettoriCompletato(HttpResponse response, Set<String> connettori, int esitoNotifica, int statusCode, String fault, String formatoFault) {
		for (var connettore : connettori) {
			checkSchedulingConnettoreCompletato(response, connettore,esitoNotifica, statusCode, fault, formatoFault);			
		}
		
		// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati
		checkConnettoriRaggiuntiEsclusivamente(response, connettori);
	}


	public static void checkSchedulingConnettoreIniziato(HttpResponse response, Set<String> connettori) {	
		for (var connettore : connettori) {
			checkSchedulingConnettoreIniziato(response, connettore);
		}
		
		// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati
		checkConnettoriRaggiuntiEsclusivamente(response, connettori);
	}


	public static void checkConnettoriRaggiuntiEsclusivamente(HttpResponse response, Set<String> connettori) {
		String query = "select connettore_nome  from transazioni_sa where id_transazione=?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
	
		List<Map<String, Object>> connettoriRaggiunti = ConfigLoader.getDbUtils().readRows(query, id_transazione);
		for(var row : connettoriRaggiunti) {
			String connettore = (String) row.get("connettore_nome");
			assertTrue(connettori.contains(connettore));
		}
		
		for (var connettore : connettori) {
			var nomeRaggiunti = connettoriRaggiunti.stream().map( row -> row.get("connettore_nome")).collect(Collectors.toList());
			assertTrue(nomeRaggiunti.contains(connettore));
		}
	}
	
	public static void checkSchedulingConnettoreIniziato(List<HttpResponse> responses, Set<String> connettori) {
		for (var r : responses) {
			checkSchedulingConnettoreIniziato(r, connettori);
		}
	}

	public static RequestAndExpectations buildRequestAndExpectationFiltered(HttpRequest request, int statusCode, Set<String> connettoriSuccesso, Set<String> connettoriFiltrati) {
		connettoriFiltrati = new HashSet<>(connettoriFiltrati);
		connettoriFiltrati.remove(Common.CONNETTORE_DISABILITATO);
		connettoriSuccesso = setIntersection(connettoriSuccesso, connettoriFiltrati);
		connettoriFiltrati.removeAll(connettoriSuccesso);
		Set<String> connettoriFallimento = connettoriFiltrati;
	
		int esito;
		
		if ( ( statusCode < 200 || statusCode > 299) && !connettoriSuccesso.isEmpty()) {
			esito = ESITO_CONSEGNA_MULTIPLA_FALLITA;
		} else if (connettoriSuccesso.isEmpty() && !connettoriFallimento.isEmpty()){
			esito = ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		} else if (connettoriFallimento.isEmpty()) {
			esito = ESITO_CONSEGNA_MULTIPLA_COMPLETATA; 
		} else {
			esito = ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		}
		
		return new RequestAndExpectations(request, connettoriSuccesso, connettoriFallimento, esito, statusCode, true);
	}

	public static void checkResponses(Map<RequestAndExpectations, List<HttpResponse>> responsesByKind) {
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia 
		// su tutti i connettori abilitati e filtrati dalla consegna condizionale
		assertTrue(!responsesByKind.isEmpty());
		
		// Controllo che le richieste siano state consegnate e le notifiche schedulate
		for (var requestAndExpectation : responsesByKind.keySet() ) {
				var responses = responsesByKind.get(requestAndExpectation);
				assertTrue(!responses.isEmpty());
				Set<String> connettoriCoinvolti = setSum(requestAndExpectation.connettoriSuccesso, requestAndExpectation.connettoriFallimento);
				
				if (requestAndExpectation.esitoPrincipale != ESITO_ERRORE_PROCESSAMENTO_PDD_4XX) {
					if (requestAndExpectation.tipoFault != TipoFault.NESSUNO) {
						checkResponsesStatus(responses, 500);
					} else {
						checkAll200(responses);
					}
					CommonConsegnaMultipla.withBackoff( () -> checkSchedulingConnettoreIniziato(responses, connettoriCoinvolti));
					checkStatoConsegna(responses, ESITO_CONSEGNA_MULTIPLA, connettoriCoinvolti.size());
				} else {
					CommonConsegnaMultipla.withBackoff( () -> checkStatoConsegna(responses, ESITO_ERRORE_PROCESSAMENTO_PDD_4XX, 0));
				}
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			if (requestAndExpectation.statusCodePrincipale == 200) {
				for (var response : responsesByKind.get(requestAndExpectation)) {
					checkRequestExpectations(requestAndExpectation, response);
				}
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo le date vengano aggiornate
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			if (requestAndExpectation.statusCodePrincipale == 200) {
				for (var response : responsesByKind.get(requestAndExpectation)) {
					checkRequestExpectationsFinal(requestAndExpectation, response);
				}
			}
		}
	}
	
	
	public static void checkMessageBox(String connettoreMessageBox, MessageBox imMessageBoxPort)
			throws IntegrationManagerException_Exception {
		List<String> ids = imMessageBoxPort.getAllMessagesId();
		assertFalse(ids.isEmpty());
		Logger logger = ConfigLoader.getLoggerCore();
		
		// Recupero il  messaggio, verifico che sia incrementato il contatore dei prelievi e che la data di prelievo sia aggiornata
		String query = "Select data_primo_prelievo_im from transazioni_sa where identificativo_messaggio = ? and connettore_nome = ? and data_primo_prelievo_im >= ? and data_registrazione >= ? and data_prelievo_im = data_primo_prelievo_im and numero_prelievi_im = 1 and data_eliminazione_im is null";
		logger.info("Checking db for idMessaggio: " + ids.get(0));
		IdentificativoIM idAndDate = org.openspcoop2.pdd.services.skeleton.IdentificativoIM.getIdentificativoIM(ids.get(0), logger);

		String idMessaggio = idAndDate.getId();
		Timestamp dataRegistrazioneMessageBox = Timestamp.from(idAndDate.getData().toInstant());
		Timestamp dataRiferimentoTest = Timestamp.from(Instant.now());

		imMessageBoxPort.getMessage(idMessaggio);

		logger.info("Checking stato per connettore messageBox: " + idMessaggio + " " + connettoreMessageBox + " " + dataRiferimentoTest.toString() );
		
		Timestamp dataPrimoPrelievo = ConfigLoader.getDbUtils().readValue(query, Timestamp.class, idMessaggio,	connettoreMessageBox, dataRiferimentoTest, dataRegistrazioneMessageBox); 
		
		// scarico nuovamente il singolo messaggio per verificare aggiornamenti dei contatori e delle data, verificando che sia mantenuta la data di primo prelievo
		query = "Select count(*) from transazioni_sa where identificativo_messaggio = ? and connettore_nome = ? and data_primo_prelievo_im = ? and data_prelievo_im >= ? and numero_prelievi_im = 2 and data_eliminazione_im is null";
		dataRiferimentoTest = Timestamp.from(Instant.now());

		imMessageBoxPort.getMessage(idMessaggio);

		logger.info("Checking secondo prelievo per connettore messageBox: " + idMessaggio + " " + connettoreMessageBox + " " + dataRiferimentoTest.toString() );

		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, idMessaggio,	connettoreMessageBox, dataPrimoPrelievo, dataRiferimentoTest);
		assertEquals(Integer.valueOf(1), count);
				
		// elimino il messaggio, verifico che la data di eliminazione sia valorizzata e la consegna completata.

		dataRiferimentoTest = Timestamp.from(Instant.now());
		imMessageBoxPort.deleteMessage(idMessaggio);
		 
		query = "Select count(*) from transazioni_sa where identificativo_messaggio = ? and connettore_nome = ? and data_primo_prelievo_im = ? and data_eliminazione_im >= ? and  consegna_terminata = ? and numero_prelievi_im = 2";
		count = ConfigLoader.getDbUtils().readValue(query, Integer.class, idMessaggio,connettoreMessageBox, dataPrimoPrelievo, dataRiferimentoTest,true);
		assertEquals(Integer.valueOf(1), count);
	}
	
	
	public static void checkConsegnaTerminataNoStatusCode(HttpResponse response, String connettore) {
		String idTransazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and dettaglio_esito = 0";
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, idTransazione, connettore, true);
		assertEquals(Integer.valueOf(1), count);
	}
	
	

	/** 
	 * Controlla che la richiesta sia stata inoltrata su file dai set di connettori indicati.
	 * 
	 * @param request 				-	La richiesta iniziale
	 * @param responseSync	-	La risposta di presa in carico
	 * @param connettoriFile		-  I connettori dell'erogazione che vanno su file 
	 *
	 */
	public static  void checkConsegnaConnettoreFile(HttpRequest request, HttpResponse responseSync, Set<String> connettoriFile) throws IOException {
		String idTransazione = responseSync.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		List<File> filesCreati = CommonConsegnaMultipla.getFilesCreati(idTransazione);
		
		
		for (var connettore : connettoriFile) {
			String requestFilename = connettore+":"+idTransazione+":request";
			String requestHeadersFilename = connettore+":"+idTransazione+":request-headers";
			
			File requestFile = filesCreati.stream().filter
					( f -> f.getName().equals(requestFilename)).findAny().get();
			
			File requestHeadersFile = filesCreati.stream().filter
					( f -> f.getName().equals(requestHeadersFilename)).findAny().get();
			
			HttpRequest requestConsegna = new HttpRequest();
			requestConsegna.setContent(Files.readAllBytes(requestFile.toPath()));
			
			List<String> content = Files.readAllLines(requestHeadersFile.toPath());
			for (var line : content) {
				String[] header = line.split("=");
				requestConsegna.addHeader(header[0], header[1]);
			}
			
			if (request.getContent() != null) {
				assertTrue(Arrays.equals(request.getContent(), requestConsegna.getContent()));
			}
			assertEquals(idTransazione, requestConsegna.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE));
			
			assertEquals(connettoriFile.size()*2, filesCreati.size());
		}
	}

	public static  List<File> getFilesCreati(String idTransazione) throws IOException {
		return Files.list(connettoriFilePath)
				.filter( file -> !Files.isDirectory(file) && file.toFile().getName().contains(idTransazione) )
				.map(Path::toFile)
				.collect(Collectors.toList());
	}

	public static void jmxAbilitaSchedulingConnettore(String erogazione, String connettore) throws UtilsException, HttpUtilsException {
	    	org.slf4j.Logger logger = ConfigLoader.getLoggerCore();
	    	logger.debug("Abilito connettore: " + connettore+ " per erogazione: "+ erogazione);
	    	
	        String jmx_user = System.getProperty("jmx_username");
	        String jmx_pass = System.getProperty("jmx_password"); 
	        
	        String nomePorta = "gw_SoggettoInternoTest/gw_"+erogazione+"/v1";
	
	        String url =  System.getProperty("govway_base_path") + "/check?resourceName=ConfigurazionePdD&methodName=enableSchedulingConnettoreMultiplo&paramValue="+nomePorta+"&paramValue2="+connettore;
	        org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmx_user, jmx_pass);
	        
	        url =  System.getProperty("govway_base_path") + "/check?resourceName=ConfigurazionePdD&methodName=enableSchedulingConnettoreMultiploRuntimeRepository&paramValue="+nomePorta+"&paramValue2="+connettore;
	        org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmx_user, jmx_pass);
	}

	public static void jmxDisabilitaSchedulingConnettore(String erogazione, String connettore) throws UtilsException, HttpUtilsException {
		org.slf4j.Logger logger = ConfigLoader.getLoggerCore();
		logger.debug("Disabilito connettore: " + connettore+ " per erogazione: "+ erogazione);
	
	    String jmx_user = System.getProperty("jmx_username");
	    String jmx_pass = System.getProperty("jmx_password"); 
	    
	    String nomePorta = "gw_SoggettoInternoTest/gw_"+erogazione+"/v1";
	    
	    String url =  System.getProperty("govway_base_path") + "/check?resourceName=ConfigurazionePdD&methodName=disableSchedulingConnettoreMultiplo&paramValue="+nomePorta+"&paramValue2="+connettore;
	    org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmx_user, jmx_pass);
	    
	    url =  System.getProperty("govway_base_path") + "/check?resourceName=ConfigurazionePdD&methodName=disableSchedulingConnettoreMultiploRuntimeRepository&paramValue="+nomePorta+"&paramValue2="+connettore;
	    org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmx_user, jmx_pass);
		
	}

	public static Map<String,java.sql.Timestamp> waitPrimaConsegna(Map<RequestAndExpectations, List<HttpResponse>> responsesByKind) {
		// Attendo la prima consegna di tutti i connettori
		
		printAttesaAttiva("@waitPrimaConsegna ENTRO");
		
		Map<String,java.sql.Timestamp> map = new HashMap<String, Timestamp>();
		
		int connettoriDaVerificare = 0;
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			if (requestExpectation.principaleSuperata) {
				for (@SuppressWarnings("unused") var response : responses) {
					for (@SuppressWarnings("unused") var connettore : CommonConsegnaMultipla.setSum(requestExpectation.connettoriFallimento,requestExpectation.connettoriSuccesso)) {
						connettoriDaVerificare++;
						String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
						printAttesaAttiva("@waitPrimaConsegna TROVATO connettore["+connettore+"] response["+response.getResultHTTPOperation()+"] ["+id_transazione+"]");
					}
				}
			}
		}
		printAttesaAttiva("@waitPrimaConsegna WAIT CONNETTORI ["+connettoriDaVerificare+"]");
		
		int LIMITE = (3 * CommonConsegnaMultipla.scheduleNewAfter);
		
		int index = 0;
		while(index<LIMITE) {
			
			boolean sleep = false;
			
			printAttesaAttiva("@waitPrimaConsegna ITERO ["+connettoriDaVerificare+"] ["+index+"]<["+LIMITE+"]");
			
			for (var requestExpectation : responsesByKind.keySet()) {
				var responses = responsesByKind.get(requestExpectation);
	
				if (requestExpectation.principaleSuperata) {
					for (var response : responses) {
						for (var connettore : CommonConsegnaMultipla.setSum(requestExpectation.connettoriFallimento,requestExpectation.connettoriSuccesso)) {
							String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
							String queryData = "select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? AND data_uscita_richiesta is not null AND numero_tentativi>0";
							String key = buildKey(response, connettore, id_transazione);
							
							Integer count = ConfigLoader.getDbUtils().readValue(queryData, Integer.class, id_transazione, connettore);
							org.openspcoop2.utils.Utilities.sleep(50);
							if(count!=null && count.intValue() == 1) {
								queryData = "select data_uscita_richiesta from transazioni_sa where id_transazione = ? and connettore_nome = ? AND data_uscita_richiesta is not null AND numero_tentativi>0";
								//getLoggerCore().info("Verifico data");
								java.sql.Timestamp t = ConfigLoader.getDbUtils().readValue(queryData, java.sql.Timestamp.class, id_transazione, connettore);
								
								map.put(key, t);
								printAttesaAttiva("@waitPrimaConsegna SIZE ["+map.size()+"] ["+map.keySet()+"]");
								if(map.size()==connettoriDaVerificare) {
									printAttesaAttiva("@waitPrimaConsegna RETURN");
									return map;
								}
							}
							else {
								if(attesaAttivaDebugLogger || attesaAttivaDebugSystemOut) {
									String queryDataTest = "select data_uscita_richiesta,numero_tentativi from transazioni_sa where id_transazione = ? and connettore_nome = ?";
									List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(queryDataTest, id_transazione, connettore);
									for (var v : letto) {
										printAttesaAttiva("@waitPrimaConsegna Analisi ["+key+"]");
										printAttesaAttiva("@waitPrimaConsegna risultato: "+v.toString());
									}
								}
								
								sleep = true;
								org.openspcoop2.utils.Utilities.sleep(1000);
								index=index+1000;
								break;
							}
						}
						if(sleep) {
							break;
						}
					}
					if(sleep) {
						break;
					}
				}
				if(sleep) {
					break;
				}
			}
			
			if(!sleep) {
				index=index+1000;
			}
		}
		
		printAttesaAttivaError("@waitPrimaConsegna ESCO SENZA TROVARE NULLA");
		throw new RuntimeException("Dopo il tempo di attesa indicato, non ho riscontrato la prima consegna per tutti i "+connettoriDaVerificare+" connettori attesi; riscontrati solamente "+map.size()+" connettori map["+map.keySet()+"]");
	}
	
	public static Map<String,java.sql.Timestamp> waitProssimaConsegna(Map<RequestAndExpectations, List<HttpResponse>> responsesByKind, Map<String,java.sql.Timestamp> precedenteConsegnaMap,
			String ... connettoriEsclusiControllo) {
		// Attendo la prossima consegna su tutti i connettori

		printAttesaAttiva("@waitProssimaConsegna ENTRO ");
		
		Map<String,java.sql.Timestamp> map = new HashMap<String, Timestamp>();
		
		int connettoriDaVerificare = 0;
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			if (requestExpectation.principaleSuperata) {
				for (@SuppressWarnings("unused") var response : responses) {
					for (var connettore : requestExpectation.connettoriFallimento) {
						if(connettoriEsclusiControllo!=null && connettoriEsclusiControllo.length>0) {
							boolean find = false;
							for (String c : connettoriEsclusiControllo) {
								if(c.equals(connettore)) {
									find = true;
									break;
								}
							}
							if(find) {
								printAttesaAttiva("@waitProssimaConsegna SKIP connettore["+connettore+"]");
								continue;
							}
						}
						connettoriDaVerificare++;
						String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
						printAttesaAttiva("@waitProssimaConsegna TROVATO connettore["+connettore+"] response["+response.getResultHTTPOperation()+"] ["+id_transazione+"]");
					}
				}
			}
		}
		printAttesaAttiva("@waitProssimaConsegna WAIT CONNETTORI ["+connettoriDaVerificare+"]");
		if(connettoriDaVerificare==0) {
			printAttesaAttiva("@waitProssimaConsegna nessun connettore da rispedire rilevato, e quindi non è necessaria alcuna attesa");
			return null;
		}
		
		int LIMITE = (3 * CommonConsegnaMultipla.getIntervalloControlloFallite());
		
		int index = 0;
		while(index<LIMITE) {

			boolean sleep = false;
			
			printAttesaAttiva("@waitProssimaConsegna ITERO ["+connettoriDaVerificare+"] ["+index+"]<["+LIMITE+"]");
			
			for (var requestExpectation : responsesByKind.keySet()) {
				var responses = responsesByKind.get(requestExpectation);
	
				if (requestExpectation.principaleSuperata) {
					for (var response : responses) {
						for (var connettore : requestExpectation.connettoriFallimento) {
							
							if(connettoriEsclusiControllo!=null && connettoriEsclusiControllo.length>0) {
								boolean find = false;
								for (String c : connettoriEsclusiControllo) {
									if(c.equals(connettore)) {
										find = true;
										break;
									}
								}
								if(find) {
									printAttesaAttiva("SKIP connettore["+connettore+"]");
									continue;
								}
							}
							
							
							String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
							String queryData = "select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? AND data_uscita_richiesta>? AND numero_tentativi>1";
							
							String key = buildKey(response, connettore, id_transazione);
							
							java.sql.Timestamp precedenteConsegna = precedenteConsegnaMap.get(key); 
							Integer count = ConfigLoader.getDbUtils().readValue(queryData, Integer.class, id_transazione, connettore, precedenteConsegna);
							org.openspcoop2.utils.Utilities.sleep(50);
							if(count!=null && count.intValue() == 1) {
								queryData = "select data_uscita_richiesta from transazioni_sa where id_transazione = ? and connettore_nome = ? AND data_uscita_richiesta>? AND numero_tentativi>1";
								//getLoggerCore().info("Verifico data");
								java.sql.Timestamp t = ConfigLoader.getDbUtils().readValue(queryData, java.sql.Timestamp.class, id_transazione, connettore, precedenteConsegna);

								map.put(key, t);
								printAttesaAttiva("@waitProssimaConsegna SIZE ["+map.size()+"] ["+map.keySet()+"]");
								if(map.size()==connettoriDaVerificare) {
									printAttesaAttiva("@waitProssimaConsegna RETURN");
									return map;
								}
							}
							else {
								
								if(attesaAttivaDebugLogger || attesaAttivaDebugSystemOut) {
									String queryDataTest = "select data_uscita_richiesta,numero_tentativi from transazioni_sa where id_transazione = ? and connettore_nome = ?";
									List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(queryDataTest, id_transazione, connettore);
									for (var v : letto) {
										printAttesaAttiva("@waitProssimaConsegna Analisi ["+key+"]");
										printAttesaAttiva("@waitProssimaConsegna risultato: "+v.toString());
									}
								}
								
								sleep = true;
								org.openspcoop2.utils.Utilities.sleep(1000);
								index=index+1000;
								break;
							}
						}
						if(sleep) {
							break;
						}
					}
					if(sleep) {
						break;
					}
				}
				if(sleep) {
					break;
				}
			}
			
			if(!sleep) {
				index=index+1000;
			}
		}
		
		printAttesaAttivaError("@waitProssimaConsegna ESCO SENZA TROVARE NULLA");
		throw new RuntimeException("Dopo il tempo di attesa indicato, non ho riscontrato la prossima consegna per tutti i "+connettoriDaVerificare+" connettori attesi; riscontrati solamente "+map.size()+" connettori map["+map.keySet()+"]");
	}

	public static Map<String,java.sql.Timestamp> waitConsegna(List<HttpResponse> responses, List<String> connettori) {
		// Attendo la prima consegna di tutti i connettori
		
		printAttesaAttiva("@waitConsegna ENTRO");
		
		Map<String,java.sql.Timestamp> map = new HashMap<String, Timestamp>();
		
		int connettoriDaVerificare = 0;
		for (int i = 0; i < responses.size(); i++) {
			HttpResponse response = responses.get(i);
			for (int j = 0; j < connettori.size(); j++) {
				String connettore = connettori.get(j);
				connettoriDaVerificare++;
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				printAttesaAttiva("@waitConsegna TROVATO connettore["+connettore+"] response["+response.getResultHTTPOperation()+"] ["+id_transazione+"]");
			}
		}
		printAttesaAttiva("@waitConsegna WAIT CONNETTORI ["+connettoriDaVerificare+"]");
		
		int LIMITE = (3 * CommonConsegnaMultipla.intervalloControllo);
		
		int index = 0;
		while(index<(3 * CommonConsegnaMultipla.intervalloControllo)) {
			
			boolean sleep = false;
			
			printAttesaAttiva("@waitConsegna ITERO ["+connettoriDaVerificare+"] ["+index+"]<["+LIMITE+"]");
			
			for (int i = 0; i < responses.size(); i++) {
				HttpResponse response = responses.get(i);
				for (int j = 0; j < connettori.size(); j++) {
					String connettore = connettori.get(j);
					String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
					String key = buildKey(response, connettore, id_transazione);					
					
					String queryData = "select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? AND data_uscita_richiesta is not null AND numero_tentativi>0";
					Integer count = ConfigLoader.getDbUtils().readValue(queryData, Integer.class, id_transazione, connettore);
					org.openspcoop2.utils.Utilities.sleep(50);
					if(count!=null && count.intValue() == 1) {
						queryData = "select data_uscita_richiesta from transazioni_sa where id_transazione = ? and connettore_nome = ? AND data_uscita_richiesta is not null AND numero_tentativi>0";
						//getLoggerCore().info("Verifico data");
						java.sql.Timestamp t = ConfigLoader.getDbUtils().readValue(queryData, java.sql.Timestamp.class, id_transazione, connettore);
						
						map.put(key, t);
						printAttesaAttiva("@waitConsegna SIZE ["+map.size()+"] ["+map.keySet()+"]");
						if(map.size()==connettoriDaVerificare) {
							printAttesaAttiva("@waitConsegna RETURN");
							org.openspcoop2.utils.Utilities.sleep(1000); // 1 ulteriore
							return map;
						}
					}
					else {
						
						if(attesaAttivaDebugLogger || attesaAttivaDebugSystemOut) {
							String queryDataTest = "select data_uscita_richiesta,numero_tentativi from transazioni_sa where id_transazione = ? and connettore_nome = ?";
							List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(queryDataTest, id_transazione, connettore);
							for (var v : letto) {
								printAttesaAttiva("@waitConsegna Analisi ["+key+"]");
								printAttesaAttiva("@waitConsegna risultato: "+v.toString());
							}
						}
						
						sleep = true;
						org.openspcoop2.utils.Utilities.sleep(1000);
						index=index+1000;
						break;
					}
				}
				if(sleep) {
					break;
				}
			}
			
			if(!sleep) {
				index=index+1000;
			}
		}
		
		printAttesaAttivaError("@waitConsegna ESCO SENZA TROVARE NULLA");
		throw new RuntimeException("Dopo il tempo di attesa indicato, non ho riscontrato la consegna per tutti i "+connettoriDaVerificare+" connettori attesi; riscontrati solamente "+map.size()+" connettori map["+map.keySet()+"]");
	}
	
	public static String buildKey(org.openspcoop2.utils.transport.http.HttpResponse response, String connettore, String id_transazione) {
		String key = response.getResultHTTPOperation()+"_"+connettore+"_"+id_transazione;
		return key;
	}
	
	private static void printAttesaAttiva(String msg) {
		if(attesaAttivaDebugSystemOut) { 
			System.out.println(msg);
		}
		if(attesaAttivaDebugLogger) {
			ConfigLoader.getLoggerCore().debug(msg);	
		}
	}
	private static void printAttesaAttivaError(String msg) {
		if(attesaAttivaDebugSystemOut) { 
			System.err.println(msg);
		}
		if(attesaAttivaDebugLogger) {
			ConfigLoader.getLoggerCore().error(msg);	
		}
	}
}
