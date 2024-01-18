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

package org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
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
			long esitoExpected, String msgErrore, 
			String clientId, IDServizioApplicativo idApplicativoToken, 
			IDSoggetto idSoggettoFruitoreTrasporto, IDServizioApplicativo idApplicativoTrasporto) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier._verify(idTransazione, 
					esitoExpected, msgErrore, 
					clientId, idApplicativoToken, 
					idSoggettoFruitoreTrasporto, idApplicativoTrasporto);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier._verify(idTransazione, 
						esitoExpected, msgErrore, 
						clientId, idApplicativoToken, 
						idSoggettoFruitoreTrasporto, idApplicativoTrasporto);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verify(idTransazione, 
							esitoExpected, msgErrore, 
							clientId, idApplicativoToken, 
							idSoggettoFruitoreTrasporto, idApplicativoTrasporto);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier._verify(idTransazione, 
							esitoExpected, msgErrore, 
							clientId, idApplicativoToken, 
							idSoggettoFruitoreTrasporto, idApplicativoTrasporto);
				}
			}
		}
	}
	
	private static void _verify(String idTransazione, 
			long esitoExpected, String msgErrore, 
			String clientId, IDServizioApplicativo idApplicativoToken, 
			IDSoggetto idSoggettoFruitoreTrasporto, IDServizioApplicativo idApplicativoTrasporto) throws Exception  {
		
		
		String query = "select count(*) from transazioni where id = ?";
		log().info(query);
		
		int count = dbUtils().readValue(query, Integer.class, idTransazione);
		assertEquals("IdTransazione: "+idTransazione, 1, count);

		
		
		query = "select esito,tipo_soggetto_fruitore,nome_soggetto_fruitore,servizio_applicativo_fruitore,token_client_id from transazioni where id = ?";
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

		if(idSoggettoFruitoreTrasporto==null && idApplicativoTrasporto!=null) {
			idSoggettoFruitoreTrasporto = idApplicativoTrasporto.getIdSoggettoProprietario();
		}
		
		Object oTipoSoggettoFruitore = row.get("tipo_soggetto_fruitore");
		Object oNomeSoggettoFruitore = row.get("nome_soggetto_fruitore");
		if(idSoggettoFruitoreTrasporto!=null) {
			assertNotNull(msg,oTipoSoggettoFruitore);
			assertNotNull(msg,oNomeSoggettoFruitore);
			assertTrue(msg+" o tipo_soggetto_fruitore classe '"+oTipoSoggettoFruitore.getClass().getName()+"'", (oTipoSoggettoFruitore instanceof String));
			assertTrue(msg+" o nome_soggetto_fruitore classe '"+oNomeSoggettoFruitore.getClass().getName()+"'", (oNomeSoggettoFruitore instanceof String));
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			if(oTipoSoggettoFruitore instanceof String) {
				tipoSoggettoFruitore = (String)oTipoSoggettoFruitore;
			}
			if(oNomeSoggettoFruitore instanceof String) {
				nomeSoggettoFruitore = (String)oNomeSoggettoFruitore;
			}
			assertNotNull(msg+" (tipo_soggetto_fruitore string)",tipoSoggettoFruitore);
			assertNotNull(msg+" (nome_soggetto_fruitore string)",nomeSoggettoFruitore);
			assertTrue(msg+" tipo_soggetto_fruitore 'found:"+tipoSoggettoFruitore+"' == 'expected:"+idSoggettoFruitoreTrasporto.getTipo()+"'", (tipoSoggettoFruitore.equals(idSoggettoFruitoreTrasporto.getTipo())));
			assertTrue(msg+" nome_soggetto_fruitore 'found:"+nomeSoggettoFruitore+"' == 'expected:"+idSoggettoFruitoreTrasporto.getNome()+"'", (nomeSoggettoFruitore.equals(idSoggettoFruitoreTrasporto.getNome())));
		}
		else {
			assertNull(msg,oTipoSoggettoFruitore);
			assertNull(msg,oNomeSoggettoFruitore);
		}
		
		Object oServizioApplicativoFruitore = row.get("servizio_applicativo_fruitore");
		if(idApplicativoTrasporto!=null && idApplicativoTrasporto.getNome()!=null && 
				!org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.SOLO_INFO_SOGGETTO.equals(idApplicativoTrasporto.getNome())) {
			assertNotNull(msg,oServizioApplicativoFruitore);
			assertTrue(msg+" o servizio_applicativo_fruitore classe '"+oServizioApplicativoFruitore.getClass().getName()+"'", (oServizioApplicativoFruitore instanceof String));
			String servizioApplicativoFruitore = null;
			if(oServizioApplicativoFruitore instanceof String) {
				servizioApplicativoFruitore = (String)oServizioApplicativoFruitore;
			}
			assertNotNull(msg+" (servizio_applicativo_fruitore string)",servizioApplicativoFruitore);
			assertTrue(msg+" servizio_applicativo_fruitore 'found:"+servizioApplicativoFruitore+"' == 'expected:"+idApplicativoTrasporto.getNome()+"'", (servizioApplicativoFruitore.equals(idApplicativoTrasporto.getNome())));
		}
		else {
			assertNull(msg,oServizioApplicativoFruitore);
		}
		
		Object oTokenClientId = row.get("token_client_id");
		if( 
				/**(
						msgErrore==null 
						|| 
						!msgErrore.contains("differente dal soggetto proprietario della porta invocata")
				) 
				&& */
				(
						idApplicativoToken!=null || clientId!=null
				)
			) {
			assertNotNull(msg,oTokenClientId);
			assertTrue(msg+" o token_client_id classe '"+oTokenClientId.getClass().getName()+"'", (oTokenClientId instanceof String));
			String tokenClientId = null;
			if(oTokenClientId instanceof String) {
				tokenClientId = (String)oTokenClientId;
			}
			assertNotNull(msg+" (token_client_id string)",tokenClientId);
			long idToken = -1;
			try {
				idToken = Long.parseLong(tokenClientId);
				if(idToken<=0) {
					throw new Exception("Minore o uguale a zero");
				}
			}catch(Throwable t) {
				throw new Exception(msg+"token client id non valido '"+idToken+"': "+t.getMessage(),t);
			}
			
			Map<String,Object> map = getCredenzialiMittenteById(tokenClientId);
			if(map==null || map.isEmpty()) {
				throw new Exception(msg+"token client id '"+idToken+"' non corrisponde a nessuna credenziale mittente");
			}
			
			if(!map.containsKey("tipo")) {
				throw new Exception(msg+"token client id '"+idToken+"' corrisponde a una credenziale mittente ("+map.keySet()+") che non contiene la colonna 'tipo' ?");
			}
			Object oTipo = map.get("tipo");
			assertTrue(msg+" o tipo crdenziale classe '"+oTipo.getClass().getName()+"'", (oTipo instanceof String));
			String tipo = null;
			if(oTipo instanceof String) {
				tipo = (String)oTipo;
			}
			assertNotNull(msg+" (tipo credenziale string)",tipo);
			assertTrue(msg+" tipo credenziale 'found:"+tipo+"' == 'expected:"+TipoCredenzialeMittente.TOKEN_CLIENT_ID.getRawValue()+"'", (tipo.equals(TipoCredenzialeMittente.TOKEN_CLIENT_ID.getRawValue())));
			
			if(!map.containsKey("credenziale")) {
				throw new Exception(msg+"token client id '"+idToken+"' corrisponde a una credenziale mittente ("+map.keySet()+") che non contiene la colonna 'credenziale' ?");
			}
			Object oCredenziale = map.get("credenziale");
			assertTrue(msg+" o credenziale classe '"+oCredenziale.getClass().getName()+"'", (oCredenziale instanceof String));
			String credenziale = null;
			if(oCredenziale instanceof String) {
				credenziale = (String)oCredenziale;
			}
			assertNotNull(msg+" (credenziale string)",credenziale);
			
			String clientIdValue = CredenzialeTokenClient.convertClientIdDBValueToOriginal(credenziale);
			if(clientId!=null) {
				assertNotNull(msg,clientIdValue);
				assertTrue(msg+" clientId tradotto 'found:"+clientIdValue+"' == 'expected:"+clientId+"'", (clientIdValue.equals(clientId)));
			}
			else {
				assertNull(msg,clientIdValue);
			}
			
			IDServizioApplicativo idSA = CredenzialeTokenClient.convertApplicationDBValueToOriginal(credenziale);
			if(idSA!=null) {
				assertNotNull(msg,idSA);
				assertTrue(msg+" applicazione token tradotto 'found:"+idSA.toFormatString()+"' == 'expected:"+idApplicativoToken.toFormatString()+"'", (idSA.equals(idApplicativoToken)));
			}
			else {
				assertNull(msg,idSA);
			}
		}
		else {
			assertNull(msg,oTokenClientId);
		}
		
		
		// diagnostici
		
		if(msgErrore!=null) {
			query = "select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '%"+msgErrore.replaceAll("'", "''")+"%'";
			log().info(query);
		
			count = dbUtils().readValue(query, Integer.class, idTransazione);
			assertTrue(msg+" Cerco dettaglio '"+msgErrore+"'; count trovati: "+count+"", (count>0));
		}

	}

	private static Map<String,Object> getCredenzialiMittenteById(String id) {
		String query = "select * from credenziale_mittente where id="+id;
		log().info(query);
		return dbUtils().readRow(query);
	}
}
