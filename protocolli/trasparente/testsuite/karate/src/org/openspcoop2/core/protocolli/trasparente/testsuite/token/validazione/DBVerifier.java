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

package org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione;

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
			long esitoExpected, String msgErrore, CredenzialiMittenteVerifier credenzialiMittente, List<String> mapExpectedTokenInfo) throws Exception  {
		verify(idTransazione, 
				esitoExpected, msgErrore, 
				credenzialiMittente,
				(mapExpectedTokenInfo!=null && !mapExpectedTokenInfo.isEmpty()) ? mapExpectedTokenInfo.toArray(new String[1]) : null);
	}
	public static void verify(String idTransazione, 
			long esitoExpected, String msgErrore, CredenzialiMittenteVerifier credenzialiMittente, String ... tokenInfoCheck) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier._verify(idTransazione, 
					esitoExpected, msgErrore, credenzialiMittente, tokenInfoCheck);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier._verify(idTransazione, 
						esitoExpected, msgErrore, credenzialiMittente, tokenInfoCheck);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verify(idTransazione, 
							esitoExpected, msgErrore, credenzialiMittente, tokenInfoCheck);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier._verify(idTransazione, 
							esitoExpected, msgErrore, credenzialiMittente, tokenInfoCheck);
				}
			}
		}
	}
	
	private static void _verify(String idTransazione, 
			long esitoExpected, String msgErrore, CredenzialiMittenteVerifier credenzialiMittente, String ... tokenInfoCheck) throws Exception  {
		
		
		String query = "select count(*) from transazioni where id = ?";
		log().info(query);
		
		int count = dbUtils().readValue(query, Integer.class, idTransazione);
		assertEquals("IdTransazione: "+idTransazione, 1, count);

		
		
		query = "select esito,token_info,token_issuer,token_subject,token_client_id,token_username,token_mail from transazioni where id = ?";
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

		
		Object otoken_info = row.get("token_info");
		if(tokenInfoCheck!=null && tokenInfoCheck.length>0) {
			assertNotNull(msg,otoken_info);
			assertTrue(msg+" otoken_info classe '"+otoken_info.getClass().getName()+"'", (otoken_info instanceof String));
			String tokenInfo = null;
			if(otoken_info instanceof String) {
				tokenInfo = (String)otoken_info;
			}
			assertNotNull(msg+" (token info string)",tokenInfo);
			for (String info: tokenInfoCheck) {
				assertTrue(msg+" tokenInfo contains '"+info+"' (token:"+tokenInfo+")", (tokenInfo.contains(info)));
			}
		}
		else {
			assertNull(msg,otoken_info);
		}
		
		checkCredenzialiMittente(row, "token_issuer", credenzialiMittente!=null ? credenzialiMittente.getIssuer() : null, TipoCredenzialeMittente.TOKEN_ISSUER);
		checkCredenzialiMittente(row, "token_subject", credenzialiMittente!=null ? credenzialiMittente.getSubject() : null, TipoCredenzialeMittente.TOKEN_SUBJECT);
		checkCredenzialiMittente(row, "token_client_id", credenzialiMittente!=null ? credenzialiMittente.getClientId() : null, TipoCredenzialeMittente.TOKEN_CLIENT_ID);
		checkCredenzialiMittente(row, "token_username", credenzialiMittente!=null ? credenzialiMittente.getUsername() : null, TipoCredenzialeMittente.TOKEN_USERNAME);
		checkCredenzialiMittente(row, "token_mail", credenzialiMittente!=null ? credenzialiMittente.getEmail() : null, TipoCredenzialeMittente.TOKEN_EMAIL);
		
		// diagnostici
		
		if(msgErrore!=null) {
			query = "select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '%"+msgErrore.replaceAll("'", "''")+"%'";
			log().info(query);
		
			count = dbUtils().readValue(query, Integer.class, idTransazione);
			assertTrue(msg+" Cerco dettaglio '"+msgErrore+"'; count trovati: "+count+"", (count>0));
		}

	}
	
	private static void checkCredenzialiMittente(Map<String, Object> row, String colonna, String expectedValue, TipoCredenzialeMittente tipoCredenzialeMittente) throws Exception {
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
			assertTrue(colonna+" tipo credenziale 'found:"+tipo+"' == 'expected:"+tipoCredenzialeMittente.getRawValue()+"'", (tipo.equals(tipoCredenzialeMittente.getRawValue())));
			
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
