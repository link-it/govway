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

package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.autenticazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DbUtils;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeClientAddress;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
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
			long esitoExpected)  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier._verify(idTransazione, 
					esitoExpected);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier._verify(idTransazione, 
						esitoExpected);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verify(idTransazione, 
							esitoExpected);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier._verify(idTransazione, 
							esitoExpected);
				}
			}
		}
	}
	
	private static void _verify(String idTransazione, 
			long esitoExpected)  {
		
		
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

	}

	
	
	
	
	public static void verify(String idTransazione, 
			String valoreAttesoReale, String valoreAttesoIndicizzato) throws UtilsException  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier._verifyCredenzialeIp(idTransazione, 
					valoreAttesoReale, valoreAttesoIndicizzato);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier._verifyCredenzialeIp(idTransazione, 
						valoreAttesoReale, valoreAttesoIndicizzato);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verifyCredenzialeIp(idTransazione, 
							valoreAttesoReale, valoreAttesoIndicizzato);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier._verifyCredenzialeIp(idTransazione, 
							valoreAttesoReale, valoreAttesoIndicizzato);
				}
			}
		}
	}
	
	private static void _verifyCredenzialeIp(String idTransazione, 
			String valoreAttesoReale, String valoreAttesoIndicizzato) throws UtilsException  {
		
		
		String query = "select transport_client_address, client_address from transazioni where id = ?";
		log().info(query);
		
		String msg = "IdTransazione: "+idTransazione;
		
		List<Map<String, Object>> rows = dbUtils().readRows(query, idTransazione);
		assertNotNull(msg, rows);
		assertEquals(msg, 1, rows.size());
					
		Map<String, Object> row = rows.get(0);
		for (String key : row.keySet()) {
			log().debug("Row["+key+"]="+row.get(key));
		}
	
		Object oTransportCredenziali = row.get("transport_client_address");
		assertNotNull(msg,oTransportCredenziali);
		assertTrue(msg+" oEsito classe '"+oTransportCredenziali.getClass().getName()+"'", (oTransportCredenziali instanceof String));
		String check = null;
		if(oTransportCredenziali instanceof String) {
			check = (String) oTransportCredenziali;
		}
		if(!valoreAttesoReale.equals(check)) {
			System.out.println("Credenziali sul database '"+check+"'");
		}
		assertEquals(msg,valoreAttesoReale, check);		

		Object oCredenziali = row.get("client_address");
		assertNotNull(msg,oCredenziali);
		assertTrue(msg+" oEsito classe '"+oCredenziali.getClass().getName()+"'", (oCredenziali instanceof String));
		check = null;
		if(oCredenziali instanceof String) {
			check = (String) oCredenziali;
		}
	
		// Check credenziali mittente
		checkCredenzialiMittente("client_address="+check, check, 
				valoreAttesoIndicizzato);
	}
	
	private static void checkCredenzialiMittente(String colonna, String oColumnValue, String expectedValue) throws UtilsException {

			assertNotNull(colonna,oColumnValue);
			assertTrue(colonna+" classe '"+oColumnValue.getClass().getName()+"'", (oColumnValue instanceof String));
			String columnValue = null;
			if(oColumnValue instanceof String) {
				columnValue = (String)oColumnValue;
			}
			assertNotNull(colonna+" (string)",columnValue);
			long columnValueAsLong = -1;
			try {
				columnValueAsLong = Long.parseLong(columnValue);
				if(columnValueAsLong<=0) {
					throw new UtilsException("Minore o uguale a zero");
				}
			}catch(Throwable t) {
				throw new UtilsException(colonna+" non valido '"+columnValueAsLong+"': "+t.getMessage(),t);
			}
			
			Map<String,Object> map = getCredenzialiMittenteById(columnValue);
			if(map==null || map.isEmpty()) {
				throw new UtilsException(colonna+" '"+columnValueAsLong+"' non corrisponde a nessuna credenziale mittente");
			}
			
			if(!map.containsKey("tipo")) {
				throw new UtilsException(colonna+" '"+columnValueAsLong+"' corrisponde a una credenziale mittente ("+map.keySet()+") che non contiene la colonna 'tipo' ?");
			}
			Object oTipo = map.get("tipo");
			assertTrue(colonna+" o tipo credenziale classe '"+oTipo.getClass().getName()+"'", (oTipo instanceof String));
			String tipo = null;
			if(oTipo instanceof String) {
				tipo = (String)oTipo;
			}
			assertNotNull(colonna+" (tipo credenziale string)",tipo);
			String expectedTipo = TipoCredenzialeMittente.CLIENT_ADDRESS.getRawValue();
			assertTrue(colonna+" tipo credenziale 'found:"+tipo+"' == 'expected:"+expectedTipo+"'", (tipo.equals(expectedTipo)));
			
			if(!map.containsKey("credenziale")) {
				throw new UtilsException(colonna+" '"+columnValueAsLong+"' corrisponde a una credenziale mittente ("+map.keySet()+") che non contiene la colonna 'credenziale' ?");
			}
			Object oCredenziale = map.get("credenziale");
			assertTrue(colonna+" o credenziale classe '"+oCredenziale.getClass().getName()+"'", (oCredenziale instanceof String));
			String credenziale = null;
			if(oCredenziale instanceof String) {
				credenziale = (String)oCredenziale;
			}
			assertNotNull(colonna+" (credenziale string)",credenziale);
			
			String valueFound = CredenzialeClientAddress.convertTransportDBValueToOriginal(credenziale);
			if(!expectedValue.equals(valueFound)) {
				System.out.println("Credenziali '"+oColumnValue+"' sul database prima della conversione '"+credenziale+"' dopo la conversione '"+valueFound+"'");
			}
			
			assertNotNull(colonna,valueFound);
			assertTrue(colonna+" tradotto 'found:"+valueFound+"' == 'expected:"+expectedValue+"'", (valueFound.equals(expectedValue)));
			
	}
	private static Map<String,Object> getCredenzialiMittenteById(String id) {
		String query = "select * from credenziale_mittente where id="+id;
		log().info(query);
		return dbUtils().readRow(query);
	}
}
