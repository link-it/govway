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

package org.openspcoop2.core.protocolli.trasparente.testsuite.other.api_grandi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DbUtils;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**
* DBVerifier
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class DBVerifier {
	
	private static DbUtils dbUtils() {
		return ConfigLoader.getDbUtils();
	}
	
	private static Logger log() {
		return ConfigLoader.getLoggerRegistrazioneMessaggi();
	}
	
	public static void verify(String idTransazione, 
			int millisecondiCheck) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier._verify(idTransazione, millisecondiCheck);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier._verify(idTransazione, millisecondiCheck);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verify(idTransazione, millisecondiCheck);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier._verify(idTransazione, millisecondiCheck);
				}
			}
		}
	}
	
	public static void _verify(String idTransazione, 
			int millisecondiCheck) throws Exception  {
	
		String query = "select data_accettazione_richiesta,data_uscita_risposta from transazioni where id = ?";
		log().info(query);
		
		String msg = "IdTransazione: "+idTransazione;
		
		List<Map<String, Object>> rows = dbUtils().readRows(query, idTransazione);
		assertNotNull(msg, rows);
		assertEquals(msg, 1, rows.size());
		
		Map<String, Object> row = rows.get(0);
		for (String key : row.keySet()) {
			log().debug("Row["+key+"]="+row.get(key));
		}
		
		Object oDataAccettazione = row.get("data_accettazione_richiesta");
		assertNotNull(msg,oDataAccettazione);
		assertTrue(msg+" oEsito classe '"+oDataAccettazione.getClass().getName()+"'", (oDataAccettazione instanceof Date));
		long dataIngresso = -1;
		if(oDataAccettazione instanceof Date) {
			dataIngresso = ((Date)oDataAccettazione).getTime();
		}
		
		Object oDataRisposta = row.get("data_uscita_risposta");
		assertNotNull(msg,oDataRisposta);
		assertTrue(msg+" oEsito classe '"+oDataRisposta.getClass().getName()+"'", (oDataRisposta instanceof Date));
		long dataRisposta = -1;
		if(oDataRisposta instanceof Date) {
			dataRisposta = ((Date)oDataRisposta).getTime();
		}
		
		long latenza = dataRisposta - dataIngresso;
		assertTrue("DataIngresso '"+dataIngresso+"', DataUscita '"+dataRisposta+"', latenza:"+latenza+"",latenza<millisecondiCheck);
		
	}
	
	
	
	
	
	
	
	public static void verifyEsito(String idTransazione, 
			long esitoExpected, String msgErrore) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier._verifyEsito(idTransazione, 
					esitoExpected, msgErrore);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier._verifyEsito(idTransazione, 
						esitoExpected, msgErrore);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verifyEsito(idTransazione, 
							esitoExpected, msgErrore);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier._verifyEsito(idTransazione, 
							esitoExpected, msgErrore);
				}
			}
		}
	}
	
	private static void _verifyEsito(String idTransazione, 
			long esitoExpected, String msgErrore) throws Exception  {
		
		
		String query = "select count(*) from transazioni where id = ?";
		log().info(query);
		
		int count = dbUtils().readValue(query, Integer.class, idTransazione);
		assertEquals("IdTransazione: "+idTransazione, 1, count);

		
		
		query = "select esito from transazioni where id = ?";
		log().info(query);
		
		String msg = "IdTransazione: "+idTransazione;
		
		List<Map<String, Object>> rows = dbUtils().readRows(query, idTransazione);
		assertNotNull(msg, rows);
		assertEquals(msg, 1, rows.size());
					
		Long esito = null;
		Map<String, Object> row = rows.get(0);
		for (String key : row.keySet()) {
			log().debug("Row["+key+"]="+row.get(key));
		}
	
		Object oEsito = row.get("esito");
		assertNotNull(msg,oEsito);
		assertTrue(msg+" oEsito classe '"+oEsito.getClass().getName()+"'", (oEsito instanceof Integer || oEsito instanceof Long));
		if(oEsito instanceof Integer) {
			esito = Long.valueOf( (Integer)oEsito );
		}
		else {
			esito = (Long)oEsito;
		}
		assertEquals(msg,esitoExpected, esito.longValue());

		
		
		// diagnostici
		
		if(msgErrore!=null) {
			query = "select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '%"+msgErrore+"%'";
			log().info(query);
		
			count = dbUtils().readValue(query, Integer.class, idTransazione);
			assertTrue(msg+" Cerco dettaglio '"+msgErrore+"'; count trovati: "+count+"", (count>0));
		}

	}
	
	
	
	
	
	public static String existsDiagnostico(String idTransazione, String diagnostico) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			return DBVerifier.existsDiagnosticoEngine(idTransazione, diagnostico);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				return DBVerifier.existsDiagnosticoEngine(idTransazione, diagnostico);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					return DBVerifier.existsDiagnosticoEngine(idTransazione, diagnostico);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					return DBVerifier.existsDiagnosticoEngine(idTransazione, diagnostico);
				}
			}
		}
	}
	private static String existsDiagnosticoEngine(String idTransazione,  String diagnostico) throws Exception  {
		
		String query = "select messaggio from msgdiagnostici where id_transazione = ? and messaggio LIKE '%"+diagnostico+"%'";
		log().info(query);
		
		String msgDiag = dbUtils().readValue(query, String.class, idTransazione);
		String msg = "IdTransazione: "+idTransazione;
		assertTrue(msg+" Cerco dettaglio '"+diagnostico+"'", (msgDiag!=null));

		return msgDiag;
	}
}
