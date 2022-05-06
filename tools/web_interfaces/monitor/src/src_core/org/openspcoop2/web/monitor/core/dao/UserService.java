/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.lib.users.dao.UserPassword;
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
	
	@Override
	public void savePassword(Long idUser, String login, String newPassword, Date dataAggiornamentoPassword) throws Exception {
		try {
			this.utenteDAO.savePassword(idUser, login, newPassword, dataAggiornamentoPassword);
		} catch (DriverUsersDBException e) {
			UserService.log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public void savePasswordStorico(Long idUser, String login, String newPassword, Date dataAggiornamentoPassword, List<UserPassword> storicoPassword) throws Exception {
		try {
			this.utenteDAO.savePassword(idUser, login, newPassword, dataAggiornamentoPassword, storicoPassword);
		} catch (DriverUsersDBException e) {
			UserService.log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public void salvaModalita(String login, String modalita) throws NotFoundException, ServiceException {
		try {
			boolean existsUser = this.utenteDAO.existsUser(login);

			if(!existsUser)
				throw new NotFoundException("Utente ["+login+"] non registrato");

			this.utenteDAO.saveProtocolloUtilizzatoPddMonitor(login, modalita);
		} catch (DriverUsersDBException e) {
			UserService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public void salvaSoggettoPddMonitor(String login, String soggetto) throws NotFoundException, ServiceException {
		try {
			boolean existsUser = this.utenteDAO.existsUser(login);

			if(!existsUser)
				throw new NotFoundException("Utente ["+login+"] non registrato");

			this.utenteDAO.saveSoggettoUtilizzatoPddMonitor(login, soggetto);
		} catch (DriverUsersDBException e) {
			UserService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
}
