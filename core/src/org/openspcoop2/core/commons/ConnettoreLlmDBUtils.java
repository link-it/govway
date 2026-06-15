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
package org.openspcoop2.core.commons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * Primitive di basso livello sulle tabelle satellite LLM associate a un connettore:
 * <ul>
 *   <li>{@code connettori_llm}: lega un connettore principale di tipo 'disabilitato'
 *       (container per API LLM) ai provider concreti referenziati per id</li>
 *   <li>{@code connettori_llm_binding}: elenca i LLM Provider Binding (modelli)
 *       esposti da un connettore provider concreto</li>
 * </ul>
 *
 * Le primitive sono SQL-only (nessuna dipendenza dai bean dei due namespace
 * config/registry) cosi' da poter essere usate da entrambi i driver. Le
 * traduzioni bean &lt;-&gt; primitive (lookup-by-name del provider concreto,
 * CRUDConnettore del concreto in cascata, ecc.) sono responsabilita' dei
 * rispettivi {@code _connettoriLIB.java}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class ConnettoreLlmDBUtils {

	private ConnettoreLlmDBUtils() {
		// utility
	}

	/**
	 * Cancella tutte le righe nelle tabelle satellite che fanno riferimento a
	 * {@code idConnettore}: i suoi binding e tutti i link in cui appare come
	 * principale o come provider concreto. Da chiamare prima della delete fisica
	 * del connettore per evitare violazioni di FK.
	 */
	public static void deleteAllForConnettore(long idConnettore, Connection con, String tipoDB)
			throws SQLException, SQLQueryObjectException {
		// binding del connettore (se e' un concreto)
		deleteBindings(idConnettore, con, tipoDB);
		// link in cui appare come container
		ISQLQueryObject qPrinc = SQLObjectFactory.createSQLQueryObject(tipoDB);
		qPrinc.addDeleteTable(CostantiDB.CONNETTORI_LLM);
		qPrinc.addWhereCondition(CostantiDB.CONNETTORI_LLM_COLUMN_ID_CONNETTORE_PRINCIPALE + "=?");
		try (PreparedStatement stm = con.prepareStatement(qPrinc.createSQLDelete())) {
			stm.setLong(1, idConnettore);
			stm.executeUpdate();
		}
		// link in cui appare come provider concreto
		ISQLQueryObject qProv = SQLObjectFactory.createSQLQueryObject(tipoDB);
		qProv.addDeleteTable(CostantiDB.CONNETTORI_LLM);
		qProv.addWhereCondition(CostantiDB.CONNETTORI_LLM_COLUMN_ID_CONNETTORE + "=?");
		try (PreparedStatement stm = con.prepareStatement(qProv.createSQLDelete())) {
			stm.setLong(1, idConnettore);
			stm.executeUpdate();
		}
	}

	/**
	 * Elenca gli id dei connettori provider concreti appesi al container indicato,
	 * nell'ordine di inserimento.
	 */
	public static List<Long> listProviderIds(long idContainer, Connection con, String tipoDB)
			throws SQLException, SQLQueryObjectException {
		List<Long> result = new ArrayList<>();
		ISQLQueryObject q = SQLObjectFactory.createSQLQueryObject(tipoDB);
		q.addFromTable(CostantiDB.CONNETTORI_LLM);
		q.addSelectField(CostantiDB.CONNETTORI_LLM_COLUMN_ID_CONNETTORE);
		q.addWhereCondition(CostantiDB.CONNETTORI_LLM_COLUMN_ID_CONNETTORE_PRINCIPALE + "=?");
		q.addOrderBy("id", true);
		try (PreparedStatement stm = con.prepareStatement(q.createSQLQuery())) {
			stm.setLong(1, idContainer);
			try (ResultSet rs = stm.executeQuery()) {
				while (rs.next()) {
					result.add(rs.getLong(CostantiDB.CONNETTORI_LLM_COLUMN_ID_CONNETTORE));
				}
			}
		}
		return result;
	}

	/**
	 * Rimuove tutti i link {@code connettori_llm} per il container indicato.
	 * Da chiamare prima di reinserire la lista aggiornata dei provider in UPDATE.
	 */
	public static void deleteLinksByContainer(long idContainer, Connection con, String tipoDB)
			throws SQLException, SQLQueryObjectException {
		ISQLQueryObject q = SQLObjectFactory.createSQLQueryObject(tipoDB);
		q.addDeleteTable(CostantiDB.CONNETTORI_LLM);
		q.addWhereCondition(CostantiDB.CONNETTORI_LLM_COLUMN_ID_CONNETTORE_PRINCIPALE + "=?");
		try (PreparedStatement stm = con.prepareStatement(q.createSQLDelete())) {
			stm.setLong(1, idContainer);
			stm.executeUpdate();
		}
	}

	/**
	 * Inserisce un legame container-&gt;provider concreto.
	 */
	public static void link(long idContainer, long idProvider, Connection con, String tipoDB)
			throws SQLException, SQLQueryObjectException {
		ISQLQueryObject q = SQLObjectFactory.createSQLQueryObject(tipoDB);
		q.addInsertTable(CostantiDB.CONNETTORI_LLM);
		q.addInsertField(CostantiDB.CONNETTORI_LLM_COLUMN_ID_CONNETTORE_PRINCIPALE, "?");
		q.addInsertField(CostantiDB.CONNETTORI_LLM_COLUMN_ID_CONNETTORE, "?");
		try (PreparedStatement stm = con.prepareStatement(q.createSQLInsert())) {
			stm.setLong(1, idContainer);
			stm.setLong(2, idProvider);
			stm.executeUpdate();
		}
	}

	/**
	 * Riscrive l'insieme dei binding di un connettore (delete-and-insert).
	 * Se {@code bindingNames} e' null o vuoto, rimuove solo gli esistenti.
	 */
	public static void writeBindings(long idConnettore, List<String> bindingNames, Connection con, String tipoDB)
			throws SQLException, SQLQueryObjectException {
		deleteBindings(idConnettore, con, tipoDB);
		if (bindingNames == null || bindingNames.isEmpty()) {
			return;
		}
		ISQLQueryObject q = SQLObjectFactory.createSQLQueryObject(tipoDB);
		q.addInsertTable(CostantiDB.CONNETTORI_LLM_BINDING);
		q.addInsertField(CostantiDB.CONNETTORI_LLM_BINDING_COLUMN_NOME_BINDING, "?");
		q.addInsertField(CostantiDB.CONNETTORI_LLM_BINDING_COLUMN_ID_CONNETTORE, "?");
		try (PreparedStatement stm = con.prepareStatement(q.createSQLInsert())) {
			for (String nome : bindingNames) {
				if (nome == null || nome.isEmpty()) {
					continue;
				}
				stm.setString(1, nome);
				stm.setLong(2, idConnettore);
				stm.executeUpdate();
			}
		}
	}

	/**
	 * Legge la lista dei binding associati a un connettore (provider concreto),
	 * nell'ordine di inserimento.
	 */
	public static List<String> readBindings(long idConnettore, Connection con, String tipoDB)
			throws SQLException, SQLQueryObjectException {
		List<String> result = new ArrayList<>();
		ISQLQueryObject q = SQLObjectFactory.createSQLQueryObject(tipoDB);
		q.addFromTable(CostantiDB.CONNETTORI_LLM_BINDING);
		q.addSelectField(CostantiDB.CONNETTORI_LLM_BINDING_COLUMN_NOME_BINDING);
		q.addWhereCondition(CostantiDB.CONNETTORI_LLM_BINDING_COLUMN_ID_CONNETTORE + "=?");
		q.addOrderBy("id", true);
		try (PreparedStatement stm = con.prepareStatement(q.createSQLQuery())) {
			stm.setLong(1, idConnettore);
			try (ResultSet rs = stm.executeQuery()) {
				while (rs.next()) {
					result.add(rs.getString(CostantiDB.CONNETTORI_LLM_BINDING_COLUMN_NOME_BINDING));
				}
			}
		}
		return result;
	}

	private static void deleteBindings(long idConnettore, Connection con, String tipoDB)
			throws SQLException, SQLQueryObjectException {
		ISQLQueryObject q = SQLObjectFactory.createSQLQueryObject(tipoDB);
		q.addDeleteTable(CostantiDB.CONNETTORI_LLM_BINDING);
		q.addWhereCondition(CostantiDB.CONNETTORI_LLM_BINDING_COLUMN_ID_CONNETTORE + "=?");
		try (PreparedStatement stm = con.prepareStatement(q.createSQLDelete())) {
			stm.setLong(1, idConnettore);
			stm.executeUpdate();
		}
	}

	/**
	 * Cerca l'id di un connettore per {@code nome_connettore}. Ritorna -1 se non
	 * esiste.
	 */
	public static long lookupConnettoreIdByNome(String nomeConnettore, Connection con, String tipoDB)
			throws SQLException, SQLQueryObjectException {
		ISQLQueryObject q = SQLObjectFactory.createSQLQueryObject(tipoDB);
		q.addFromTable(CostantiDB.CONNETTORI);
		q.addSelectField("id");
		q.addWhereCondition(CostantiDB.CONNETTORI_COLUMN_NOME + "=?");
		try (PreparedStatement stm = con.prepareStatement(q.createSQLQuery())) {
			stm.setString(1, nomeConnettore);
			try (ResultSet rs = stm.executeQuery()) {
				if (rs.next()) {
					return rs.getLong("id");
				}
				return -1;
			}
		}
	}
}
