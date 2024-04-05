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
package org.openspcoop2.web.ctrlstat.servlet.remote_stores;

import java.sql.Connection;
import java.util.List;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.pdd.core.keystore.KeystoreException;
import org.openspcoop2.pdd.core.keystore.KeystoreNotFoundException;
import org.openspcoop2.pdd.core.keystore.RemoteStore;
import org.openspcoop2.pdd.core.keystore.RemoteStoreKeyEntry;
import org.openspcoop2.pdd.core.keystore.RemoteStoreProviderDriverUtils;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;

/**
 * RemoteStoresCore
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoresCore extends ControlStationCore {

	public RemoteStoresCore() throws DriverControlStationException {
		super();
	}
	public RemoteStoresCore(ControlStationCore core) throws DriverControlStationException {
		super(core);
	}
	public List<RemoteStoreKeyEntry> remoteStoreKeysList(ConsoleSearch ricerca, long idRemoteStore) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "remoteStoreKeysList";
		DriverConfigurazioneDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverConfigurazioneDB(con, null, this.tipoDB);
			return RemoteStoreProviderDriverUtils.getRemoteStoreKeyEntries(log, driver, ricerca, idRemoteStore);
		} catch (DriverConfigurazioneException | KeystoreException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<RemoteStore> remoteStoresList() throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "remoteStoresList";
		DriverConfigurazioneDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverConfigurazioneDB(con, null, this.tipoDB);
			return RemoteStoreProviderDriverUtils.getRemoteStores(driver);
		} catch (DriverConfigurazioneException | KeystoreException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public RemoteStoreKeyEntry getRemoteStoreKeyEntry(long idRemoteStoreKey) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "getRemoteStoreKeyEntry";
		DriverConfigurazioneDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverConfigurazioneDB(con, null, this.tipoDB);
			return RemoteStoreProviderDriverUtils.getRemoteStoreKeyEntry(log,driver, idRemoteStoreKey);
		} catch (DriverConfigurazioneException | KeystoreException | KeystoreNotFoundException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
}
