package org.openspcoop2.web.monitor.core.dao;

import it.link.pdd.core.DAO;
import org.openspcoop2.core.commons.dao.DAOFactory;
import it.link.pdd.core.utenti.IdSoggetto;
import it.link.pdd.core.utenti.IdUtente;
import it.link.pdd.core.utenti.Ruolo;
import it.link.pdd.core.utenti.StatoTabella;
import it.link.pdd.core.utenti.Utente;
import it.link.pdd.core.utenti.UtenteSoggetto;
import it.link.pdd.core.utenti.dao.IUtenteService;
import it.link.pdd.core.utenti.dao.IUtenteServiceSearch;
import it.link.pdd.core.utenti.dao.jdbc.JDBCUtenteServiceSearch;
import org.openspcoop2.web.monitor.core.bean.UtentiSearchForm;
import org.openspcoop2.web.monitor.core.costants.RuoliUtente;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.AliasField;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;

public class UserService implements IUserService {

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();

	private IUtenteServiceSearch utenteSearchDAO;

	private IUtenteService utenteDAO;

	private it.link.pdd.core.utenti.dao.IServiceManager utentiServiceManager;
	
	private UtentiSearchForm search = null;

	public UserService() {

		try {

			// init Service Manager utenti
			this.utentiServiceManager = (it.link.pdd.core.utenti.dao.IServiceManager) DAOFactory.getInstance(UserService.log).getServiceManager(DAO.UTENTI,UserService.log);
			this.utenteSearchDAO = this.utentiServiceManager.getUtenteServiceSearch();
			this.utenteDAO = this.utentiServiceManager.getUtenteService();
			
		} catch (Exception e) {
			UserService.log.error(e.getMessage(), e);
		}
	}

	@Override
	public void setSearch(UtentiSearchForm search) {
		this.search = search;
	}
	
	@Override
	public UtentiSearchForm getSearch() {
		return this.search;
	}
	
	@Override
	public List<Utente> findAll(int start, int limit) {

		try {

			IExpression e = this.getExpressionFromFilter(this.search);
			IPaginatedExpression pagExpression = this.utenteSearchDAO.toPaginatedExpression(e);
			pagExpression.sortOrder(SortOrder.ASC).addOrder(Utente.model().LOGIN);
			pagExpression.offset(start).limit(limit);
			
			return this.utenteSearchDAO.findAll(pagExpression);
		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			UserService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public int totalCount() {
		NonNegativeNumber res = null;
		try {
			IExpression e = this.getExpressionFromFilter(this.search);
			 
			res = this.utenteSearchDAO.count(e);

			if (res != null) {
				return new Long(res.longValue()).intValue();
			}
		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			UserService.log.error(e.getMessage(), e);
		}

		return 0;

		// return ((Long)  createQuery("select count(u) from User u")
		// .getSingleResult()).intValue();
	}

	@Override
	public void delete(Utente obj) throws Exception {
		this.utenteDAO.delete(obj);
	}

	@Override
	public void deleteById(String key) {

		IdUtente idutente = new IdUtente();
		idutente.setLogin(key);

		try {
			this.utenteDAO.deleteById(idutente);
		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		}

		// User u =  find(User.class, key);
		// this.em.remove(u);
	}

	@Override
	public List<Utente> findAll() {

		try {
			IExpression e = this.getExpressionFromFilter(this.search);
			IPaginatedExpression pagExpression = this.utenteSearchDAO.toPaginatedExpression(e);
			pagExpression.sortOrder(SortOrder.ASC).addOrder(Utente.model().LOGIN);
			
			return this.utenteSearchDAO.findAll(pagExpression); 
		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		}catch (ExpressionNotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			UserService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public Utente findById(String key) {

		try {
			return this.utenteSearchDAO.find(this.utenteSearchDAO
					.newExpression().equals(Utente.model().LOGIN, key));
		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			UserService.log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			UserService.log.error(e.getMessage(), e);
		}

		return null;
		//  find(User.class, key);
	}

	@Override
	public void store(Utente obj) throws Exception {

		IdUtente idUt = new IdUtente();
		idUt.setLogin(obj.getLogin());

		if (this.utenteDAO.exists(idUt)) {
	//		obj.setRuoloList(new ArrayList<Ruolo>());
			this.utenteDAO.update(idUt, obj);
		} else {
			// temp
			if (obj.getRuoloList() == null)
				obj.setRuoloList(new ArrayList<Ruolo>());
			if (obj.getStatoTabellaList() == null)
				obj.setStatoTabellaList(new ArrayList<StatoTabella>());
			if (obj.getUtenteSoggettoList() == null)
				obj.setUtenteSoggettoList(new ArrayList<UtenteSoggetto>());
			this.utenteDAO.create(obj);
		}
		// if (obj.getId() != null) {
		// createQuery("delete from Authority a where a.id.user=:user")
		// .setParameter("user", obj).executeUpdate();
		//
		// User u = this.em.merge(obj);
		//
		// this.em.persist(u);
		// } else {
		// this.em.persist(obj);
		// }
	}

	@Override
	public void deleteAll() throws Exception {

		List<Utente> toRemove = this.findAll();

		for (Utente u : toRemove) {
			this.utenteDAO.delete(u);
		}

		// try {
		// List<User> l = this.findAll();
		// for (User user : l) {
		//  remove(user);
		// }
		// } catch (Exception e) {
		// UserService.log.error(e.getMessage(), e);
		// }
	}

	@Override
	public List<String> getTipiNomiSoggettiAssociati(Utente utente) {
		List<String> lst = null;
		try {
			if (utente != null && utente.getLogin() != null) {
				Utente u = this.utenteSearchDAO.find(this.utenteSearchDAO
						.newExpression().equals(Utente.model().LOGIN,
								utente.getLogin()));
				lst = new ArrayList<String>();
				for (UtenteSoggetto us : u.getUtenteSoggettoList()) {
					IdSoggetto idsog = us.getSoggetto();
					if (idsog.getTipo() != null && idsog.getNome() != null) {
						lst.add(idsog.getTipo() + "/" + idsog.getNome());
					}
				}
			}
		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			UserService.log.error(e.getMessage(), e);
		}

		return lst;

	}

	@Override
	public List<Ruolo> getRoles(Utente user) {
		Utente u;
		List<Ruolo> auths = null;
		try {
			if (user != null && user.getLogin() != null) {
				u = this.utenteSearchDAO.find(this.utenteDAO.newExpression()
						.equals(Utente.model().LOGIN, user.getLogin()));
				auths = u.getRuoloList();
			}
			return auths;
		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			UserService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public Utente find(Utente user) {
		Utente u;
		try {
			u = this.utenteDAO.find(this.utenteDAO.newExpression().equals(
					Utente.model().LOGIN, user.getLogin()));
			return u;
		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			UserService.log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			UserService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public List<String> nomeUtenteAutoComplete(String input) {
		List<String> list = new ArrayList<String>();


		IExpression expr;
		try {
			// utilizzo la stessa condizione dei ricerca che mi restituisce solo gli allarmi consentiti per utente
			expr  = this.getExpressionFromFilter(this.search);

			if(!StringUtils.isEmpty(input)){
				int idx= input.indexOf("/");
				if(idx != -1){
					input = input.substring(idx + 1, input.length());
				}

				expr.ilike(Utente.model().LOGIN, input.toLowerCase() , LikeMode.ANYWHERE);
			}

			// Ordinamento gia' presente nella query standard
			expr.sortOrder(SortOrder.ASC).addOrder(Utente.model().LOGIN);

			IPaginatedExpression pagExpr = this.utenteSearchDAO.toPaginatedExpression(expr);
			pagExpr.offset(0).limit(DynamicUtilsService.LIMIT_SEARCH);

			List<Object> select = this.utenteSearchDAO.select(pagExpr, Utente.model().LOGIN);

			if(select != null && select.size() > 0)
				for (Object object : select) {
					list.add((String) object);
				}

		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			UserService.log.debug(e.getMessage());
		} catch (ExpressionNotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			UserService.log.error(e.getMessage(), e);
		}

		return list;
	}

	private IExpression getExpressionFromFilter(UtentiSearchForm searchForm) 
			throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		IExpression expr = this.utenteSearchDAO.newExpression();
		boolean addAnd = false;

		if(searchForm == null)
			return expr;
		
		if(StringUtils.isNotEmpty(searchForm.getNome())){
			expr.ilike(Utente.model().LOGIN, searchForm.getNome(), LikeMode.ANYWHERE);
			addAnd  = true;
		}
		
		String ruolo = searchForm.getRuolo();
		if(ruolo!= null && !ruolo.equals("--") && !ruolo.equals("*")){
			RuoliUtente ruoloU = RuoliUtente.getFromString(ruolo.toLowerCase());
			
			if(addAnd) expr.and();
			expr.equals(Utente.model().RUOLO.RUOLO, ruoloU.name());
			addAnd  = true;
		}
		
		return expr;
	}
	
	@Override
	public StatoTabella getTableState(String nomeTabella,Utente utente) {
		StatoTabella state = null;

		UserService.log.debug("Get Table State [utente: "
				+ utente.getLogin() + "]");
		try {
			
			IPaginatedExpression pagExpr = this.utenteSearchDAO.newPaginatedExpression();
			pagExpr.equals(Utente.model().LOGIN, utente.getLogin());
			pagExpr.and();
			pagExpr.equals(Utente.model().STATO_TABELLA.TABELLA,nomeTabella);
			
			String aliasStato = "statoTabellaUtente";
			AliasField af = new AliasField(Utente.model().STATO_TABELLA.STATO, aliasStato);
			CustomField cf = new CustomField("id", Long.class, "id", ((JDBCUtenteServiceSearch)this.utenteSearchDAO).getFieldConverter().toTable(Utente.model().STATO_TABELLA));
			
			List<Map<String, Object>> list = this.utenteSearchDAO.select(pagExpr, af , cf);
			if(list.size()>0) {
				Map<String, Object> map = list.get(0);
				
				Object oId = map.get("id");
				
				Object o = map.get(aliasStato);
				if(o instanceof String) {
					state = new StatoTabella();
					state.setTabella(nomeTabella);
					state.setStato((String) o);
					if(oId!=null && (oId instanceof Long)) {
						state.setId((Long)oId);
					}
				}
			}
			
//			Utente u = this.utenteSearchDAO.find(this.utenteSearchDAO
//					.newExpression()
//					.equals(Utente.model().LOGIN, utente.getLogin())
//					.and()
//					.equals(Utente.model().STATO_TABELLA.TABELLA,nomeTabella));
////							NomiTabelle.TRANSAZIONI.toString()));
//
//			for (StatoTabella stato : u.getStatoTabellaList()) {
//				if(nomeTabella.equals(stato.getTabella())){
//					state = stato;
//					break;
//				}
//			}

			return state;
		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			state = new StatoTabella();
			state.setTabella(nomeTabella);
			state.setStato(null);

			return state;
//		} catch (MultipleResultException e) {
//			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			UserService.log.error(e.getMessage(), e);
		}

		return null;
		// try{
		// state =
		// (TableState)this.em.createQuery("select s from TableState s where s.utente.id=:idUtente and s.tabella=:tabella")
		// .setParameter("idUtente", utente.getId())
		// .setParameter("tabella", NomiTabelle.TRANSAZIONI)
		// .getSingleResult();
		// }catch(NoResultException nr){
		// //inizializzo lo stato
		// state = new TableState();
		// state.setUtente(utente);
		// state.setTabella(NomiTabelle.TRANSAZIONI);
		// state.setStato(null);
		// }catch(Exception e){
		// log.error(e,e);
		// }
		// return state;
	}

	@Override
	public void saveTableState(String nomeTabella,Utente user, StatoTabella stato) {

		for (int i = 0; i < user.sizeStatoTabellaList(); i++) {
			StatoTabella statoOld = user.getStatoTabella(i);
			if(nomeTabella.equals(statoOld.getTabella())){
				user.removeStatoTabella(i);
				break;
			}
		}
		
		user.addStatoTabella(stato);

		IdUtente idUtente = new IdUtente();
		idUtente.setLogin(user.getLogin());

		try {
			this.utenteDAO.update(idUtente, user);
		} catch (ServiceException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			UserService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			UserService.log.error(e.getMessage(), e);
		}

	}
}
