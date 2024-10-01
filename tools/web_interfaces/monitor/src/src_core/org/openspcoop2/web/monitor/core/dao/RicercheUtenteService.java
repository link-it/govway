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
package org.openspcoop2.web.monitor.core.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.RicercaUtente;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.RicercaUtenteBean;
import org.openspcoop2.web.monitor.core.bean.RicercheUtenteSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.thread.ThreadExecutorManager;
import org.slf4j.Logger;


/**
 * RicercheUtenteService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RicercheUtenteService implements IRicercheUtenteService{

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();
	
	private org.openspcoop2.web.lib.users.DriverUsersDB utenteDAO;

	private RicercheUtenteSearchForm searchForm = null;

	private boolean timeoutEvent = false;
	private Integer timeoutRicerche = null;

	public RicercheUtenteService(){
		try {
			this.utenteDAO = (org.openspcoop2.web.lib.users.DriverUsersDB) DAOFactory.getInstance(log).getServiceManager(org.openspcoop2.web.lib.users.ProjectInfo.getInstance());
			this.timeoutRicerche = PddMonitorProperties.getInstance(RicercheUtenteService.log).getIntervalloTimeoutRicercaEventi();
			
		} catch (Exception e) {
			RicercheUtenteService.log.error(e.getMessage(), e);
		}
	}
	
	public RicercheUtenteService(Connection con, boolean autoCommit){
		this(con, autoCommit, null, RicercheUtenteService.log);
	}
	public RicercheUtenteService(Connection con, boolean autoCommit, Logger log){
		this(con, autoCommit, null, log);
	}
	public RicercheUtenteService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties){
		this(con, autoCommit, serviceManagerProperties, RicercheUtenteService.log);
	}
	public RicercheUtenteService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log){
		try {
			this.utenteDAO = (org.openspcoop2.web.lib.users.DriverUsersDB) DAOFactory.getInstance(log).getServiceManager(org.openspcoop2.web.lib.users.ProjectInfo.getInstance(), con, autoCommit, serviceManagerProperties, log);
			this.timeoutRicerche = PddMonitorProperties.getInstance(RicercheUtenteService.log).getIntervalloTimeoutRicercaEventi();
			
		} catch (Exception e) {
			RicercheUtenteService.log.error(e.getMessage(), e);
		}
	}

	@Override
	public void setSearch(RicercheUtenteSearchForm searchForm) {
		this.searchForm = searchForm;
	}
	
	@Override
	public RicercheUtenteSearchForm getSearch() {
		return this.searchForm;
	}
	
	private void logErrorMsg(String message, Exception e) {
		log.error(message,e);
	}

	@Override
	public List<RicercaUtenteBean> findAll(int start, int limit) {
		RicercheUtenteService.log.debug("Metodo FindAll: start[{}], limit: [{}]", start, limit);

		User loggedUtente = this.searchForm.getUser();
		String login = loggedUtente.getLogin();
		try {
			this.timeoutEvent = false;
			
			List<RicercaUtenteBean> list = null;
			if(this.timeoutRicerche == null) {
				list = this.leggiListaRicerche(login,start,limit);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.leggiListaRicerche(login,start,limit)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					RicercheUtenteService.log.error(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					RicercheUtenteService.log.error(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					RicercheUtenteService.log.error(e.getMessage(), e);
				}
			}
			return list;
		} catch (ServiceException | NotImplementedException e) {
			RicercheUtenteService.log.error(e.getMessage(), e);
		}

		return new ArrayList<>();
	}

	@Override
	public int totalCount() {
		RicercheUtenteService.log.debug("Metodo TotalCount");
		User loggedUtente = this.searchForm.getUser();
		String login = loggedUtente.getLogin();
		
		String modulo = null;
		String modalitaRicerca = null;
		String visibilita = null;
		String protocollo = null;
		String soggetto = null;
		// applico filtri sulle ricerche
		if(this.searchForm.getFiltroModulo() != null && !Costanti.NON_SELEZIONATO.equals(this.searchForm.getFiltroModulo())) {
			modulo = this.searchForm.getFiltroModulo();
		}
		
		if(this.searchForm.getFiltroModalitaRicerca() != null && !Costanti.NON_SELEZIONATO.equals(this.searchForm.getFiltroModalitaRicerca())) {
			modalitaRicerca = this.searchForm.getFiltroModalitaRicerca();
		}
		
		if(this.searchForm.getFiltroVisibilita() != null && !Costanti.NON_SELEZIONATO.equals(this.searchForm.getFiltroVisibilita())) {
			visibilita = this.searchForm.getFiltroVisibilita();
		}
		
		try {
			return this.utenteDAO.countRicerche(login, modulo, modalitaRicerca, visibilita, protocollo, soggetto);
		} catch (DriverUsersDBException e) {
			log.error(e.getMessage(), e);
		}
		
		return 0;
	}
	
	@Override
	public int totaleRicercheUtente(String login) {
		RicercheUtenteService.log.debug("Metodo totaleRicercheUtente [{}]", login);
		try {
			return this.utenteDAO.countRicerche(login, null, null, null, null, null);
		} catch (DriverUsersDBException e) {
			log.error(e.getMessage(), e);
		}
		
		return 0;
	}

	@Override
	public void store(RicercaUtenteBean ricercaPersonalizzata) throws Exception {
		this.insertRicerca(this.searchForm.getUser().getLogin(), ricercaPersonalizzata);	
	}
	
	@Override
	public void insertRicerca(String login, RicercaUtente ricercaPersonalizzata) throws DriverUsersDBException {
		try{
			Long idRicerca = this.utenteDAO.insertRicerca(login, ricercaPersonalizzata);
			ricercaPersonalizzata.setId(idRicerca);
		} catch (DriverUsersDBException e) {
			logErrorMsg(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public void updateRicerca(String login, RicercaUtente ricercaPersonalizzata) throws DriverUsersDBException {
		try{
			this.utenteDAO.updateRicerca(login, ricercaPersonalizzata);
		} catch (DriverUsersDBException e) {
			logErrorMsg(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void deleteById(Long key) {
		User loggedUtente = this.searchForm.getUser();
		String login = loggedUtente.getLogin();
		try {
			this.utenteDAO.cancellaRicerca(login, key);
		} catch (DriverUsersDBException e) {
			log.error("Errore durante la cancellazione della ricerca personalizzata "+key+" dell'utente "+login+": " +e.getMessage(),e);
		}
	}

	@Override
	public void delete(RicercaUtenteBean obj)  throws Exception {
		this.deleteById(obj.getId());
	}

	@Override
	public void deleteAll() throws Exception {
		User loggedUtente = this.searchForm.getUser();
		String login = loggedUtente.getLogin();
		try {
			this.utenteDAO.cancellaRicerche(login);
		} catch (DriverUsersDBException e) {
			log.error("Errore durante la cancellazione delle ricerche personalizzate dell'utente "+login+": " +e.getMessage(),e);
		}
	}

	@Override
	public RicercaUtenteBean findById(Long key) {
		RicercaUtenteBean bean = null;
		User loggedUtente = this.searchForm.getUser();
		String login = loggedUtente.getLogin();
		try {
			return new RicercaUtenteBean(this.utenteDAO.leggiRicerca(login, key));
		}catch (DriverUsersDBException e) {
			log.error("Errore durante la lettura della ricerca personalizzata dell'utente "+login+" con id "+key+": " +e.getMessage(),e);
		}
		return bean;
	}
	
	@Override
	public RicercaUtenteBean leggiRicercaPersonalizzata(long idUtente, long idRicerca) {
		RicercaUtenteBean bean = null;
		try {
			return new RicercaUtenteBean(this.utenteDAO.leggiRicerca(idUtente, idRicerca));
		}catch (DriverUsersDBException e) {
			log.error("Errore durante la lettura della ricerca personalizzata dell'utente "+idUtente+" con id "+idRicerca+": " +e.getMessage(),e);
		}
		return bean;
	}

	@Override
	public List<RicercaUtenteBean> findAll() {
		RicercheUtenteService.log.debug("Metodo FindAll");

		User loggedUtente = this.searchForm.getUser();
		String login = loggedUtente.getLogin();
		try {
			this.timeoutEvent = false;
			
			List<RicercaUtenteBean> list = null;
			if(this.timeoutRicerche == null) {
				list = this.leggiListaRicerche(login,null,null);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.leggiListaRicerche(login,null,null)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					RicercheUtenteService.log.error(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					RicercheUtenteService.log.error(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					RicercheUtenteService.log.error(e.getMessage(), e);
				}
			}
			return list;
		} catch (ServiceException | NotImplementedException e) {
			RicercheUtenteService.log.error(e.getMessage(), e);
		}

		return new ArrayList<>();
	}
	
	@Override
	public List<RicercaUtenteBean> listaRicercheDisponibiliPerUtente(String login, String modulo, String modalitaRicerca, String protocollo, String soggetto) {
		RicercheUtenteService.log.debug("Metodo listaRicercheDisponibiliPerUtente login[{}], modulo[{}], modalitaricerca [{}], protocollo [{}], soggetto [{}]", login, modulo, modalitaRicerca, protocollo, soggetto);

		try {
			this.timeoutEvent = false;
			
			List<RicercaUtenteBean> list = null;
			if(this.timeoutRicerche == null) {
				list = this.listaRicercheDisponibiliPerUtenteEngine(login, modulo, modalitaRicerca, protocollo, soggetto);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.listaRicercheDisponibiliPerUtenteEngine(login, modulo, modalitaRicerca, protocollo, soggetto)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					RicercheUtenteService.log.error(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					RicercheUtenteService.log.error(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					RicercheUtenteService.log.error(e.getMessage(), e);
				}
			}
			return list;
		} catch (ServiceException | NotImplementedException e) {
			RicercheUtenteService.log.error(e.getMessage(), e);
		}

		return new ArrayList<>();
	}
	
	private List<RicercaUtenteBean> leggiListaRicerche(String login, Integer start, Integer limit){
		String modulo = null;
		String modalitaRicerca = null;
		String visibilita = null;
		String protocollo = null;
		String soggetto = null;
		
		// applico filtri sulle ricerche
		if(this.searchForm.getFiltroModulo() != null && !Costanti.NON_SELEZIONATO.equals(this.searchForm.getFiltroModulo())) {
			modulo = this.searchForm.getFiltroModulo();
		}
		
		if(this.searchForm.getFiltroModalitaRicerca() != null && !Costanti.NON_SELEZIONATO.equals(this.searchForm.getFiltroModalitaRicerca())) {
			modalitaRicerca = this.searchForm.getFiltroModalitaRicerca();
		}
		
		if(this.searchForm.getFiltroVisibilita() != null && !Costanti.NON_SELEZIONATO.equals(this.searchForm.getFiltroVisibilita())) {
			visibilita = this.searchForm.getFiltroVisibilita();
		}
		
		return leggiListaRicercheEngine(login, start, limit, modulo, modalitaRicerca, visibilita, soggetto, protocollo);
	}

	private List<RicercaUtenteBean> leggiListaRicercheEngine(String login, Integer start, Integer limit, 
			String modulo, String modalitaRicerca, String visibilita, String protocollo, String soggetto) {
		List<RicercaUtenteBean> list = new ArrayList<>();
		
		try {
			List<RicercaUtente> listaRicerche = this.utenteDAO.listaRicerche(login, start, limit, modulo, modalitaRicerca, visibilita, protocollo, soggetto);
			
			for (RicercaUtente ricercaUtente : listaRicerche) {
				list.add(new RicercaUtenteBean(ricercaUtente));
			}
		} catch (DriverUsersDBException e) {
			RicercheUtenteService.log.error(e.getMessage(), e);
		}

		return list;
	}
	
	private List<RicercaUtenteBean> listaRicercheDisponibiliPerUtenteEngine(String login, String modulo, String modalitaRicerca, String protocollo, String soggetto) {
		List<RicercaUtenteBean> list = new ArrayList<>();
		
		try {
			List<RicercaUtente> listaRicerche =  this.utenteDAO.listaRicercheDisponibiliPerUtente(login, modulo, modalitaRicerca, protocollo, soggetto);
			
			for (RicercaUtente ricercaUtente : listaRicerche) {
				list.add(new RicercaUtenteBean(ricercaUtente));
			}
		} catch (DriverUsersDBException e) {
			RicercheUtenteService.log.error(e.getMessage(), e);
		}

		return list;
	}

	@Override
	public boolean isTimeoutEvent() {
		return this.timeoutEvent;
	}
}
