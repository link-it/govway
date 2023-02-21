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
package org.openspcoop2.core.config.driver.db;

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_soggettiLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_soggettiLIB {

	/**
	 * CRUD oggetto Soggetto Non si occupa di chiudere la connessione con
	 * il db in caso di errore in quanto verra' gestita dal metodo chiamante
	 * 
	 * @param type
	 *            Tipo operazione {1 (CREATE),2 (UPDATE),3 (DELETE)}
	 * @param soggetto
	 * @return L'ID dell'oggetto creato in caso di CREATE, altrimenti il numero
	 *         di righe che sono state modificate/cancellate
	 * @throws DriverConfigurazioneException
	 */
	public static long CRUDSoggetto(int type, org.openspcoop2.core.config.Soggetto soggetto, Connection con) throws DriverConfigurazioneException {
		if (soggetto == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSoggetto] Parametro non valido.");

		String nome = soggetto.getNome();
		String tipo = soggetto.getTipo();

		if (nome == null || nome.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSoggetto] Parametro Nome non valido.");
		if (tipo == null || tipo.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSoggetto] Parametro Tipo non valido.");

		String descrizione = soggetto.getDescrizione();
		String identificativoPorta = soggetto.getIdentificativoPorta();
		String superuser = soggetto.getSuperUser();

		boolean router = soggetto.getRouter();
		boolean isDefault = soggetto.isDominioDefault();
		
		String pdUrlPrefixRewriter = soggetto.getPdUrlPrefixRewriter();
		String paUrlPrefixRewriter = soggetto.getPaUrlPrefixRewriter();
		
		PreparedStatement updateStmt = null;
		PreparedStatement selectStmt = null;
		String updateQuery = "";
		String selectQuery = "";
		ResultSet selectRS = null;
		int n = 0;
		try {

			// preparo lo statement in base al tipo di operazione
			switch (type) {
			case CREATE:
				// CREATE
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(DriverConfigurazioneDB_LIB.tabellaSoggetti);
				sqlQueryObject.addInsertField("nome_soggetto", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("identificativo_porta", "?");
				sqlQueryObject.addInsertField("tipo_soggetto", "?");
				sqlQueryObject.addInsertField("is_router", "?");
				sqlQueryObject.addInsertField("is_default", "?");
				sqlQueryObject.addInsertField("superuser", "?");
				sqlQueryObject.addInsertField("pd_url_prefix_rewriter", "?");
				sqlQueryObject.addInsertField("pa_url_prefix_rewriter", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				int index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, identificativoPorta);
				updateStmt.setString(index++, tipo);
				updateStmt.setInt(index++, (router ? CostantiDB.TRUE : CostantiDB.FALSE));
				updateStmt.setInt(index++, (isDefault ? CostantiDB.TRUE : CostantiDB.FALSE));
				updateStmt.setString(index++, superuser);
				updateStmt.setString(index++, pdUrlPrefixRewriter);
				updateStmt.setString(index++, paUrlPrefixRewriter);
				// eseguo lo statement
				n = updateStmt.executeUpdate();

				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto CREATE : \n" + DBUtils.formatSQLString(updateQuery, nome, descrizione, identificativoPorta, tipo, router, superuser));
				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(DriverConfigurazioneDB_LIB.tabellaSoggetti);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
				sqlQueryObject.addWhereCondition("nome_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, tipo);
				selectStmt.setString(2, nome);

				break;

			case UPDATE:
				// UPDATE
				String oldNomeSoggetto = null;
				String oldTipoSoggetto = null;
				if(soggetto.getOldIDSoggettoForUpdate()!=null){
					oldNomeSoggetto = soggetto.getOldIDSoggettoForUpdate().getNome();
					oldTipoSoggetto = soggetto.getOldIDSoggettoForUpdate().getTipo();
				}
				// controllo se sono presenti i campi necessari per
				// l'aggiornamento
				if (oldNomeSoggetto == null || oldNomeSoggetto.equals(""))
					oldNomeSoggetto = nome;
				if (oldTipoSoggetto == null || oldTipoSoggetto.equals(""))
					oldTipoSoggetto = tipo;

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(DriverConfigurazioneDB_LIB.tabellaSoggetti);
				sqlQueryObject.addUpdateField("nome_soggetto", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("identificativo_porta", "?");
				sqlQueryObject.addUpdateField("tipo_soggetto", "?");
				sqlQueryObject.addUpdateField("is_router", "?");
				sqlQueryObject.addUpdateField("is_default", "?");
				sqlQueryObject.addUpdateField("superuser", "?");
				sqlQueryObject.addUpdateField("pd_url_prefix_rewriter", "?");
				sqlQueryObject.addUpdateField("pa_url_prefix_rewriter", "?");
				sqlQueryObject.addWhereCondition("nome_soggetto=?");
				sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, identificativoPorta);
				updateStmt.setString(index++, tipo);
				updateStmt.setInt(index++, (router ? CostantiDB.TRUE : CostantiDB.FALSE));
				updateStmt.setInt(index++, (isDefault ? CostantiDB.TRUE : CostantiDB.FALSE));
				updateStmt.setString(index++, superuser);
				updateStmt.setString(index++, pdUrlPrefixRewriter);
				updateStmt.setString(index++, paUrlPrefixRewriter);
				updateStmt.setString(index++, oldNomeSoggetto);
				updateStmt.setString(index++, oldTipoSoggetto);
				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();

				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto UPDATE : \n" + DBUtils.formatSQLString(updateQuery, nome, descrizione, identificativoPorta, tipo, router, soggetto.getId()));
				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n);
				break;

			case DELETE:
				// DELETE
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(DriverConfigurazioneDB_LIB.tabellaSoggetti);
				sqlQueryObject.addWhereCondition("nome_soggetto=?");
				sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setString(1, soggetto.getNome());
				updateStmt.setString(2, soggetto.getTipo());
				n = updateStmt.executeUpdate();
				updateStmt.close();

				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n);
				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto DELETE : \n" + DBUtils.formatSQLString(updateQuery, soggetto.getNome(), soggetto.getTipo()));

				break;
			}

			// in caso di create eseguo la select e ritorno l'id dell'oggetto
			// creato
			if (type == CostantiDB.CREATE) {
				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {
					soggetto.setId(selectRS.getLong("id"));
					return soggetto.getId();
				}
			}

			return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSoggetto] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSoggetto] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(selectRS!=null) 
					selectRS.close();
			} catch (Exception e) {
				// ignore
			}
			try {
				if(selectStmt!=null) 
					selectStmt.close();
			} catch (Exception e) {
				// ignore
			}
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	
}
