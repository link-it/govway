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



package org.openspcoop2.core.registry.driver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.mapping.DBProtocolPropertiesUtils;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaAzioni;
import org.openspcoop2.core.registry.driver.FiltroRicercaFruizioniServizio;
import org.openspcoop2.core.registry.driver.FiltroRicercaOperations;
import org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes;
import org.openspcoop2.core.registry.driver.FiltroRicercaProtocolPropertyRegistry;
import org.openspcoop2.core.registry.driver.FiltroRicercaResources;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * DriverRegistroServiziDB_protocolPropertiesDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_protocolPropertiesDriver {

	private DriverRegistroServiziDB driver = null;
	
	public DriverRegistroServiziDB_protocolPropertiesDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected boolean existsProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome) throws DriverRegistroServiziException {

		Connection connection;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsProtocolProperty");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsProtocolProperty] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			return DBProtocolPropertiesUtils.existsProtocolProperty(proprietarioProtocolProperty, idProprietario, nome, connection, this.driver.tipoDB);
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			if (this.driver.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

	}

	protected ProtocolProperty getProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome) throws DriverRegistroServiziException {
		String nomeMetodo = "getProtocolProperty";

		Connection con = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getProtocolProperty");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		try {
			return DBProtocolPropertiesUtils.getProtocolPropertyRegistry(proprietarioProtocolProperty, idProprietario, nome, con, this.driver.tipoDB);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	protected ProtocolProperty getProtocolProperty(long idProtocolProperty) throws DriverRegistroServiziException {
		String nomeMetodo = "getProtocolProperty";

		Connection con = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getProtocolProperty");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		try {

			return DriverRegistroServiziDB_LIB.getProtocolProperty(idProtocolProperty, con, this.driver.tipoDB);

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	protected void setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, FiltroRicercaFruizioniServizio filtroRicerca, String tabella) throws SQLQueryObjectException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca.getProtocolPropertiesFruizione(), tabella);
		}
	}
	protected void setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, FiltroRicercaAzioni filtroRicerca, String tabella) throws SQLQueryObjectException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca.getProtocolPropertiesAzione(), tabella);
		}
	}
	protected void setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, FiltroRicercaOperations filtroRicerca, String tabella) throws SQLQueryObjectException{
		if(filtroRicerca!=null){
			if(CostantiDB.PORT_TYPE.equals(tabella)){
				this._setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca.getProtocolPropertiesPortType(), tabella);
			}
			else{
				this._setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca.getProtocolPropertiesAzione(), tabella);
			}
		}
	}
	protected void setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, FiltroRicercaPortTypes filtroRicerca, String tabella) throws SQLQueryObjectException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca.getProtocolPropertiesPortType(), tabella);
		}
	}
	protected void setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, FiltroRicercaResources filtroRicerca, String tabella) throws SQLQueryObjectException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca.getProtocolPropertiesResources(), tabella);
		}
	}
	protected void setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, FiltroRicercaAccordi filtroRicerca, String tabella) throws SQLQueryObjectException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca.getProtocolPropertiesAccordo(), tabella);
		}
	}
	protected void setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, FiltroRicerca filtroRicerca, String tabella) throws SQLQueryObjectException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca.getProtocolProperties(), tabella);
		}
	}
	private void _setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, List<FiltroRicercaProtocolPropertyRegistry> list, String tabella) throws SQLQueryObjectException{
		if(list!=null && list.size()>0){
			List<org.openspcoop2.core.mapping.FiltroRicercaProtocolProperty> l = new ArrayList<>();
			l.addAll(list);
			DBProtocolPropertiesUtils.setProtocolPropertiesForSearch(sqlQueryObject, l, tabella);
		}
	}
	
	protected void setProtocolPropertiesForSearch(PreparedStatement stmt, int index, FiltroRicercaFruizioniServizio filtroRicerca, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(stmt, index, filtroRicerca.getProtocolPropertiesFruizione(), proprietario);
		}
	}
	protected void setProtocolPropertiesForSearch(PreparedStatement stmt, int index, FiltroRicercaAzioni filtroRicerca, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(stmt, index, filtroRicerca.getProtocolPropertiesAzione(), proprietario);
		}
	}
	protected void setProtocolPropertiesForSearch(PreparedStatement stmt, int index, FiltroRicercaOperations filtroRicerca, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(filtroRicerca!=null){
			if(ProprietariProtocolProperty.PORT_TYPE.equals(proprietario)){
				this._setProtocolPropertiesForSearch(stmt, index, filtroRicerca.getProtocolPropertiesPortType(), proprietario);
			}
			else{
				this._setProtocolPropertiesForSearch(stmt, index, filtroRicerca.getProtocolPropertiesAzione(), proprietario);
			}
		}
	}
	protected void setProtocolPropertiesForSearch(PreparedStatement stmt, int index, FiltroRicercaPortTypes filtroRicerca, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(stmt, index, filtroRicerca.getProtocolPropertiesPortType(), proprietario);
		}
	}
	protected void setProtocolPropertiesForSearch(PreparedStatement stmt, int index, FiltroRicercaResources filtroRicerca, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(stmt, index, filtroRicerca.getProtocolPropertiesResources(), proprietario);
		}
	}
	protected void setProtocolPropertiesForSearch(PreparedStatement stmt, int index, FiltroRicercaAccordi filtroRicerca, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(stmt, index, filtroRicerca.getProtocolPropertiesAccordo(), proprietario);
		}
	}
	protected void setProtocolPropertiesForSearch(PreparedStatement stmt, int index, FiltroRicerca filtroRicerca, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(stmt, index, filtroRicerca.getProtocolProperties(), proprietario);
		}
	}
	
	private void _setProtocolPropertiesForSearch(PreparedStatement stmt, int index, 
			List<FiltroRicercaProtocolPropertyRegistry> list, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(list!=null && list.size()>0){
			List<org.openspcoop2.core.mapping.FiltroRicercaProtocolProperty> l = new ArrayList<>();
			l.addAll(list);
			DBProtocolPropertiesUtils.setProtocolPropertiesForSearch(stmt, index, l, proprietario, this.driver.tipoDB, this.driver.log);
		}
	}
}
