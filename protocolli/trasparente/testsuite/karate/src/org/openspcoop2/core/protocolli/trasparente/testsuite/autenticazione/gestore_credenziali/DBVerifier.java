/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DbUtils;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTokenClient;
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
		return ConfigLoader.getLoggerCore();
	}
	
	public static void verify(String idTransazione, 
			long esitoExpected, 
			String soggetto, String applicativo,
			CredenzialeTrasporto credenzialiTrasporto,
			String ... msgErrore) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier._verify(idTransazione, 
					esitoExpected, soggetto, applicativo, credenzialiTrasporto, msgErrore);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier._verify(idTransazione, 
						esitoExpected, soggetto, applicativo, credenzialiTrasporto, msgErrore);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verify(idTransazione, 
							esitoExpected, soggetto, applicativo, credenzialiTrasporto, msgErrore);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier._verify(idTransazione, 
							esitoExpected, soggetto, applicativo, credenzialiTrasporto, msgErrore);
				}
			}
		}
	}
	
	private static void _verify(String idTransazione, 
			long esitoExpected, 
			String soggetto, String applicativo,
			CredenzialeTrasporto credenzialiTrasporto,
			String ... msgErrori) throws Exception  {
		
		
		String query = "select count(*) from transazioni where id = ?";
		log().info(query);
		
		int count = dbUtils().readValue(query, Integer.class, idTransazione);
		assertEquals("IdTransazione: "+idTransazione, 1, count);

		
		
		query = "select esito,nome_soggetto_fruitore,servizio_applicativo_fruitore,credenziali,trasporto_mittente from transazioni where id = ?";
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

		if(soggetto!=null) {
			Object oSoggetto = row.get("nome_soggetto_fruitore");
			assertNotNull(msg,oSoggetto);
			assertTrue(msg+" oEsito classe '"+oSoggetto.getClass().getName()+"'", (oSoggetto instanceof String));
			String check = null;
			if(oSoggetto instanceof String) {
				check = (String) oSoggetto;
			}
			assertEquals(msg,soggetto, check);
		}
		else {
			Object oSoggetto = row.get("nome_soggetto_fruitore");
			boolean isNull = (oSoggetto == null) || ("".equals(oSoggetto.toString()));
			assertTrue(msg+" atteso soggetto null, trovato: "+oSoggetto, isNull);
		}
		
		if(applicativo!=null) {
			Object oApplicativo = row.get("servizio_applicativo_fruitore");
			assertNotNull(msg,oApplicativo);
			assertTrue(msg+" oEsito classe '"+oApplicativo.getClass().getName()+"'", (oApplicativo instanceof String));
			String check = null;
			if(oApplicativo instanceof String) {
				check = (String) oApplicativo;
			}
			assertEquals(msg,applicativo, check);
		}
		else {
			Object oApplicativo = row.get("servizio_applicativo_fruitore");
			boolean isNull = (oApplicativo == null) || ("".equals(oApplicativo.toString()));
			assertTrue(msg+" atteso applicativo null, trovato: "+oApplicativo, isNull);
		}
		
		String credenziali = credenzialiTrasporto!=null ? credenzialiTrasporto.getCredenziali() : null;
		if(credenziali!=null) {
			Object oCredenziali = row.get("credenziali");
			assertNotNull(msg,oCredenziali);
			assertTrue(msg+" oEsito classe '"+oCredenziali.getClass().getName()+"'", (oCredenziali instanceof String));
			String check = null;
			if(oCredenziali instanceof String) {
				check = (String) oCredenziali;
			}
			if(!credenziali.equals(check)) {
				System.out.println("Credenziali sul database '"+check+"'");
			}
			assertEquals(msg,credenziali, check);
		}
		else {
			Object oCredenziali = row.get("credenziali");
			boolean isNull = (oCredenziali == null) || ("".equals(oCredenziali.toString()));
			assertTrue(msg+" attese credenziali null, trovate: "+oCredenziali, isNull);
		}

		// Check credenziali mittente
		String identificativoAutenticato = credenzialiTrasporto!=null ? credenzialiTrasporto.getIdentificativoAutenticato() : null;
		String tipoCredenziali = credenzialiTrasporto!=null ? credenzialiTrasporto.getTipoCredenziali() : null;
		checkCredenzialiMittente(row, "trasporto_mittente", identificativoAutenticato, 
				TipoCredenzialeMittente.TRASPORTO, tipoCredenziali);
		
		// diagnostici
		
		if(msgErrori!=null && msgErrori.length>0) {
			for (String msgErrore : msgErrori) {
				if(msgErrore.contains("'")) {
					msgErrore = msgErrore .replaceAll("'", "''");
				}
				query = "select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '%"+msgErrore+"%'";
				log().info(query);
			
				count = dbUtils().readValue(query, Integer.class, idTransazione);
				assertTrue(msg+" Cerco dettaglio '"+msgErrore+"'; count trovati: "+count+"", (count>0));		
			}
		}

	}
	
	private static void checkCredenzialiMittente(Map<String, Object> row, String colonna, String expectedValue, 
			TipoCredenzialeMittente tipoCredenzialeMittente, String tipoCredenziali) throws Exception {
		Object oColumnValue = row.get(colonna);
		if( expectedValue!=null) {
			assertNotNull(colonna,oColumnValue);
			assertTrue(colonna+" classe '"+oColumnValue.getClass().getName()+"'", (oColumnValue instanceof String));
			String tokenClientId = null;
			if(oColumnValue instanceof String) {
				tokenClientId = (String)oColumnValue;
			}
			assertNotNull(colonna+" (string)",tokenClientId);
			long idToken = -1;
			try {
				idToken = Long.parseLong(tokenClientId);
				if(idToken<=0) {
					throw new Exception("Minore o uguale a zero");
				}
			}catch(Throwable t) {
				throw new Exception(colonna+" non valido '"+idToken+"': "+t.getMessage(),t);
			}
			
			Map<String,Object> map = getCredenzialiMittenteById(tokenClientId);
			if(map==null || map.isEmpty()) {
				throw new Exception(colonna+" '"+idToken+"' non corrisponde a nessuna credenziale mittente");
			}
			
			if(!map.containsKey("tipo")) {
				throw new Exception(colonna+" '"+idToken+"' corrisponde a una credenziale mittente ("+map.keySet()+") che non contiene la colonna 'tipo' ?");
			}
			Object oTipo = map.get("tipo");
			assertTrue(colonna+" o tipo credenziale classe '"+oTipo.getClass().getName()+"'", (oTipo instanceof String));
			String tipo = null;
			if(oTipo instanceof String) {
				tipo = (String)oTipo;
			}
			assertNotNull(colonna+" (tipo credenziale string)",tipo);
			String expectedTipo = tipoCredenzialeMittente.getRawValue()+"_"+tipoCredenziali;
			assertTrue(colonna+" tipo credenziale 'found:"+tipo+"' == 'expected:"+expectedTipo+"'", (tipo.equals(expectedTipo)));
			
			if(!map.containsKey("credenziale")) {
				throw new Exception(colonna+" '"+idToken+"' corrisponde a una credenziale mittente ("+map.keySet()+") che non contiene la colonna 'credenziale' ?");
			}
			Object oCredenziale = map.get("credenziale");
			assertTrue(colonna+" o credenziale classe '"+oCredenziale.getClass().getName()+"'", (oCredenziale instanceof String));
			String credenziale = null;
			if(oCredenziale instanceof String) {
				credenziale = (String)oCredenziale;
			}
			assertNotNull(colonna+" (credenziale string)",credenziale);
			
			String valueFound = credenziale;
			if(TipoCredenzialeMittente.TOKEN_CLIENT_ID.equals(tipoCredenzialeMittente)) {
				valueFound = CredenzialeTokenClient.convertClientIdDBValueToOriginal(credenziale);
			}
			
			assertNotNull(colonna,valueFound);
			assertTrue(colonna+" tradotto 'found:"+valueFound+"' == 'expected:"+expectedValue+"'", (valueFound.equals(expectedValue)));
			
		}
		else {
			assertNull(colonna,oColumnValue);
		}
	}
	
	private static Map<String,Object> getCredenzialiMittenteById(String id) {
		String query = "select * from credenziale_mittente where id="+id;
		log().info(query);
		return dbUtils().readRow(query);
	}
}
