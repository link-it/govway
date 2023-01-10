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

package org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509;

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
		return ConfigLoader.getLoggerCore();
	}
	
	public static void verify(String idTransazione, 
			long esitoExpected, String msgErrore, 
			String applicativoMittente, String msgRequestDetail, String msgResponseDetail) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier._verify(idTransazione, 
					esitoExpected, msgErrore, 
					applicativoMittente, msgRequestDetail, msgResponseDetail);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier._verify(idTransazione, 
						esitoExpected, msgErrore, 
						applicativoMittente, msgRequestDetail, msgResponseDetail);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verify(idTransazione, 
							esitoExpected, msgErrore, 
							applicativoMittente, msgRequestDetail, msgResponseDetail);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier._verify(idTransazione, 
							esitoExpected, msgErrore, 
							applicativoMittente, msgRequestDetail, msgResponseDetail);
				}
			}
		}
	}
	
	private static void _verify(String idTransazione, 
			long esitoExpected, String msgErrore, 
			String applicativoMittente, String msgRequestDetail, String msgResponseDetail) throws Exception  {
		
		
		String query = "select count(*) from transazioni where id = ?";
		log().info(query);
		
		int count = dbUtils().readValue(query, Integer.class, idTransazione);
		assertEquals("IdTransazione: "+idTransazione, 1, count);

		
		
		query = "select esito,servizio_applicativo_fruitore,pdd_ruolo from transazioni where id = ?";
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

		Object opdd_ruolo = row.get("pdd_ruolo");
		assertNotNull(msg,opdd_ruolo);
		assertTrue(msg+" opdd_ruolo classe '"+opdd_ruolo.getClass().getName()+"'", (opdd_ruolo instanceof String));
		String pdd_ruolo = null;
		if(opdd_ruolo instanceof String) {
			pdd_ruolo = (String)opdd_ruolo;
		}
		assertNotNull(msg+" (pdd_ruolo string)",pdd_ruolo);
		String tipoMessaggioRichiesta = "RichiestaIngressoDumpBinario";
		String tipoMessaggioRisposta = "RispostaUscitaDumpBinario";
		if("delegata".equals(pdd_ruolo)) {
			tipoMessaggioRichiesta = "RichiestaUscitaDumpBinario";
			tipoMessaggioRisposta = "RispostaIngressoDumpBinario";
		}
		
		if(applicativoMittente!=null) {
			Object oapplicativo_mittente = row.get("servizio_applicativo_fruitore");
			assertNotNull(msg,oapplicativo_mittente);
			assertTrue(msg+" oapplicativo_mittente classe '"+oapplicativo_mittente.getClass().getName()+"'", (oapplicativo_mittente instanceof String));
			String applicativo_mittente = null;
			if(oapplicativo_mittente instanceof String) {
				applicativo_mittente = (String)oapplicativo_mittente;
			}
			assertNotNull(msg+" (applicativo_mittente string)",applicativo_mittente);
			assertTrue(msg+" mittente is '"+applicativoMittente+"' (applicativo_mittente:"+applicativo_mittente+")", (applicativoMittente.equals(applicativo_mittente)));
		}
		
		// diagnostici
		
		if(msgErrore!=null) {
			query = "select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '%"+msgErrore.replaceAll("'", "''")+"%'";
			log().info(query);
		
			count = dbUtils().readValue(query, Integer.class, idTransazione);
			assertTrue(msg+" Cerco dettaglio '"+msgErrore+"'; count trovati: "+count+"", (count>0));
		}

		if(msgRequestDetail!=null) {
			query = "select body from dump_messaggi where id_transazione = ? and tipo_messaggio = '"+tipoMessaggioRichiesta+"'";
			log().info(query);
			
			rows = dbUtils().readRows(query, idTransazione);
			assertNotNull(msg, rows);
			assertEquals(msg, 1, rows.size());
			
			row = rows.get(0);
			
			Object oBody = row.get("body");
			assertNotNull(msg,oBody);
			byte[] content = (byte[]) oBody;
			String sContent = new String(content);
			assertTrue(msg+" payload["+sContent+"] contains '"+msgRequestDetail+"'", sContent.contains(msgRequestDetail));
		}
		
		if(msgResponseDetail!=null) {
			query = "select body from dump_messaggi where id_transazione = ? and tipo_messaggio = '"+tipoMessaggioRisposta+"'";
			log().info(query);
			
			rows = dbUtils().readRows(query, idTransazione);
			assertNotNull(msg, rows);
			assertEquals(msg, 1, rows.size());
			
			row = rows.get(0);
			
			Object oBody = row.get("body");
			assertNotNull(msg,oBody);
			byte[] content = (byte[]) oBody;
			String sContent = new String(content);
			assertTrue(msg+" payload["+sContent+"] contains '"+msgResponseDetail+"'", sContent.contains(msgResponseDetail));
		}
		
	}

	
	public static String getIdTransazione(Date now, String idApplicativo) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			return DBVerifier._getIdTransazione(now, idApplicativo);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				return DBVerifier._getIdTransazione(now, idApplicativo);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					return DBVerifier._getIdTransazione(now, idApplicativo);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					return DBVerifier._getIdTransazione(now, idApplicativo);
				}
			}
		}
	}
	
	private static String _getIdTransazione(Date now, String idApplicativo) throws Exception  {
		
		
		String query = "select id from transazioni where data_ingresso_richiesta > ? AND id_correlazione_applicativa = ?";
		log().info(query);
		
		String idTransazione = dbUtils().readValue(query, String.class, new java.sql.Timestamp(now.getTime()), idApplicativo);
		assertNotNull("IdTransazione letto da idApplicativo: "+idApplicativo, idTransazione);

		return idTransazione;

	}
}
