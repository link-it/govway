/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.web.monitor.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/**
 * UserService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class UserService implements IUserService {

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();

	private org.openspcoop2.web.lib.users.DriverUsersDB utenteDAO;


	public UserService() {

		try {

			// init Service Manager utenti
			this.utenteDAO = (org.openspcoop2.web.lib.users.DriverUsersDB) DAOFactory.getInstance(UserService.log).getServiceManager(org.openspcoop2.web.lib.users.ProjectInfo.getInstance());

		} catch (Exception e) {
			UserService.log.error(e.getMessage(), e);
		}
	}

	@Override
	public List<User> findAll(int start, int limit) {
		return null;
	}

	@Override
	public int totalCount() {
		return 0;
	}

	@Override
	public void delete(User obj) throws Exception {
		throw new NotImplementedException("Operazione non disponibile");
	}

	@Override
	public void deleteById(String key) {}

	@Override
	public List<User> findAll() {
		return null;
	}

	@Override
	public User findById(String key) {

		try {
			return this.utenteDAO.getUser(key);
		} catch (DriverUsersDBException e) {
			UserService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public void store(User obj) throws Exception {
		throw new NotImplementedException("Operazione non disponibile");
	}

	@Override
	public void deleteAll() throws Exception {
		throw new NotImplementedException("Operazione non disponibile");
	}

	@Override
	public List<String> getTipiNomiSoggettiAssociati(User utente) {
		List<String> lst = null;
		if (utente != null) {
			lst = new ArrayList<String>();
			for (IDSoggetto idsog : utente.getSoggetti()) {
				if (idsog.getTipo() != null && idsog.getNome() != null) {
					lst.add(idsog.getTipo() + "/" + idsog.getNome());
				}
			}
		}

		return lst;

	}

	@Override
	public Stato getTableState(String nomeTabella,User utente) {
		Stato state = null;
		UserService.log.debug("Get Table State [utente: " + utente.getLogin() + "]");
		try {
			state = this.utenteDAO.getStato(utente.getLogin(), nomeTabella.toString());
			if(state == null) {
				state = new Stato();
				state.setOggetto(nomeTabella.toString());
				state.setStato(null);
			}
			return state;			

		} catch (DriverUsersDBException e) {
			UserService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public void saveTableState(String nomeTabella,User user, Stato stato) {
		try {
			this.utenteDAO.saveStato(user.getLogin(), stato.getOggetto(), stato.getStato());
		} catch (DriverUsersDBException e) {
			UserService.log.error(e.getMessage(), e);
		}
	}
	
	
}
