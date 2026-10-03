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
