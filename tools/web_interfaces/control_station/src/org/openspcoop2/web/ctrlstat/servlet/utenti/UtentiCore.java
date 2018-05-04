/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
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

			return driver.getDriverUsersDB().getUsersByPermesso(permesso);

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
	
	public List<String> getUsersByProtocolloSupportato(String protocollo) throws DriverUsersDBException {
		return getUsersByProtocolloSupportato(protocollo, false);
	}
	
	public List<String> getUsersByProtocolloSupportato(String protocollo, boolean esclusiUtentiConSoloPermessoUtente) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "getUsersByProtocolloSupportato";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverUsersDB().getUsersByProtocolloSupportato(protocollo,esclusiUtentiConSoloPermessoUtente);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverUsersDBException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isForceEnableMultiTenant(User u, boolean checkProtocolliCaricatiManager) throws Exception {
		
		List<String> protocolli = null;
		if(u.getProtocolliSupportati()!=null && u.getProtocolliSupportati().size()>0) {
			protocolli = u.getProtocolliSupportati();
		}
		else {
			if(!checkProtocolliCaricatiManager) {
				protocolli = new ArrayList<>();
			}
			else {
				protocolli = this.getProtocolli();
			}
		}
		
		for (String protocollo : protocolli) {
			
			Search s = new Search();
			s.setPageSize(Liste.SOGGETTI, 2); // serve solo per il count
			s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, protocollo); // imposto protocollo
			s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, PddTipologia.OPERATIVO.toString()); // imposto dominio
			List<org.openspcoop2.core.registry.Soggetto> lista = null;
			if(this.isVisioneOggettiGlobale(u.getLogin())){
				lista = this.soggettiRegistroList(null, s);
			}else{
				lista = this.soggettiRegistroList(u.getLogin(), s);
			}
			if(lista!=null && lista.size()>1) {
				return true;
			}
			
		}
		
		return false;
	}
	
	public List<IDServizio> utentiServiziList(String login, ISearch ricerca) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "utentiServiziList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverUsersDB().utentiServiziList(login,ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverUsersDBException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<IDSoggetto> utentiSoggettiList(String login, ISearch ricerca) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "utentiSoggettiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverUsersDB().utentiSoggettiList(login,ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverUsersDBException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
}
