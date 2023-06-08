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

package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.identificazione_azione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	private DBVerifier() {}
	
	private static DbUtils dbUtils() {
		return ConfigLoader.getDbUtils();
	}
	
	private static Logger log() {
		return ConfigLoader.getLoggerCore();
	}
	
	public static void verify(String idTransazione, 
			long esitoExpected, String azioneAttesa, String msgErrore)  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier.verifyEngine(idTransazione, 
					esitoExpected, azioneAttesa, msgErrore);
		}catch(Exception t) {
			Utilities.sleep(500);
			try {
				DBVerifier.verifyEngine(idTransazione, 
						esitoExpected, azioneAttesa, msgErrore);
			}catch(Exception t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier.verifyEngine(idTransazione, 
							esitoExpected, azioneAttesa, msgErrore);
				}catch(Exception t3) {
					Utilities.sleep(5000);
					DBVerifier.verifyEngine(idTransazione, 
							esitoExpected, azioneAttesa, msgErrore);
				}
			}
		}
	}
	
	private static void verifyEngine(String idTransazione, 
			long esitoExpected, String azioneAttesa, String msgErrore)  {
		
		
		String query = "select count(*) from transazioni where id = ?";
		log().info(query);
		
		int count = dbUtils().readValue(query, Integer.class, idTransazione);
		assertEquals("IdTransazione: "+idTransazione, 1, count);

		
		
		query = "select esito,azione  from transazioni where id = ?";
		log().info(query);
		
		String msg = "IdTransazione: "+idTransazione;
		
		List<Map<String, Object>> rows = dbUtils().readRows(query, idTransazione);
		assertNotNull(msg, rows);
		assertEquals(msg, 1, rows.size());
					
		Long esito = null;
		Map<String, Object> row = rows.get(0);
		for (Entry<String, Object> entry : row.entrySet()) {
			String msgc = "Row["+entry.getKey()+"]="+entry.getValue();
			log().debug(msgc);
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
		
		String azione = null;
		Object oAzione = row.get("azione");
		assertNotNull(msg,oAzione);
		assertTrue(msg+" oAzione classe '"+oAzione.getClass().getName()+"'", (oAzione instanceof String));
		if(oAzione instanceof String) {
			azione = (String)oAzione ;
		}
		assertEquals(msg,azioneAttesa, azione);

		
		
		// diagnostici
		
		if(msgErrore!=null) {
			query = "select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '%"+msgErrore.replace("'", "''")+"%'";
			log().info(query);
		
			count = dbUtils().readValue(query, Integer.class, idTransazione);
			assertTrue(msg+" Cerco dettaglio '"+msgErrore+"'; count trovati: "+count+"", (count>0));
		}

	}
	
}
