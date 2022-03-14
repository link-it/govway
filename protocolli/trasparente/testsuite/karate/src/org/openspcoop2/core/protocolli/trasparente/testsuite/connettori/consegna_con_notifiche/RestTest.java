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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_con_notifiche;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_0;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_ROTTO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_COMPLETATA;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_FALLITA;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_ERRORE_APPLICATIVO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_OK;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.FORMATO_FAULT_REST;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkNessunaNotifica;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkNessunoScheduling;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkPresaInConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreIniziato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkStatoConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.esitoConsegnaFromStatusCode;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.getNumeroTentativiSchedulingConnettore;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.isEsitoErrore;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setDifference;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations.TipoFault;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectationsFault;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilsException;

/**
 * RestTest
 * 
 * @author Francesco Scarlato
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RestTest extends ConfigLoader{

	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if(!cartellaRisposte.exists()) {
			cartellaRisposte.mkdir();
		}
		if (!cartellaRisposte.isDirectory()|| !cartellaRisposte.canWrite()) {
			throw new RuntimeException("E' necessario creare la cartella per scrivere le richieste dei connettori, indicata dalla poprietà: <connettori.consegna_multipla.connettore_file.path> ");
		}
	}
	
	@AfterClass
	public static void After() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if(cartellaRisposte.exists() && cartellaRisposte.isDirectory() && cartellaRisposte.canWrite()) {
			FileSystemUtilities.emptyDir(cartellaRisposte);
		}
	}

	@org.junit.After
	public void AfterEach() {
		Common.fermaRiconsegne(dbUtils);
	}
	
	
	public static RequestAndExpectations buildRequestAndExpectations(String erogazione, int statusCode, Set<String> connettoriOk, Set<String> connettoriErrore) {
		HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		
		int esito;
		// Se qualsiasi connettore accetta uno status code non 2xx, la consegna multipla va segnata come fallita.
		if ( ( statusCode < 200 || statusCode > 299) && !connettoriOk.isEmpty()) {
			esito = ESITO_CONSEGNA_MULTIPLA_FALLITA;
		} else {
			esito =  connettoriErrore.isEmpty() ? ESITO_CONSEGNA_MULTIPLA_COMPLETATA : ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		}
		
		return new RequestAndExpectations(request, connettoriOk, connettoriErrore, esito, statusCode, true);
	}
	
	@Test
	public void varieCombinazioniDiRegole2xx_4xx() {
		varieCombinazioniDiRegole(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	
	@Test
	public void varieCombinazioniDiRegole3xx_5xx() {
		varieCombinazioniDiRegole(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}
	
	@Test
	public void valorizzazioneCampiAvanzata() throws IOException {
		/* Qui vengono testati i vari campi e la rivalorizzazione delle date. 
		 * 
		 * data_registrazione
		 * data_accettazione_richiesta     
 			data_uscita_richiesta           
 			data_accettazione_risposta      
 			data_ingresso_risposta         
		    data_primo_tentativo          
 			data_ultimo_errore              
 			dettaglio_esito_ultimo_errore   
 			codice_risposta_ultimo_errore  
 			ultimo_errore  
 			location_ultimo_errore  
 			fault_ultimo_errore        
 			formato_fault_ultimo_errore                    
		 */

		final String erogazione = "TestConsegnaConNotificheRestVarieCombinazioniDiRegole";
		
		// seguo la statusCode2xxVsConnettori, mando solo 2xx così  la principale supera sempre e controllo
		// i campi sulle notifiche
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		for (var entry : CommonConsegnaMultipla.statusCode2xxVsConnettori.entrySet()) {
			int statusCode = entry.getKey();
			var connettoriSuccesso = entry.getValue();
			var connettoriErrore = setDifference(Common.setConnettoriAbilitati,connettoriSuccesso);
			var requestExpectation = buildRequestAndExpectations(erogazione, statusCode, connettoriSuccesso, connettoriErrore);
			
			requestsByKind.add(requestExpectation);
		}
		
		HttpRequest requestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestsByKind.add(new RequestAndExpectations(
				requestProblem,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.REST ));
		
		Timestamp dataRiferimentoTest = java.sql.Timestamp.from(Instant.now());
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size()) );
				checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
			} else {
				for (var response : responses) {
					CommonConsegnaMultipla.withBackoff( () -> checkNessunaNotifica(response));
					checkNessunoScheduling(response);
				}
			}
		}
				
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);

			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);

					for (var connettore : requestExpectation.connettoriFallimento) {
						String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);

						String query = "Select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ?"
								+ " and data_uscita_richiesta > ? and data_registrazione > ? and data_ingresso_risposta >= data_uscita_richiesta"
								+ " and data_ingresso_risposta >= data_accettazione_risposta and data_uscita_richiesta = data_primo_tentativo"
								+ " and cluster_id_in_coda = 'IDGW' and cluster_id_consegna = 'IDGW' ";
						getLoggerCore().info("Checking date per connettori fallimento: " + id_transazione + " " + connettore + " " + dataRiferimentoTest.toString() );
						Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione,	connettore, dataRiferimentoTest, dataRiferimentoTest);
						
						// debug for error
						if(count.intValue() != 1) {
							ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa (dataRiferimentoTest:"+dataRiferimentoTest+"):");
							String query2 = "select data_uscita_richiesta, data_registrazione, data_ingresso_risposta, data_accettazione_risposta, data_primo_tentativo,cluster_id_in_coda,cluster_id_consegna from transazioni_sa where id_transazione = ? and connettore_nome = ?";
							List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
							for (var v : letto) {
								ConfigLoader.getLoggerCore().error(v.toString());
							}
						}
						
						assertEquals(Integer.valueOf(1), count);
						
						String fault 		 = "";
						String formatoFault = "";
						String ultimoErrore;
						String locationUltimoErrore; 

						if (requestExpectation.tipoFault == TipoFault.REST) {
							fault 		 = CommonConsegnaMultipla.FAULT_REST;
							formatoFault = CommonConsegnaMultipla.FORMATO_FAULT_REST;
							ultimoErrore = "Consegna [http] con errore: errore applicativo, {\"type\":\"https://httpstatuses.com/500\",\"title\":\"Internal Server Error\",\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
							locationUltimoErrore = "http://localhost:8080/TestService/echo?returnCode="+requestExpectation.statusCodePrincipale+"&problem=true&replyPrefixQueryParameter=GovWay-TestSuite-&id_connettore="+connettore+"&replyQueryParameter=id_connettore";
						} else {
							ultimoErrore =  "Consegna [http] con errore: errore HTTP " + requestExpectation.statusCodePrincipale;
							locationUltimoErrore = "http://localhost:8080/TestService/echo?returnCode="+requestExpectation.statusCodePrincipale+"&replyPrefixQueryParameter=GovWay-TestSuite-&id_connettore="+connettore+"&replyQueryParameter=id_connettore";
						}

						query = "Select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? and data_ultimo_errore > ? and dettaglio_esito_ultimo_errore = ? and codice_risposta_ultimo_errore = ? and ultimo_errore LIKE '"+ultimoErrore+"' and location_ultimo_errore LIKE '"+locationUltimoErrore+"'";
						
						int esitoNotifica;
						if (requestExpectation.tipoFault != TipoFault.NESSUNO) {
							esitoNotifica = ESITO_ERRORE_APPLICATIVO;				
						} else if (isEsitoErrore(requestExpectation.statusCodePrincipale)) {			
							esitoNotifica = ESITO_ERRORE_INVOCAZIONE;
						} else {
							esitoNotifica = esitoConsegnaFromStatusCode(requestExpectation.statusCodePrincipale);
						}
						String statusCode = String.valueOf(requestExpectation.statusCodePrincipale);
						
						getLoggerCore().info("Checking info errori date per connettori fallimento: " + id_transazione + " " + connettore + " " + dataRiferimentoTest.toString() + " " + esitoNotifica + " " + statusCode + " " + ultimoErrore + " " + locationUltimoErrore );
						if (fault.isEmpty() ) {
							query += " and fault_ultimo_errore is null and formato_fault_ultimo_errore is null";
							count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, dataRiferimentoTest, esitoNotifica, statusCode);
						} else {
							getLoggerCore().info("QUI");
							query += " and fault_ultimo_errore LIKE '"+fault+"' and formato_fault_ultimo_errore = ? and cluster_id_ultimo_errore = 'IDGW'";
							count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, dataRiferimentoTest, esitoNotifica, statusCode, formatoFault);
						}
						
						// debug for error
						if(count.intValue() != 1) {
							ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa:");
							String query2 = "select data_ultimo_errore, dettaglio_esito_ultimo_errore, codice_risposta_ultimo_errore, ultimo_errore, location_ultimo_errore,fault_ultimo_errore,formato_fault_ultimo_errore,cluster_id_ultimo_errore from transazioni_sa where id_transazione = ? and connettore_nome = ?";
							List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
							for (var v : letto) {
								ConfigLoader.getLoggerCore().error(v.toString());
							}
						}
						
						assertEquals(Integer.valueOf(1), count);
					}

					for (var connettore : requestExpectation.connettoriSuccesso) {
						
						String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
						String query = "Select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ?"
								+ " and data_uscita_richiesta > ? and data_registrazione > ? and data_ingresso_risposta >= data_uscita_richiesta"
								+ " and data_ingresso_risposta >= data_accettazione_risposta and data_uscita_richiesta = data_primo_tentativo"
								+ " and data_ultimo_errore is null and dettaglio_esito_ultimo_errore = 0 and codice_risposta_ultimo_errore is null and ultimo_errore is null and location_ultimo_errore  is null"
								+ " and data_uscita_richiesta >= data_accettazione_richiesta ";
																																		
						getLoggerCore().info("Checking date per connettori successo: " + id_transazione + " " + connettore + " " + dataRiferimentoTest.toString() );
						Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione,	connettore, dataRiferimentoTest, dataRiferimentoTest);
						
						// debug for error
						if(count.intValue() != 1) {
							ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa (dataRiferimentoTest:"+dataRiferimentoTest+"):");
							String query2 = "select data_accettazione_richiesta, data_uscita_richiesta, data_registrazione, data_ingresso_risposta, data_accettazione_risposta, data_primo_tentativo, data_ultimo_errore, dettaglio_esito_ultimo_errore, codice_risposta_ultimo_errore, ultimo_errore, location_ultimo_errore from transazioni_sa where id_transazione = ? and connettore_nome = ?";
							List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
							for (var v : letto) {
								ConfigLoader.getLoggerCore().error(v.toString());
							}
						}
						
						assertEquals(Integer.valueOf(1), count);
					}

				}
			}
		}

		// Attendo l'intervallo di riconsegna e controllo le date vengano aggiornate
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);

			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectationsFinal(requestExpectation, response);
					
					for (var connettore : requestExpectation.connettoriFallimento) {
						
						String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
						String key = CommonConsegnaMultipla.buildKey(response, connettore, id_transazione);
						dataRiferimentoTest = primaConsegna.get(key);						
						
						String query = "Select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ?"
								+ " and data_uscita_richiesta > ? and data_ingresso_risposta >= data_uscita_richiesta"
								+ " and data_ingresso_risposta >= data_accettazione_risposta and data_uscita_richiesta > data_primo_tentativo";
						getLoggerCore().info("Checking date per connettori fallimento: " + id_transazione + " " + connettore + " " + dataRiferimentoTest.toString() );
						Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione,	connettore, dataRiferimentoTest);
						
						// debug for error
						if(count.intValue() != 1) {
							ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa (dataRiferimentoTest:"+dataRiferimentoTest+"):");
							String query2 = "select data_uscita_richiesta, data_ingresso_risposta, data_accettazione_risposta, data_primo_tentativo from transazioni_sa where id_transazione = ? and connettore_nome = ?";
							List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
							for (var v : letto) {
								ConfigLoader.getLoggerCore().error(v.toString());
							}
						}
						
						assertEquals(Integer.valueOf(1), count);

						String fault 		 = "";
						String formatoFault = "";
						String ultimoErrore;
						String locationUltimoErrore; 

						if (requestExpectation.tipoFault == TipoFault.REST) {
							fault 		 = CommonConsegnaMultipla.FAULT_REST;
							formatoFault = CommonConsegnaMultipla.FORMATO_FAULT_REST;
							ultimoErrore = "Consegna [http] con errore: errore applicativo, {\"type\":\"https://httpstatuses.com/500\",\"title\":\"Internal Server Error\",\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
							locationUltimoErrore = "http://localhost:8080/TestService/echo?returnCode="+requestExpectation.statusCodePrincipale+"&problem=true&replyPrefixQueryParameter=GovWay-TestSuite-&id_connettore="+connettore+"&replyQueryParameter=id_connettore";
						} else {
							ultimoErrore =  "Consegna [http] con errore: errore HTTP " + requestExpectation.statusCodePrincipale;
							locationUltimoErrore = "http://localhost:8080/TestService/echo?returnCode="+requestExpectation.statusCodePrincipale+"&replyPrefixQueryParameter=GovWay-TestSuite-&id_connettore="+connettore+"&replyQueryParameter=id_connettore";
						}

						query = "Select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? and data_ultimo_errore > ? and dettaglio_esito_ultimo_errore = ? and codice_risposta_ultimo_errore = ? and ultimo_errore LIKE '"+ultimoErrore+"' and location_ultimo_errore LIKE '"+locationUltimoErrore+"'";
						
						int esitoNotifica;
						if (requestExpectation.tipoFault != TipoFault.NESSUNO) {
							esitoNotifica = ESITO_ERRORE_APPLICATIVO;				
						} else if (isEsitoErrore(requestExpectation.statusCodePrincipale)) {			
							esitoNotifica = ESITO_ERRORE_INVOCAZIONE;
						} else {
							esitoNotifica = esitoConsegnaFromStatusCode(requestExpectation.statusCodePrincipale);
						}
						String statusCode = String.valueOf(requestExpectation.statusCodePrincipale);
						
						getLoggerCore().info("Checking info errori date per connettori fallimento: " + id_transazione + " " + connettore + " " + dataRiferimentoTest.toString() + " " + esitoNotifica + " " + statusCode + " " + ultimoErrore + " " + locationUltimoErrore );
						if (fault.isEmpty() ) {
							query += " and fault_ultimo_errore is null and formato_fault_ultimo_errore is null";
							count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, dataRiferimentoTest, esitoNotifica, statusCode);
							assertEquals(Integer.valueOf(1), count);
						} else {
							query += " and fault_ultimo_errore LIKE '"+fault+"' and formato_fault_ultimo_errore = ?";
							count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, dataRiferimentoTest, esitoNotifica, statusCode, formatoFault);
							assertEquals(Integer.valueOf(1), count);
						}
					}
				}
			}
		}
		
	}
	
	
	@Test
	public void regoleRispedizioneProblem() {
		/**
		 * Qui si testano le varie regole di rispedizione per i singoli connettori tenendo conto dei campi del Problem.
		 * Connettore0, Connettore1:  Consegna Fallita Personalizzata
		 * Connettore2, Connettore3: Consegna  Completata Personalizzata
		 */
		final String erogazione = "TestConsegnaConNotificheRegoleProblem";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		HttpRequest requestRispeditaTuttiValorizzati = new HttpRequest();
		requestRispeditaTuttiValorizzati.setMethod(HttpRequestMethod.GET);
		requestRispeditaTuttiValorizzati.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&problem=true&problemStatus=501&problemType=TypeRispedita&problemDetail=detailRispedita");
		
		HttpRequest requestRispeditaRegex = new HttpRequest();
		requestRispeditaRegex.setMethod(HttpRequestMethod.GET);
		requestRispeditaRegex.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&problem=true&problemStatus=502&problemType=TypeRispedita123");
		
		HttpRequest requestCompletataTuttiValorizzati =  new HttpRequest();
		requestCompletataTuttiValorizzati.setMethod(HttpRequestMethod.GET);
		requestCompletataTuttiValorizzati.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&problem=true&problemStatus=501&problemType=TypeCompletata&problemDetail=detailCompletata");
		
		HttpRequest requestCompletataRegex = new HttpRequest();
		requestCompletataRegex.setMethod(HttpRequestMethod.GET);
		requestCompletataRegex.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&problem=true&problemStatus=502&problemType=TypeCompletata123");
		
		String  fault = "{\"type\":\"TypeRispedita\",\"title\":\"Not Implemented\",\"status\":501,\"detail\":\"detailRispedita\"}";
		
		requestsByKind.add(new RequestAndExpectationsFault(
				requestRispeditaTuttiValorizzati,	
				Set.of(),
				Set.of(CONNETTORE_0,CONNETTORE_2, CONNETTORE_3,CONNETTORE_1),
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 501, TipoFault.REST, fault, FORMATO_FAULT_REST)
			);
	
		 
		 fault = "{\"type\":\"TypeRispedita123\",\"title\":\"Bad Gateway\",\"status\":502,\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestRispeditaRegex,
				Set.of(),
				Set.of(CONNETTORE_1,CONNETTORE_2,CONNETTORE_3, CONNETTORE_0), 
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 502, TipoFault.REST, fault, FORMATO_FAULT_REST)
			);
		
		 fault = "{\"type\":\"TypeCompletata\",\"title\":\"Not Implemented\",\"status\":501,\"detail\":\"detailCompletata\"}";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestCompletataTuttiValorizzati,
				Set.of(CONNETTORE_2),
				Set.of(CONNETTORE_3, CONNETTORE_1, CONNETTORE_0), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 501, TipoFault.REST, fault, FORMATO_FAULT_REST)
			);
		
	
		fault = "{\"type\":\"TypeCompletata123\",\"title\":\"Bad Gateway\",\"status\":502,\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestCompletataRegex,				
				Set.of(CONNETTORE_3),
				Set.of(CONNETTORE_2, CONNETTORE_0,CONNETTORE_1),
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 502, TipoFault.REST, fault, FORMATO_FAULT_REST)
			);
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CondizionaleByFiltroRestTest.checkResponses(responsesByKind);
	}
	
	
	@Test
	public void schedulingAbilitatoDisabilitato() throws UtilsException, HttpUtilsException {
		/*
		 * Prima si disabilita lo scheduling su di un connettore, si verifica lo stato sul db e poi si riattiva
		 * lo scheduling.
		 */
		
		final String erogazione = "TestConsegnaConNotificheSchedulingAbilitatoDisabilitatoRest";
		
		CommonConsegnaMultipla.jmxDisabilitaSchedulingConnettore(erogazione, Common.CONNETTORE_1);
		
		HttpRequest request1 = RequestBuilder.buildRestRequest(erogazione);
		
		var responses = Common.makeParallelRequests(request1, 5);
		Common.checkAll200(responses);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			CommonConsegnaMultipla.withBackoff( () ->checkPresaInConsegna(r, Common.setConnettoriAbilitati.size()));
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
		
		List<String> connettori = new ArrayList<String>();
		connettori.add(Common.CONNETTORE_0);
		connettori.add(Common.CONNETTORE_2);
		connettori.add(Common.CONNETTORE_3);
		CommonConsegnaMultipla.waitConsegna(responses, connettori);
				
		String fault = "";
		String formatoFault = "";
		for (var response : responses) {
			// Sul connettore disabilitato non è avvenuto nulla ancora.
			checkSchedulingConnettoreIniziato(response, Common.CONNETTORE_1);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,  1);
		}
				
		CommonConsegnaMultipla.jmxAbilitaSchedulingConnettore(erogazione, Common.CONNETTORE_1);
		
		// Attendo la consegna sul connettore appena abilitato
		
		connettori = new ArrayList<String>();
		connettori.add(Common.CONNETTORE_1);
		CommonConsegnaMultipla.waitConsegna(responses, connettori);
		
		for (var response : responses) {
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_1, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
		}
	}
	
	
	
	@Test
	public void consegnaConNotificheSemplice() throws IOException {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		
		final String erogazione = "TestConsegnaConNotificheRest";
		HttpRequest request1 = RequestBuilder.buildRestRequest(erogazione);

		var responses = Common.makeParallelRequests(request1, 5);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r, Common.setConnettoriAbilitati.size()));
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(Common.setConnettoriAbilitati);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkConsegnaConnettoreFile(request1, response, connettoriFile);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati, ESITO_OK, 200, "", "");
		}

	}
	
	
	@Test
	public void erroreDiProcessamentoOk() {
		/**
		 *		Si testa la consegna di notifiche in caso di errore di processamento 
		 */
		
		// In questo caso l'errore di processamento è configurato per passare e far avviare le notifiche.
		final String erogazione = "TestConsegnaConNotificheErroreDiProcessamentoOKRest";
		
		// Questa va su test-regola-contenuto e la correlazione applicativa estrae dal contenuto l'id, per questa le notifiche vengono schedulate.
		HttpRequest requestOk = RequestBuilder.buildRequest_Contenuto("valoreIdApplicativo", erogazione) ;
		
		 //  Questa va su test-regola-contenuto e la correlazione applicativa fallisce perchè è una get semplice e non vi trova il contenuto
		// Per questa le notifiche saranno schedulate lo stesso
 		HttpRequest requestErroreProcessamentoOK = new HttpRequest(); 	
		requestErroreProcessamentoOK.setMethod(HttpRequestMethod.GET);
		requestErroreProcessamentoOK.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-contenuto"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);

		var responsesOk = Common.makeParallelRequests(requestOk, 5);
		var responsesErroreProcessamentoOK = Common.makeParallelRequests(requestErroreProcessamentoOK, 5);

		for (var r : responsesOk) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r, Common.setConnettoriAbilitati.size()));
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		
		for (var r : responsesErroreProcessamentoOK) {
			assertEquals(502, r.getResultHTTPOperation());
			checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);						
		}

		// Attendo la consegna
		List<HttpResponse> responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responsesOk);
		responsesCheck.addAll(responsesErroreProcessamentoOK);
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(Common.setConnettoriAbilitati);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);	

		for (var r : responsesOk) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati, ESITO_OK, 200, "", "");
		}
		for (var r : responsesErroreProcessamentoOK) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati,  ESITO_OK,200,  "", "");
		}
		
	}
	
	
	@Test
	public void erroreDiProcessamento() {
		/**
		 *		Si testa la riconsegna di notifiche in caso di errore di processamento 
		 */
		final String erogazione = "TestConsegnaConNotificheErroreDiProcessamentoRest";
		
		HttpRequest requestStandard = RequestBuilder.buildRestRequest(erogazione);
		// Questa va su test-regola-contenuto e la correlazione applicativa estrae dal contenuto l'id, per questa le notifiche vengono schedulate.
		HttpRequest requestOk = RequestBuilder.buildRequest_Contenuto("valoreIdApplicativo", erogazione) ;		
		
		// Questa va su test-regola-contenuto e la correlazione applicativa fallisce perchè non vi trova l'id applicativo. 
		// Per questa richiesta le notifiche non saranno schedulate
 		HttpRequest requestErroreProcessamento = new HttpRequest(); 	
		requestErroreProcessamento.setMethod(HttpRequestMethod.GET);
		requestErroreProcessamento.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-contenuto"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);

		var responses 		= Common.makeParallelRequests(requestStandard, 5);
		var responsesOk = Common.makeParallelRequests(requestOk, 5);
		var responsesErroreProcessamento = Common.makeParallelRequests(requestErroreProcessamento, 5);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r, Common.setConnettoriAbilitati.size()));
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		
		for (var r : responsesOk) {
			assertEquals(200, r.getResultHTTPOperation());
			checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		
		for (var r : responsesErroreProcessamento) {
			assertEquals(502, r.getResultHTTPOperation());
			checkNessunaNotifica(r);
			checkNessunoScheduling(r);						
		}

		// Attendo la consegna
		List<HttpResponse> responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responses);
		responsesCheck.addAll(responsesOk);
		//responsesCheck.addAll(responsesErroreProcessamento);
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(Common.setConnettoriAbilitati);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);

		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati,  ESITO_OK, 200,  "", "");
		}
		for (var r : responsesOk) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati,  ESITO_OK,  200, "", "");
		}
		
	}
	
	@Test
	public void consegnaConNotificheNonCondizionale() {
		// Non c'è la condizionalità sulla transazione sincrona, per cui si comporta come una consegna multipla classica
		// Questa non la divido anche per i 3xx e 5xx visto che stiamo testando tutt'altra funzionalità
		final String erogazione = "TestConsegnaConNotificheNonCondizionaleRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		for (var entry : CommonConsegnaMultipla.statusCode2xx4xxVsConnettori.entrySet()) {
			int statusCode = entry.getKey();
			var connettoriSuccesso = entry.getValue();
			var connettoriErrore = setDifference(Common.setConnettoriAbilitati,connettoriSuccesso);
			requestsByKind.add(buildRequestAndExpectations(erogazione, statusCode, connettoriSuccesso, connettoriErrore));			
		}
		
		HttpRequest requestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestsByKind.add(new RequestAndExpectations(
				requestProblem,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA,
				500,
				true,
				TipoFault.REST));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size()));
			checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectations(requestAndExpectation, response);
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo le date vengano aggiornate
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectationsFinal(requestAndExpectation, response);
			}
		}
		
	}
	
	
	@Test
	public void consegnaConNotificheErrori() {
		/**
		 * Nel primo test verifico il funzionamento senza particolari condizioni: invio una richiesta
		 * e verifico le consegne. Qui invece inizio facendo fallire qualche richiesta.
		 * I connettori file non posso istruirli, quindi uso tutti connettori http.
		 */
		// Errore di Consegna => Spedizione
		// CompletateConSuccesso, FaultApplicativo => Errore
		
		final String erogazione = "TestConsegnaConNotifiche2Rest";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		// 401, principale superata, scheduling fallito su tutti i connettori

		Set<String> connettoriSuccesso = Set.of();
		var connettoriErrore = Common.setConnettoriAbilitati;
		var requestAndExpectation = buildRequestAndExpectations(erogazione, 401, connettoriSuccesso, connettoriErrore);
		requestsByKind.add(requestAndExpectation);
		
		
		// Fault applicativo, principale fallita, nessuno scheduling 
		HttpRequest requestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestAndExpectation = new RequestAndExpectations(
					requestProblem,
					Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
					Set.of(CONNETTORE_1),  
					0, 
					500,
					false,
					TipoFault.REST);
		requestsByKind.add(requestAndExpectation);
		
		// 200, principale fallita, nessuno scheduling
		requestAndExpectation = buildRequestAndExpectations(erogazione,200, Set.of(), Set.of());
		requestAndExpectation.principaleSuperata = false;
		requestsByKind.add(requestAndExpectation);

		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		// Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size()));
				checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
			} else {
				for (var response : responses) {
					CommonConsegnaMultipla.withBackoff( () -> checkNessunaNotifica(response));
					checkNessunoScheduling(response);
				}
			}
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);
				}
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);

			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectationsFinal(requestExpectation, response);
				}
			}
		}
		
	}
	
	
	@Test
	public void erroreTransazionePrincipale() {
		/* In questo test conrollo che non avvenga la spedizione nel caso di errori sulla transazione principale */
		
		final String erogazione = "TestConsegnaConNotificheRest";
		
		HttpRequest request1 = RequestBuilder.buildRestRequest(erogazione);
		request1.setUrl(request1.getUrl()+"&returnCode=401");
		
		var responses = Common.makeParallelRequests(request1, 5);
		
		for (var r : responses) {
			// Le richieste che vedono fallita la transazione principale con un 401, ottengono un 500
			assertEquals(401, r.getResultHTTPOperation());
			CommonConsegnaMultipla.withBackoff( () -> checkNessunaNotifica(r));
			checkNessunoScheduling(r);
		}
	}
	
	
	public void varieCombinazioniDiRegole(Map<Integer,Set<String>> statusCodeVsConnettori) {
		/**
		 * Qui si testa la corretta rispedizione di richieste diverse su connettori diversi.
		 * Vengono testati tutte le regole. 
		 */
		
		final String erogazione = "TestConsegnaConNotificheRestVarieCombinazioniDiRegole";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		for (var entry : statusCodeVsConnettori.entrySet()) {
			int statusCode = entry.getKey();
			var connettoriSuccesso = entry.getValue();
			var connettoriErrore = setDifference(Common.setConnettoriAbilitati,connettoriSuccesso);
			var requestExpectation = buildRequestAndExpectations(erogazione, statusCode, connettoriSuccesso, connettoriErrore);
			
			if (statusCode >= 400 && statusCode <= 499) {
				requestExpectation.principaleSuperata = false;
			}
			if (statusCode >= 500 && statusCode <= 599) {
				requestExpectation.principaleSuperata = false;	
			}
			
			requestsByKind.add(requestExpectation);
		}
		
		// Le fault passano
		HttpRequest requestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestsByKind.add(new RequestAndExpectations(
				requestProblem,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.REST ));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		// Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size()));
				checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
			} else {
				for (var response : responses) {
					CommonConsegnaMultipla.withBackoff( () -> checkNessunaNotifica(response));
					checkNessunoScheduling(response);
				}
			}
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);
				}
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);

			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectationsFinal(requestExpectation, response);
				}
			}
		}
		
	}
	
	
	@Test
	public void cadenzaRispedizione() {
			// La configurazione della API è come quella del primo test: TestConsegnaMultipla
			// L'unica differenza è sul Connettore0 dove è stato impostato un intervallo di rispedizione pari ad un minuto.
			// Il test dunque controlla che in caso di mancata consegna, per gli altri connettori avviene il reinvio dopo "intervalloControlloFallite",
			// per il Connettore0 invece il reinvio avviene dopo un minuto + intervalloControlloFallite
			
			final String erogazione = "TestConsegnaConNotificheRispedizioneRest";
			
			Set<String> connettoriSuccessoRequest = Set.of();
			Set<String> connettoriFallimentoRequest = Common.setConnettoriAbilitati;
			
			List<RequestAndExpectations> requestsByKind = new ArrayList<>();
			
			for(int i=0; i<5;i++) {
				int statusCode = 500+i;
				HttpRequest request5xx = RequestBuilder.buildRestRequest(erogazione);
				request5xx.setUrl(request5xx.getUrl()+"&returnCode=" + statusCode);
				requestsByKind.add(new RequestAndExpectations(request5xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,statusCode));

				statusCode = 200+i;
				HttpRequest request2xx = RequestBuilder.buildRestRequest(erogazione);
				request2xx.setUrl(request2xx.getUrl()+"&returnCode=" + statusCode);
				requestsByKind.add(new RequestAndExpectations(request2xx, Common.setConnettoriAbilitati, Set.of(), ESITO_CONSEGNA_MULTIPLA_COMPLETATA, statusCode));

				statusCode = 400+i;
				HttpRequest request4xx = RequestBuilder.buildRestRequest(erogazione);
				request4xx.setUrl(request4xx.getUrl()+"&returnCode=" + statusCode);
				requestsByKind.add(new RequestAndExpectations(request4xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, statusCode));
			}
									
			// Configuro gli esiti per la richiesta sincrona
			/*
			for (var requestExpectation : requestsByKind) {
				int statusCode = requestExpectation.statusCodePrincipale;
				
				if (statusCode >= 400 && statusCode <= 499) {
					requestExpectation.principaleSuperata = false;
				}
				
				if (statusCode >= 500 && statusCode <= 599) {
					requestExpectation.principaleSuperata = false;	
				}
			}	
			*/		
			
			HttpRequest requestProblem =  RequestBuilder.buildRestRequestProblem(erogazione);
			requestsByKind.add(new RequestAndExpectations(requestProblem, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.REST));
			
			Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
			
			// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
			// Solo Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
			for (var requestExpectation : responsesByKind.keySet()) {
				var responses = responsesByKind.get(requestExpectation);
				
				if (requestExpectation.principaleSuperata) {
					CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size()));
					checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
				} else {
					for (var response : responses) {
						CommonConsegnaMultipla.withBackoff( () -> checkNessunaNotifica(response));
						checkNessunoScheduling(response);
					}
				}
			}
			
			// Attendo la prima consegna
			Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);

			// Adesso devono essere state effettuate le consegne
			for (var requestExpectation : responsesByKind.keySet()) {
				var responses = responsesByKind.get(requestExpectation);
				
				if (requestExpectation.principaleSuperata) {
					for (var response : responses) {
						CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);
					}
				}
			}
			
			// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
			CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna,CONNETTORE_0);
			
			for (var requestAndExpectation : responsesByKind.keySet()) {
				if (requestAndExpectation.principaleSuperata ) {
				
					for (var response : responsesByKind.get(requestAndExpectation)) {
						
						for (var connettore : requestAndExpectation.connettoriFallimento) {
							if (connettore.equals(CONNETTORE_0) ) {
								assertTrue(	getNumeroTentativiSchedulingConnettore(response, connettore) == 1);
							} else {
								assertTrue(	getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
							}
						}
	
						checkStatoConsegna(response, requestAndExpectation.esitoPrincipale, requestAndExpectation.connettoriFallimento.size());
					}
				}
			}
			
			// Attendo un minuto, anche il connettore 0 ora deve avere una rispedizione in più
			ConfigLoader.getLoggerCore().info("@CadenzaRispedizione test in corso, attendo 1 minuto ...");
			org.openspcoop2.utils.Utilities.sleep(1000*60);
			org.openspcoop2.utils.Utilities.sleep(CommonConsegnaMultipla.intervalloControllo);
			
			for (var requestAndExpectation : responsesByKind.keySet()) {
				if (requestAndExpectation.principaleSuperata) {
					for (var response : responsesByKind.get(requestAndExpectation)) {
	
						for (var connettore : requestAndExpectation.connettoriFallimento) {
							if (connettore.equals(CONNETTORE_0) ) {
								assertTrue(	getNumeroTentativiSchedulingConnettore(response, connettore) == 2);
							} else {
								assertTrue(	getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
							}
						}
	
						checkStatoConsegna(response, requestAndExpectation.esitoPrincipale, requestAndExpectation.connettoriFallimento.size());
					}
				}
			}
		}
	
	
	@Test
	public void connettoreRotto() {
		final String erogazione = "TestConsegnaConNotificheConnettoreRottoRest";
		
		// Va come il primo test, ci si aspetta che funzionino tutti i connettori
		// eccetto quello rotto, dove devono avvenire le rispedizioni.
		
		HttpRequest request1 = RequestBuilder.buildRestRequest(erogazione);

		var responses = Common.makeParallelRequests(request1, 5);
		
		Common.checkAll200(responses);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna		
   	   	Set<String> connettoriSchedulati = new HashSet<>(Common.setConnettoriAbilitati);
   	   	connettoriSchedulati.add(CONNETTORE_ROTTO);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r, connettoriSchedulati.size()));
			checkSchedulingConnettoreIniziato(r, connettoriSchedulati);	
		}
	
		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettoriSchedulati);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);
	
		// La consegna deve risultare ancora in corso per via dell'errore sul connettore rotto, con un connettore
		// in rispedizione
		String fault = "";
		String formatoFault = "";
		for (var r : responses) {
			CommonConsegnaMultipla.checkStatoConsegna(r, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			CommonConsegnaMultipla.checkSchedulingConnettoreInCorso(r, CONNETTORE_ROTTO, ESITO_ERRORE_INVOCAZIONE,0, fault, formatoFault);
			for (var connettore : Common.setConnettoriAbilitati) {				
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(r, connettore, esitoConsegnaFromStatusCode(200), 200, fault, formatoFault);
			}
		}
		
	}

}
