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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.ProtocolProperty;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.FiltroRicercaProtocolPropertyConfig;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.mapping.DBProtocolPropertiesUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * DriverConfigurazioneDB_protocolPropertiesDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_protocolPropertiesDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_protocolPropertiesDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	
	protected boolean existsProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome) throws DriverConfigurazioneException {

		Connection connection;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsProtocolProperty");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("DriverConfigurazioneDB::existsProtocolProperty] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);
		try {
			return DBProtocolPropertiesUtils.existsProtocolProperty(proprietarioProtocolProperty, idProprietario, nome, connection, this.driver.tipoDB);
		} catch (Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		} finally {
			this.driver.closeConnection(connection);
		}

	}

	protected ProtocolProperty getProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "getProtocolProperty";

		Connection con = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getProtocolProperty");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		try {
			return DBProtocolPropertiesUtils.getProtocolPropertyConfig(proprietarioProtocolProperty, idProprietario, nome, con, this.driver.tipoDB);
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			this.driver.closeConnection(con);
		}
	}

	protected ProtocolProperty getProtocolProperty(long idProtocolProperty) throws DriverConfigurazioneException {
		String nomeMetodo = "getProtocolProperty";

		Connection con = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getProtocolProperty");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		try {

			return DriverConfigurazioneDB_LIB.getProtocolProperty(idProtocolProperty, con, this.driver.tipoDB);

		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			this.driver.closeConnection(con);
		}
	}
	
	
	protected void setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, FiltroRicercaServiziApplicativi filtroRicerca, String tabella) throws SQLQueryObjectException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca.getProtocolProperties(), tabella);
		}
	}
	protected void _setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, List<FiltroRicercaProtocolPropertyConfig> list, String tabella) throws SQLQueryObjectException{
		if(list!=null && !list.isEmpty()){
			List<org.openspcoop2.core.mapping.FiltroRicercaProtocolProperty> l = new ArrayList<>();
			l.addAll(list);
			DBProtocolPropertiesUtils.setProtocolPropertiesForSearch(sqlQueryObject, l, tabella);
		}
	}
	
	
	protected void setProtocolPropertiesForSearch(PreparedStatement stmt, int index, FiltroRicercaServiziApplicativi filtroRicerca, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(stmt, index, filtroRicerca.getProtocolProperties(), proprietario);
		}
	}
	
	protected void _setProtocolPropertiesForSearch(PreparedStatement stmt, int index, 
			List<FiltroRicercaProtocolPropertyConfig> list, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(list!=null && !list.isEmpty()){
			List<org.openspcoop2.core.mapping.FiltroRicercaProtocolProperty> l = new ArrayList<>();
			l.addAll(list);
			DBProtocolPropertiesUtils.setProtocolPropertiesForSearch(stmt, index, l, proprietario, this.driver.tipoDB, this.driver.log);
		}
	}
}
