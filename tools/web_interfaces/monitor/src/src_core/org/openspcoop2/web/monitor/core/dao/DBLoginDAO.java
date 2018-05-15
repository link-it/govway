package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.crypt.Password;
import org.slf4j.Logger;

import org.openspcoop2.core.commons.dao.DAO;
import org.openspcoop2.core.commons.dao.DAOFactory;
import it.link.pdd.core.utenti.Ruolo;
import it.link.pdd.core.utenti.Utente;
import it.link.pdd.core.utenti.dao.IUtenteServiceSearch;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.dao.ISoggettoServiceSearch;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;

public class DBLoginDAO implements ILoginDAO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2911265067726812578L;

	private static Logger log = LoggerWrapperFactory.getLogger(DBLoginDAO.class);

	private IUtenteServiceSearch utenteDAO;

	private  it.link.pdd.core.utenti.dao.IServiceManager utentiServiceManager;

	private ISoggettoServiceSearch soggettoDAO;

	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;

	public DBLoginDAO() {
		try {	

			// init Service Manager utenti
			this.utentiServiceManager = (it.link.pdd.core.utenti.dao.IServiceManager) DAOFactory.getInstance(DBLoginDAO.log).getServiceManager(DAO.UTENTI);
			this.utenteDAO = this.utentiServiceManager.getUtenteServiceSearch();
						
			// init Service Manager utils
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory.getInstance(DBLoginDAO.log).getServiceManager(DAO.UTILS);
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
			Utente u = this.utenteDAO.find(this.utenteDAO.newExpression()
					.equals(Utente.model().LOGIN, username));
			Password passwordManager = new Password();

			return passwordManager.checkPw(pwd, u.getPassword());

		}catch (NotFoundException e) {
			DBLoginDAO.log.debug(e.getMessage(), e);
			return false;
		}  catch (ServiceException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
			throw e;
		}  catch (MultipleResultException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotImplementedException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			DBLoginDAO.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
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
	public Utente getUtente(UserDetailsBean user) {

		try {
			Utente u = this.utenteDAO.find(this.utenteDAO.newExpression()
					.equals(Utente.model().LOGIN, user.getUsername()));
			Password passwordManager = new Password();

			return passwordManager.checkPw(user.getPassword(), u.getPassword()) ? u
					: null;

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
	public UserDetailsBean loadUserByUsername(String username)
			throws NotFoundException, ServiceException, UserInvalidException {

		log.debug("cerco utente " + username);

		try {
			Utente u = this.utenteDAO.find(this.utenteDAO
					.newExpression().equals(Utente.model().LOGIN, username));

			// check consistenza
			if(u.getRuoloList()==null || u.getRuoloList().size()<=0){
				throw new UserInvalidException("Utente non dispone di alcun ruolo");
			}
			boolean amministratore = false;
			boolean operatore = false;
			for (Ruolo ruolo : u.getRuoloList()) {
				if(UserDetailsBean.RUOLO_OPERATORE.equals(ruolo.getRuolo())){
					operatore = true;
				}
				else if(UserDetailsBean.RUOLO_AMMINISTRATORE.equals(ruolo.getRuolo())){
					amministratore = true;
				}
			}
			if(operatore && !amministratore){
				if(u.getUtenteSoggettoList()==null || u.getUtenteSoggettoList().size()<=0){
					throw new UserInvalidException("Utente con ruolo 'operatore' senza alcun soggetto associato (Può darsi che i precedenti soggetti associati siano stati eliminati da pddConsole)");
				}
			}
			
			UserDetailsBean details = new UserDetailsBean();
			details.setUtente(u);

			return details;
			
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
			throw e; 
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
			throw e ; //new UsernameNotFoundException("Utente non trovato.");
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (UserInvalidException e) {
			log.error(e.getMessage(), e);
			throw  e; 
		}
	}
}
