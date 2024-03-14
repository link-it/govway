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

package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DbUtils;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**
* DBVerifier
*
* @author Andrea Poli (apoli@link.it)
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
	
	public static void verify(String api, String operazione, String credenzialeUnivoca,
			long esitoExpected, FaseTracciamento faseTracciamento, String diagnosticoErrore, boolean error, String ... detail) throws Exception  {
		verify(null, api, operazione, credenzialeUnivoca,
				esitoExpected, faseTracciamento, diagnosticoErrore, error, detail);
	}
	public static void verify(String idTransazione,
			long esitoExpected, FaseTracciamento faseTracciamento, String diagnosticoErrore, boolean error, String ... detail) throws Exception  {
		verify(idTransazione, null, null, null,
				esitoExpected, faseTracciamento, diagnosticoErrore, error, detail);
	}
	public static void verify(
			String idTransazione, String api, String operazione, String credenzialeUnivoca,
			long esitoExpected, FaseTracciamento faseTracciamento, String diagnosticoErrore, boolean error, String ... detail) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		if(FaseTracciamento.POST_OUT_RESPONSE.equals(faseTracciamento)) {
			Utilities.sleep(100);
		}
		try {
			DBVerifier._verify(idTransazione, api, operazione, credenzialeUnivoca, 
					esitoExpected, faseTracciamento, diagnosticoErrore, error, detail);
		}catch(Throwable t) {
			if(!FaseTracciamento.POST_OUT_RESPONSE.equals(faseTracciamento)) {
				throw t;
			}
			Utilities.sleep(500);
			try {
				DBVerifier._verify(idTransazione, api, operazione, credenzialeUnivoca, 
						esitoExpected, faseTracciamento, diagnosticoErrore, error, detail);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verify(idTransazione, api, operazione, credenzialeUnivoca, 
							esitoExpected, faseTracciamento, diagnosticoErrore, error, detail);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier._verify(idTransazione, api, operazione, credenzialeUnivoca, 
							esitoExpected, faseTracciamento, diagnosticoErrore, error, detail);
				}
			}
		}
	}
	
	private static void _verify(String idTransazione, String api, String operazione, String credenzialeUnivoca,
			long esitoExpected, FaseTracciamento faseTracciamento, String diagnosticoErrore, boolean error, String ... detail) throws Exception  {
		
		
		String query = "select count(*) from transazioni where ";
		if(idTransazione!=null) {
			query = query + "id = ?";
		}
		else {
			query = query + "nome_servizio = ? AND azione = ? AND credenziali LIKE '%"+credenzialeUnivoca+"%'";
		}
		log().info("exists: "+query);
		
		int count = idTransazione!=null ? 
				dbUtils().readValue(query, Integer.class, idTransazione)
				:
				dbUtils().readValue(query, Integer.class, api, operazione)
				;
		assertEquals("IdTransazione: "+idTransazione, 1, count);

		
		
		query = "select esito,esito_contesto,error_log,warning_log from transazioni where ";
		if(idTransazione!=null) {
			query = query + "id = ?";
		}
		else {
			query = query + "nome_servizio = ? AND azione = ? AND credenziali LIKE '%"+credenzialeUnivoca+"%'";
		}
		log().info(query);
		
		String msg = "IdTransazione: "+idTransazione+" api:"+api+" operazione:"+operazione+"";
		
		List<Map<String, Object>> rows = idTransazione!=null ?
				dbUtils().readRows(query, idTransazione)
				:
				dbUtils().readRows(query, api, operazione)
				;
		assertNotNull(msg, rows);
		assertEquals(msg, 1, rows.size());
					
		Long esito = null;
		String esitoContesto = null;
		String logDetail = null;
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

		Object oEsitoContesto = row.get("esito_contesto");
		assertNotNull(msg,oEsitoContesto);
		assertTrue(msg+" oEsitoContesto classe '"+oEsitoContesto.getClass().getName()+"'", (oEsitoContesto instanceof String));
		if(oEsitoContesto instanceof String) {
			esitoContesto = (String)oEsitoContesto;
		}
		String esitoContestoExpected = "standard";
		esitoContestoExpected = EsitoUtils.buildEsitoContext(esitoContestoExpected, faseTracciamento);
		assertEquals(msg,esitoContestoExpected, esitoContesto);
		
		Object oWarn = row.get("warning_log");
		if(error || detail==null || detail.length<=0) {
			assertNull(msg+" warn["+oWarn+"]",oWarn);
		}
		else {
			assertNotNull(msg,oWarn);
			assertTrue(msg+" oWarn classe '"+oWarn.getClass().getName()+"'", (oWarn instanceof String));
			if(oWarn instanceof String) {
				logDetail = (String)oWarn;
			}
			if(detail!=null && detail.length>0) {
				for (String d : detail) {
					boolean contains = logDetail.contains(d);
					assertTrue(msg+" ["+logDetail+"] contains ["+d+"]",contains);
				}
			}
		}
		
		Object oError = row.get("error_log");
		if(!error || detail==null || detail.length<=0) {
			assertNull(msg+" error["+oError+"]",oError);
		}
		else {
			assertNotNull(msg,oError);
			assertTrue(msg+" oError classe '"+oError.getClass().getName()+"'", (oError instanceof String));
			if(oError instanceof String) {
				logDetail = (String)oError;
			}
			if(detail!=null && detail.length>0) {
				for (String d : detail) {
					boolean contains = logDetail.contains(d);
					assertTrue(msg+" ["+logDetail+"] contains ["+d+"]",contains);
				}
			}
		}
		

		// diagnostici
		
		if(diagnosticoErrore!=null) {
			query = "select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '%"+diagnosticoErrore.replaceAll("'", "''")+"%'";
			log().info(query);
		
			count = dbUtils().readValue(query, Integer.class, idTransazione);
			assertTrue(msg+" Cerco dettaglio '"+diagnosticoErrore+"'; count trovati: "+count+"", (count>0));
		}

	}

	public static void verifyNotExpected(String api, String operazione, String credenzialeUnivoca, FaseTracciamento faseTracciamento) throws Exception  {
		verifyNotExpected(null, api, operazione, credenzialeUnivoca, faseTracciamento);
	}
	public static void verifyNotExpected(String idTransazione, FaseTracciamento faseTracciamento) throws Exception  {
		verifyNotExpected(idTransazione, null, null, null, faseTracciamento);
	}
	public static void verifyNotExpected(String idTransazione, String api, String operazione, String credenzialeUnivoca, FaseTracciamento faseTracciamento) throws Exception  {
		
		String esitoContestoExpected = "standard";
		esitoContestoExpected = EsitoUtils.buildEsitoContext(esitoContestoExpected, faseTracciamento);
		
		String query = "select count(*) from transazioni where esito_contesto=? AND ";
		if(idTransazione!=null) {
			query = query + "id = ?";
		}
		else {
			query = query + "nome_servizio = ? AND azione = ? AND credenziali LIKE '%"+credenzialeUnivoca+"%'";
		}
		log().info("notExists: "+query);
		
		int count = idTransazione!=null ? 
				dbUtils().readValue(query, Integer.class, esitoContestoExpected, idTransazione)
				:
				dbUtils().readValue(query, Integer.class, esitoContestoExpected, api, operazione)
				;
		assertEquals("IdTransazione: "+idTransazione, 0, count);
		
	}
	
	
	
	public static void verifyInfo(String idTransazione, List<String> tokenInfoCheck, boolean tempiElaborazioneExpected ) throws Exception  {
		
		String query = "select token_info, tempi_elaborazione from transazioni where id=?";
		log().info("select: "+query);
		
		String msg = "IdTransazione: "+idTransazione;
		
		List<Map<String, Object>> rows = dbUtils().readRows(query, idTransazione);
		assertNotNull(msg, rows);
		assertEquals(msg, 1, rows.size());
					
		Map<String, Object> row = rows.get(0);
		for (String key : row.keySet()) {
			log().debug("Row["+key+"]="+row.get(key));
		}
		
		Object otoken_info = row.get("token_info");
		if(tokenInfoCheck!=null && tokenInfoCheck.size()>0) {
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
		
		Object oTempiElaborazione = row.get("tempi_elaborazione");
		if(tempiElaborazioneExpected) {
			assertNotNull(msg,oTempiElaborazione);
			assertTrue(msg+" oTempiElaborazione classe '"+oTempiElaborazione.getClass().getName()+"'", (oTempiElaborazione instanceof String));
			String tempiElaborazione = null;
			if(oTempiElaborazione instanceof String) {
				tempiElaborazione = (String)oTempiElaborazione;
			}
			assertNotNull(msg+" (tempiElaborazione string)",tempiElaborazione);
			assertTrue(msg+" tempiElaborazione ("+tempiElaborazione+")", StringUtils.isNoneEmpty(tempiElaborazione.trim()));
		}
		else {
			assertNull(msg,oTempiElaborazione);
		}
	}
	
	public static void verifyDumpMessaggio(String api, String operazione, String credenzialeUnivoca, FaseTracciamento faseTracciamento, boolean expected) throws Exception  {
		verifyDumpMessaggio(null, api, operazione, credenzialeUnivoca, "RichiestaIngressoDumpBinario", expected);
		if(FaseTracciamento.OUT_RESPONSE.equals(faseTracciamento)) {
			verifyDumpMessaggio(null, api, operazione, credenzialeUnivoca, "RichiestaUscitaDumpBinario", expected);
			verifyDumpMessaggio(null, api, operazione, credenzialeUnivoca, "RispostaIngressoDumpBinario", expected);
		}
	}
	public static void verifyDumpMessaggio(String idTransazione, FaseTracciamento faseTracciamento, boolean expected) throws Exception  {
		verifyDumpMessaggio(idTransazione, null, null, null, "RichiestaIngressoDumpBinario", expected);
		if(FaseTracciamento.OUT_RESPONSE.equals(faseTracciamento) || FaseTracciamento.POST_OUT_RESPONSE.equals(faseTracciamento)) {
			verifyDumpMessaggio(idTransazione, null, null, null, "RichiestaUscitaDumpBinario", expected);
			verifyDumpMessaggio(idTransazione, null, null, null, "RispostaIngressoDumpBinario", expected);
		}
		if(FaseTracciamento.POST_OUT_RESPONSE.equals(faseTracciamento)) {
			verifyDumpMessaggio(idTransazione, null, null, null, "RispostaUscitaDumpBinario", expected);
		}
	}
	public static void verifyDumpMessaggio(String idTransazione, String api, String operazione, String credenzialeUnivoca, String tipo, boolean expected) throws Exception  {
		
		if(idTransazione==null) {
			String query = "select id from transazioni where ";
			query = query + "nome_servizio = ? AND azione = ? AND credenziali LIKE '%"+credenzialeUnivoca+"%'";
			log().info(query);
			
			String msg = "api:"+api+" operazione:"+operazione+"";
			
			List<Map<String, Object>> rows = dbUtils().readRows(query, api, operazione);
			assertNotNull(msg, rows);
			assertEquals(msg, 1, rows.size());
			
			Map<String, Object> row = rows.get(0);
			
			Object oId = row.get("id");
			assertNotNull(msg,oId);
			assertTrue(msg+" oId classe '"+oId.getClass().getName()+"'", (oId instanceof String));
			if(oId instanceof String) {
				idTransazione = (String)oId;
			}
		}
		
		String query = "select * from dump_messaggi where tipo_messaggio = '"+tipo+"' and id_transazione=?";
		log().info("EXISTS:"+expected+" -> " +query);
		
		String msg = "IdTransazione: "+idTransazione+ " api:"+api+" operazione:"+operazione+" dump: "+tipo;
		
		List<Map<String, Object>> rows = dbUtils().readRows(query, idTransazione);
				
		if(expected) {
			assertNotNull(msg, rows);
			
			assertEquals(msg, 1, rows.size());
			
			Map<String, Object> row = rows.get(0);
			
			Object oBody = row.get("body");
			assertNotNull(msg,oBody);
			byte[] content = (byte[]) oBody;
			String sContent = new String(content);
			assertNotNull(msg,sContent);
		}
		else {
			assertTrue(msg, rows==null || (rows.size()==0));
		}
		
	}
}
