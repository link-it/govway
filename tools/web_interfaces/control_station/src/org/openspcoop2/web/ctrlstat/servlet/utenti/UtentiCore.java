/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.sql.Connection;
import java.util.List;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * UtentiCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtentiCore extends ControlStationCore {

	public UtentiCore() throws Exception {
		super();
	}
	public UtentiCore(ControlStationCore core) throws Exception {
		super(core);
	}
	
	
	public boolean existsUser(String login) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "existsUser";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverUsersDB().existsUser(login);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverUsersDBException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<String> getUsersWithType(String permesso) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "getUsersWithType";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverUsersDB().getUsersWithType(permesso);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverUsersDBException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public User getUser(String login) throws DriverUsersDBException {
		return getUser(login,true);
	}
	public User getUser(String login,boolean logError) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "getUser";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverUsersDB().getUser(login);

		} catch (Exception e) {
			if(logError)
				ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverUsersDBException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public User getUser(long id) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "getUser";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverUsersDB().getUser(id);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverUsersDBException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<User> userList(ISearch ricerca) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "userList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverUsersDB().userList(ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverUsersDBException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
}
