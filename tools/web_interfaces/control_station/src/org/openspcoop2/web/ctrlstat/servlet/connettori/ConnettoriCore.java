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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
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
	public ConnettoriCore(ControlStationCore core) throws Exception {
		super(core);
	}

	protected static ArrayList<Property> fromPropertiesToCollection(Properties props) {
		ArrayList<Property> lista = new ArrayList<Property>();

		Property tmp = null;
		Enumeration<?> en = props.keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String value = (String) props.get(key);
			// log.info("loading property "+key+" - "+value);
			tmp = new Property();
			tmp.setNome(key);
			tmp.setValore(value);

			lista.add(tmp);
		}

		return lista;
	}

	public List<String> connettoriList() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public void CRUDConnettore(int type, org.openspcoop2.core.config.Connettore connettore) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "CRUDConnettore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			if (type == 1)
				driver.getDriverConfigurazioneDB().createConnettore(connettore);
			if (type == 2)
				driver.getDriverConfigurazioneDB().updateConnettore(connettore);
			if (type == 3)
				driver.getDriverConfigurazioneDB().deleteConnettore(connettore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public void CRUDConnettore(int type, Connettore connettore) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "CRUDConnettore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			if (type == 1)
				driver.getDriverRegistroServiziDB().createConnettore(connettore);
			if (type == 2)
				driver.getDriverRegistroServiziDB().updateConnettore(connettore);
			if (type == 3)
				driver.getDriverRegistroServiziDB().deleteConnettore(connettore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
}
