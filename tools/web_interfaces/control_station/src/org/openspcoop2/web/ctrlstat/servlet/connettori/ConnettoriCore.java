/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;

/**
 * ConnettoriCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoriCore extends ControlStationCore {

	public ConnettoriCore() throws Exception {
		super();
	}
	public ConnettoriCore(boolean initForApi, String confDir, String protocolloDefault) throws Exception {
		super(initForApi, confDir, protocolloDefault);
	}
	public ConnettoriCore(ControlStationCore core) throws Exception {
		super(core);
	}

	protected static ArrayList<Property> fromPropertiesToCollection(Properties props) {
		ArrayList<Property> lista = new ArrayList<>();

		Property tmp = null;
		Enumeration<?> en = props.keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String value = (String) props.get(key);
			tmp = new Property();
			tmp.setNome(key);
			tmp.setValore(value);

			lista.add(tmp);
		}

		return lista;
	}

	public List<String> connettoriList() throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "connettoriList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().connettoriList();

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Property[] getPropertiesConnettore(String nomeConnettore) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getPropertiesConnettore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getPropertiesConnettore(nomeConnettore);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public org.openspcoop2.core.config.Property[] getPropertiesConnettoreConfig(String nomeConnettore) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getPropertiesConnettore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPropertiesConnettore(nomeConnettore);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Connettore getConnettoreRegistro(long idConnettore) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getConnettoreRegistro";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getConnettore(idConnettore);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public org.openspcoop2.core.config.Connettore getConnettoreConfig(long idConnettore) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getConnettoreRegistro";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getConnettore(idConnettore);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
}
