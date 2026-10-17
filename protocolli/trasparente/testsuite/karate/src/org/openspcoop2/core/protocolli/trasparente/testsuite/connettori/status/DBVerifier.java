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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.status;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * Classe di  utilita per le funzioni che interagisocno con il db
 *
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DBVerifier {
	
	private DBVerifier() {}
	
	public static void checkMsgDiag(String idTransazione, String msgRegex) throws SQLQueryObjectException {
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(ConfigLoader.getDbUtils().tipoDatabase);
		query.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
		query.addSelectField(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO);
		query.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE + " = ?");
		query.setANDLogicOperator(true);
		
		List<String> messaggi = new ArrayList<>();
		int[] timeouts = {100, 250, 500, 2000, 5000, 5000};
		
		// il diagnostico atteso potrebbe non essere ancora stato scritto sul db: si riprova finché non compare o non scadono i tentativi
		for (int timeout : timeouts) {
			
			Utilities.sleep(timeout);
			
			List<Map<String, Object>> msgs = ConfigLoader.getDbUtils().readRows(query.createSQLQuery(), idTransazione);
			messaggi.clear();
			if (msgs != null) {
				for (Map<String, Object> msg : msgs) {
					assertTrue(msg.containsKey(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO));
					String message = (String) msg.get(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO);
					if (message.matches(msgRegex))
						return;
					messaggi.add(message);
				}
			}
		}
		
		fail("Diagnostico atteso non trovato per la transazione '" + idTransazione + "'; regex attesa: [" + msgRegex + "]; diagnostici presenti: " + messaggi);
	}
	
	/**
	 * Attende che il timer delle statistiche abbia registrato, tra i dati statistici validi (stato_record &gt;= 1) della tabella indicata,
	 * almeno le transazioni attese sulle operazioni di test del servizio.
	 * Il connettore 'Status' legge i dati statistici e non le transazioni: il tempo necessario dipende dall'intervallo del timer
	 * e dalla durata di un ciclo di generazione (sulle statistiche giornaliere, con molte transazioni nella giornata, anche decine di secondi).
	 */
	public static void waitStatistiche(String tabella, String servizio, TipoServizio tipoServizio, long transazioniAttese, long transazioniOkAttese) throws SQLQueryObjectException {
		String tipoPorta = TipoServizio.FRUIZIONE.equals(tipoServizio) ? "delegata" : "applicativa";
		
		long totale = 0;
		long ok = 0;
		long scadenza = System.currentTimeMillis() + WAIT_STATISTICHE_MS;
		while(System.currentTimeMillis() < scadenza) {
			totale = sommaRichieste(tabella, servizio, tipoPorta, false);
			ok = sommaRichieste(tabella, servizio, tipoPorta, true);
			if(totale >= transazioniAttese && ok >= transazioniOkAttese) {
				ConfigLoader.getLoggerCore().debug("Statistiche ({}) del servizio {} ({}) aggiornate: transazioni {}, ok {}", tabella, servizio, tipoPorta, totale, ok);
				return;
			}
			Utilities.sleep(1000);
		}
		
		fail("Statistiche ("+tabella+") del servizio '"+servizio+"' ("+tipoPorta+") non aggiornate entro "+(WAIT_STATISTICHE_MS/1000)+" secondi; transazioni attese "+transazioniAttese+" (ok "+transazioniOkAttese+"), trovate "+totale+" (ok "+ok+")");
	}
	private static long sommaRichieste(String tabella, String servizio, String tipoPorta, boolean soloOk) throws SQLQueryObjectException {
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(ConfigLoader.getDbUtils().tipoDatabase);
		query.addFromTable(tabella);
		query.addSelectSumField("richieste", "totale");
		query.addWhereCondition("servizio=?");
		query.addWhereCondition("tipo_porta=?");
		query.addWhereCondition("stato_record>=?");
		query.addWhereINCondition("azione", true, AZIONE_TEST_OK, AZIONE_TEST_ERR);
		if(soloOk) {
			query.addWhereCondition("esito=?");
		}
		query.setANDLogicOperator(true);
		
		List<Map<String, Object>> rows = soloOk ?
				ConfigLoader.getDbUtils().readRows(query.createSQLQuery(), servizio, tipoPorta, CostantiDB.STATISTICHE_STATO_RECORD_VALIDO, ESITO_OK) :
				ConfigLoader.getDbUtils().readRows(query.createSQLQuery(), servizio, tipoPorta, CostantiDB.STATISTICHE_STATO_RECORD_VALIDO);
		if(rows!=null && !rows.isEmpty() && rows.get(0)!=null && rows.get(0).get("totale") instanceof Number) {
			return ((Number)rows.get(0).get("totale")).longValue();
		}
		return 0;
	}
	private static final long WAIT_STATISTICHE_MS = 180000;
	private static final String AZIONE_TEST_OK = "test1";
	private static final String AZIONE_TEST_ERR = "test3";
	private static final int ESITO_OK = 0;
	
	public static void resetStatistiche(String servizio) throws SQLQueryObjectException {
		ISQLQueryObject query;
		
		// rimuovo messaggi diagnostici
		ISQLQueryObject idTransazioni = SQLObjectFactory.createSQLQueryObject(ConfigLoader.getDbUtils().tipoDatabase);
		idTransazioni.addFromTable(CostantiDB.TRANSAZIONI);
		idTransazioni.addSelectField("id");
		idTransazioni.addWhereCondition("nome_servizio=?");
		
		query = SQLObjectFactory.createSQLQueryObject(ConfigLoader.getDbUtils().tipoDatabase);
		query.addDeleteTable(CostantiDB.MSG_DIAGNOSTICI);
		query.addWhereINSelectSQLCondition(true, "id_transazione", idTransazioni);
		ConfigLoader.getDbUtils().update(query.createSQLDelete(), servizio);
		
		// rimuovo transazioni
		query = SQLObjectFactory.createSQLQueryObject(ConfigLoader.getDbUtils().tipoDatabase);
		query.addDeleteTable(CostantiDB.TRANSAZIONI);
		query.addWhereCondition("nome_servizio=?");
		ConfigLoader.getDbUtils().update(query.createSQLDelete(), servizio);
		
		
		// rimuovo statistiche
		String[] statistics = {
				CostantiDB.STATISTICHE_ORARIE,
				CostantiDB.STATISTICHE_GIORNALIERE,
				CostantiDB.STATISTICHE_SETTIMANALI,
				CostantiDB.STATISTICHE_MENSILI,
		};
		
		for (String statistic : statistics) {
			query = SQLObjectFactory.createSQLQueryObject(ConfigLoader.getDbUtils().tipoDatabase);
			query.addDeleteTable(statistic);
			query.addWhereCondition("servizio=?");
			ConfigLoader.getDbUtils().update(query.createSQLDelete(), servizio);
		}
	}
	
}
