package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.dao.ISoggettoServiceSearch;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.crypt.Password;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.slf4j.Logger;

public class DBLoginDAO implements ILoginDAO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2911265067726812578L;

	private static Logger log = LoggerWrapperFactory.getLogger(DBLoginDAO.class);

	private org.openspcoop2.web.lib.users.DriverUsersDB utenteDAO;

	private ISoggettoServiceSearch soggettoDAO;

	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;

	public DBLoginDAO() {
		try {	

			// init Service Manager utenti
			this.utenteDAO = (org.openspcoop2.web.lib.users.DriverUsersDB) DAOFactory.getInstance(DBLoginDAO.log).getServiceManager(org.openspcoop2.web.lib.users.ProjectInfo.getInstance());

			// init Service Manager utils
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory.getInstance(DBLoginDAO.log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			this.soggettoDAO = this.utilsServiceManager.getSoggettoServiceSearch();

		} catch (ServiceException e) {

			DBLoginDAO.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
		} catch (Exception e) {
			DBLoginDAO.log.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean login(String username, String pwd) throws ServiceException {
		try {
			boolean existsUser = this.utenteDAO.existsUser(username);

			if(!existsUser)
				throw new NotFoundException("Utente ["+username+"] non registrato");

			User u = this.utenteDAO.getUser(username);
			Password passwordManager = new Password();
			return passwordManager.checkPw(pwd, u.getPassword());
		}catch (NotFoundException e) {
			DBLoginDAO.log.debug(e.getMessage(), e);
			return false;
		} catch (DriverUsersDBException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public Soggetto getSoggetto(IdSoggetto idSog) {
		try {
			return this.soggettoDAO.find(this.soggettoDAO.newExpression()
					.equals(Soggetto.model().NOME_SOGGETTO, idSog.getNome())
					.and()
					.equals(Soggetto.model().TIPO_SOGGETTO, idSog.getTipo()));
		} catch (ServiceException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public void salvaUtente(User user) throws NotFoundException, ServiceException {
		try {
			boolean existsUser = this.utenteDAO.existsUser(user.getLogin());

			if(!existsUser)
				throw new NotFoundException("Utente ["+user.getLogin()+"] non registrato");
			
			this.utenteDAO.updateUser(user);
			
		} catch (DriverUsersDBException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public void salvaModalita(User user) throws NotFoundException, ServiceException {
		try {
			boolean existsUser = this.utenteDAO.existsUser(user.getLogin());

			if(!existsUser)
				throw new NotFoundException("Utente ["+user.getLogin()+"] non registrato");

			this.utenteDAO.saveProtocolloUtilizzatoPddMonitor(user.getLogin(), user.getProtocolloSelezionatoPddMonitor());
		} catch (DriverUsersDBException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public UserDetailsBean loadUserByUsername(String username)
			throws NotFoundException, ServiceException, UserInvalidException {

		log.debug("cerco utente " + username);

		try {
			boolean existsUser = this.utenteDAO.existsUser(username);

			if(!existsUser)
				throw new NotFoundException("Utente ["+username+"] non registrato");

			User u = this.utenteDAO.getUser(username);

			// check consistenza spostato dentro il metodo setutente
			//			int foundSoggetti = u.getSoggetti() != null ? u.getSoggetti().size() : 0;
			//			int foundServizi = u.getServizi() !=  null ? u.getServizi().size() : 0;
			//			
			//			boolean admin = (foundServizi + foundSoggetti) == 0;
			//			boolean operatore = (foundServizi + foundSoggetti) > 0;

			UserDetailsBean details = new UserDetailsBean();
			details.setUtente(u);

			return details;

		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
			throw e ; //new UsernameNotFoundException("Utente non trovato.");
		} catch (UserInvalidException e) {
			log.error(e.getMessage(), e);
			throw  e; 
		} catch (DriverUsersDBException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e); 
		}
	}
}
