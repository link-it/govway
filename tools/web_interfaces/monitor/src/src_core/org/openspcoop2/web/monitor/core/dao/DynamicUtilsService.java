package org.openspcoop2.web.monitor.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.Fruitore;
import org.openspcoop2.core.commons.search.IdPortaDominio;
import org.openspcoop2.core.commons.search.IdServizioApplicativo;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.commons.search.PortaDominio;
import org.openspcoop2.core.commons.search.ServizioApplicativo;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IFruitoreServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortTypeServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaApplicativaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDelegataServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDominioServiceSearch;
import org.openspcoop2.core.commons.search.dao.IServizioApplicativoServiceSearch;
import org.openspcoop2.core.commons.search.dao.ISoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCSoggettoServiceSearch;
import org.openspcoop2.core.commons.search.utils.ProjectInfo;
import org.openspcoop2.core.commons.search.utils.RegistroCore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.dao.IServiceSearchWithId;
import org.openspcoop2.generic_project.dao.IServiceSearchWithoutId;
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
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;


/***
 * 
 * Funzionalita' di supporto per la gestione delle maschere di ricerca.
 * 
 * @author pintori
 *
 */
public class DynamicUtilsService implements IDynamicUtilsService{


	private int defaultStart = 0;
	private int defaultLimit = 100;

	private static Logger log = LoggerManager.getPddMonitorSqlLogger(); 

	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;

	private ISoggettoServiceSearch soggettoDAO;

	private IAccordoServizioParteComuneServiceSearch aspcDAO = null;
	private IAccordoServizioParteSpecificaServiceSearch aspsDAO = null;

	private IServizioApplicativoServiceSearch serviziApplicativiDAO = null;
	private IPortTypeServiceSearch portTypeDAO = null;

	private IFruitoreServiceSearch fruitoreSearchDAO;

	private IPortaDominioServiceSearch pddDAO = null;

	private IPortaDelegataServiceSearch portaDelegataDAO = null;
	private IPortaApplicativaServiceSearch portaApplicativaDAO  = null;

	public static final int LIMIT_SEARCH = 10000;
	
	public DynamicUtilsService(){
		try{
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance( log).getServiceManager(ProjectInfo.getInstance(), DynamicUtilsService.log);
			this.soggettoDAO = this.utilsServiceManager.getSoggettoServiceSearch();

			this.aspcDAO = this.utilsServiceManager.getAccordoServizioParteComuneServiceSearch();
			this.aspsDAO = this.utilsServiceManager.getAccordoServizioParteSpecificaServiceSearch();

			this.serviziApplicativiDAO = this.utilsServiceManager.getServizioApplicativoServiceSearch();

			this.portTypeDAO  = this.utilsServiceManager.getPortTypeServiceSearch();

			this.fruitoreSearchDAO = this.utilsServiceManager.getFruitoreServiceSearch();

			this.pddDAO = this.utilsServiceManager.getPortaDominioServiceSearch();

			this.portaApplicativaDAO = this.utilsServiceManager.getPortaApplicativaServiceSearch();
			this.portaDelegataDAO = this.utilsServiceManager.getPortaDelegataServiceSearch();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}


	public static IExpression getExpressionTipiSoggettiCompatibiliConProtocollo(IServiceSearchWithId<?, ?> dao, IField field, String protocollo) throws Exception{
		if(protocollo != null){
			IExpression expr = dao.newExpression();

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);

			List<String> tipiSoggetti = protocolFactory.createProtocolConfiguration().getTipiSoggetti();

			return expr.in(field, tipiSoggetti);
		}

		return null;
	}

	public static IExpression getExpressionTipiServiziCompatibiliConProtocollo(IServiceSearchWithId<?, ?> dao, IField field, String protocollo) throws Exception{
		if(protocollo != null){
			IExpression expr = dao.newExpression();

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);

			List<String> tipiServizi = new ArrayList<String>();
			
			tipiServizi.addAll(protocolFactory.createProtocolConfiguration().getTipiServizi(ServiceBinding.SOAP));
			tipiServizi.addAll(protocolFactory.createProtocolConfiguration().getTipiServizi(ServiceBinding.REST));
			
			return expr.in(field, tipiServizi);
		}

		return null;
	}
	
	public static IExpression getExpressionTipiSoggettiCompatibiliConProtocollo(IServiceSearchWithoutId<?> dao, IField field, String protocollo) throws Exception{
		if(protocollo != null){
			IExpression expr = dao.newExpression();

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);

			List<String> tipiSoggetti = protocolFactory.createProtocolConfiguration().getTipiSoggetti();

			return expr.in(field, tipiSoggetti);
		}

		return null;
	}

	public static IExpression getExpressionTipiServiziCompatibiliConProtocollo(IServiceSearchWithoutId<?> dao, IField field, String protocollo) throws Exception{
		if(protocollo != null){
			IExpression expr = dao.newExpression();

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);

			List<String> tipiServizi = new ArrayList<String>();
			
			tipiServizi.addAll(protocolFactory.createProtocolConfiguration().getTipiServizi(ServiceBinding.SOAP));
			tipiServizi.addAll(protocolFactory.createProtocolConfiguration().getTipiServizi(ServiceBinding.REST));

			return expr.in(field, tipiServizi);
		}

		return null;
	}

	@Override
	public int countPdD(String tipoProtocollo) {
		try {
			IExpression expr = this.pddDAO.newExpression();
			NonNegativeNumber nnn = null;
			if(StringUtils.isNotBlank(tipoProtocollo)){

				IPaginatedExpression sogExpr = this.soggettoDAO.newPaginatedExpression();
				if(StringUtils.isNotEmpty(tipoProtocollo)){
					sogExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
				}

				List<Object> select = this.soggettoDAO.select(sogExpr, true, Soggetto.model().SERVER);

				return select != null ? select.size() : 0;
			} else {
				nnn = this.pddDAO.count(expr);
				if(nnn != null)
					return (int) nnn.longValue();
			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage());
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return 0;
	}




	/***
	 * 
	 * Restituisce l'elenco dei primi n soggetti che corrispondono all'input inserito
	 * 
	 * 
	 * @param input
	 * @return elenco risultati trovati
	 */
	@Override
	public List<Soggetto> soggettiAutoComplete(String tipoProtocollo,String input) {
		return soggettiAutoComplete(tipoProtocollo,input,false);
	}
	@Override
	public List<Soggetto> soggettiAutoComplete(String tipoProtocollo,String input,boolean searchTipo) {

		IExpression expr;
		try {
			expr = this.soggettoDAO.newExpression();

			if(!StringUtils.isEmpty(input)){
				int idx= input.indexOf("/");
				if(idx != -1){
					input = input.substring(idx + 1, input.length());
				}

				if(searchTipo){
					IExpression exprOrTipo = this.soggettoDAO.newExpression();
					exprOrTipo.ilike(Soggetto.model().TIPO_SOGGETTO, input.toLowerCase() , LikeMode.ANYWHERE);
					IExpression exprOrNome = this.soggettoDAO.newExpression();
					exprOrNome.ilike(Soggetto.model().NOME_SOGGETTO, input.toLowerCase() , LikeMode.ANYWHERE);
					expr.or(exprOrTipo,exprOrNome);
				}
				else{
					expr.ilike(Soggetto.model().NOME_SOGGETTO, input.toLowerCase() , LikeMode.ANYWHERE);
				}
			}
			if(tipoProtocollo!= null){
				expr.and();
				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
			}

			expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

			IPaginatedExpression pagExpr = this.soggettoDAO
					.toPaginatedExpression(expr);
			pagExpr.offset(this.defaultStart).limit(this.defaultLimit);

			return this.soggettoDAO.findAll(pagExpr);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Soggetto>();
	}

	//	@Override
	//	public List<Soggetto> findElencoSoggetti() {
	//		try {
	//			IExpression expr = this.soggettoDAO.newExpression();
	//
	//			expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);
	//
	//			IPaginatedExpression pagExpr = this.soggettoDAO
	//					.toPaginatedExpression(expr);
	//
	//			return this.soggettoDAO.findAll(pagExpr);
	//		} catch (ServiceException e) {
	//			log.error(e.getMessage(), e);
	//		} catch (NotImplementedException e) {
	//			log.error(e.getMessage(), e);
	//		} catch (ExpressionNotImplementedException e) {
	//			log.error(e.getMessage(), e);
	//		} catch (ExpressionException e) {
	//			log.error(e.getMessage(), e);
	//		}
	//
	//		return new ArrayList<Soggetto>();
	//	}
	//
	//	@Override
	//	public int countElencoSoggetti() {
	//		try {
	//			IExpression expr = this.soggettoDAO.newExpression();
	//			NonNegativeNumber nnn = this.soggettoDAO.count(expr);
	//
	//			if(nnn != null)
	//				return (int) nnn.longValue();
	//		} catch (ServiceException e) {
	//			log.error(e.getMessage(), e);
	//		} catch (NotImplementedException e) {
	//			log.error(e.getMessage(), e);
	//		} 
	//
	//		return 0;
	//	}

	@Override
	public List<Soggetto> findElencoSoggetti(String tipoProtocollo,String idPorta) {
		try {
			IExpression expr = this.soggettoDAO.newExpression();

			boolean addAnd = false;
			if(idPorta != null){
				expr.equals(Soggetto.model().IDENTIFICATIVO_PORTA,idPorta);
				addAnd = true;
			}

			if(tipoProtocollo!= null){
				if(addAnd)
					expr.and();

				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
			}

			expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

			IPaginatedExpression pagExpr = this.soggettoDAO
					.toPaginatedExpression(expr);

			return this.soggettoDAO.findAll(pagExpr);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Soggetto>();
	}

	@Override
	public int countElencoSoggetti(String tipoProtocollo,String idPorta) {
		try {
			IExpression expr = this.soggettoDAO.newExpression();
			boolean addAnd = false;
			if(idPorta != null){
				expr.equals(Soggetto.model().IDENTIFICATIVO_PORTA,idPorta);
				addAnd = true;
			}

			if(tipoProtocollo!= null){
				if(addAnd)
					expr.and();

				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
			}

			NonNegativeNumber nnn = this.soggettoDAO.count(expr);

			if(nnn != null)
				return (int) nnn.longValue();
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return 0;
	}

	@Override
	public Soggetto findSoggettoByTipoNome(String tipoSoggetto,	String nomeSoggetto) {

		try {
			if(StringUtils.isNotEmpty(tipoSoggetto) && StringUtils.isNotEmpty(nomeSoggetto)){
				return this.soggettoDAO.find(this.soggettoDAO.newExpression()
						.equals(Soggetto.model().NOME_SOGGETTO, nomeSoggetto).and()
						.equals(Soggetto.model().TIPO_SOGGETTO, tipoSoggetto));
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
	
	@Override
	public Soggetto findSoggettoById(long idSoggetto) {
		try {
			return ((JDBCSoggettoServiceSearch) this.soggettoDAO).get(idSoggetto);
			
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Soggetto> findElencoSoggettiFromTipoSoggetto(String tipoSoggetto) {
		try {
			return this.soggettoDAO.findAll(this.soggettoDAO
					.toPaginatedExpression(this.soggettoDAO
							.newPaginatedExpression()
							.equals(Soggetto.model().TIPO_SOGGETTO,tipoSoggetto)
							.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().NOME_SOGGETTO)));
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public int countElencoSoggettiFromTipoSoggetto(String tipoSoggetto) {
		try {
			IExpression expr = this.soggettoDAO.newExpression();
			expr.equals(Soggetto.model().TIPO_SOGGETTO,tipoSoggetto);
			NonNegativeNumber nnn = this.soggettoDAO.count(expr);

			if(nnn != null)
				return (int) nnn.longValue();
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}

		return 0;
	}

	@Override
	public List<Soggetto> findElencoSoggettiFromTipoPdD(String tipoProtocollo,TipoPdD tipoPdD) {
		try {
			IPaginatedExpression pagExpr = this.pddDAO.newPaginatedExpression();
			pagExpr.equals(PortaDominio.model().TIPO, tipoPdD).
			sortOrder(SortOrder.ASC).addOrder(PortaDominio.model().NOME);
			List<IdPortaDominio> idPorte = this.pddDAO.findAllIds(pagExpr);

			IPaginatedExpression sogPagExpr = this.soggettoDAO.newPaginatedExpression();
			if(idPorte != null && idPorte.size() > 0){
				List<String> nomiPorte = new ArrayList<String>();
				for (IdPortaDominio idPorta : idPorte) {
					nomiPorte.add(idPorta.getNome());
				}
				sogPagExpr.in(Soggetto.model().SERVER, nomiPorte);
			}
			sogPagExpr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

			if(StringUtils.isNotEmpty(tipoProtocollo)){
				sogPagExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
			}

			return this.soggettoDAO.findAll(sogPagExpr);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return new ArrayList<Soggetto>(); 
	}

	@Override
	public int countElencoSoggettiFromTipoTipoPdD(String tipoProtocollo,TipoPdD tipoPdD) {
		try {
			if(tipoPdD!=null){
				IPaginatedExpression pagExpr = this.pddDAO.newPaginatedExpression();
				pagExpr.equals(PortaDominio.model().TIPO, tipoPdD).
				sortOrder(SortOrder.ASC).addOrder(PortaDominio.model().NOME);
				List<IdPortaDominio> idPorte = this.pddDAO.findAllIds(pagExpr);
				IExpression sogExpr = this.soggettoDAO.newExpression();
				if(idPorte != null && idPorte.size() > 0){
					List<String> nomiPorte = new ArrayList<String>();
					for (IdPortaDominio idPorta : idPorte) {
						nomiPorte.add(idPorta.getNome());
					}
					sogExpr.in(Soggetto.model().SERVER, nomiPorte);
				}
				else{
					// se non esistono porte non esistono nemmeno soggetti
					return 0;
				}
	
				if(StringUtils.isNotEmpty(tipoProtocollo)){
					sogExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
				}
	
				NonNegativeNumber nnn = this.soggettoDAO.count(sogExpr);
	
				if(nnn != null){
					return (int) nnn.longValue(); 
				}
			}
			else{
				// devo cercare i soggetto a cui non è stata associata una pdd
				IExpression sogExpr = this.soggettoDAO.newExpression();
				sogExpr.isNull(Soggetto.model().SERVER);
				sogExpr.isEmpty(Soggetto.model().SERVER);
				sogExpr.or();
				
				NonNegativeNumber nnn = this.soggettoDAO.count(sogExpr);
				
				if(nnn != null){
					return (int) nnn.longValue(); 
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return 0;
	}
	
	@Override
	public boolean checkTipoPdd(String nome,TipoPdD tipoPdD) {
		try {
			if(nome==null || "".equals(nome) || "-".equals(nome)){
				if(TipoPdD.ESTERNO.equals(tipoPdD)){
					return true;
				}
				else{
					return false;
				}
			}
			
			IPaginatedExpression pagExpr = this.pddDAO.newPaginatedExpression();
			pagExpr.equals(PortaDominio.model().TIPO, tipoPdD).equals(PortaDominio.model().NOME, nome).
			sortOrder(SortOrder.ASC).addOrder(PortaDominio.model().NOME);
			pagExpr.limit(1);
			List<IdPortaDominio> idPorte = this.pddDAO.findAllIds(pagExpr);

			return idPorte != null && idPorte.size() > 0;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false; 
	}


	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo) {
		try {
			IExpression expr = this.aspsDAO.newExpression();
			expr.sortOrder(SortOrder.ASC)
			.addOrder(AccordoServizioParteSpecifica.model().TIPO) 
			.addOrder(AccordoServizioParteSpecifica.model().NOME)
			;

			IPaginatedExpression pagExpr = this.aspsDAO
					.toPaginatedExpression(expr);

			if(tipoProtocollo!= null){
				pagExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}

			return this.aspsDAO.select(pagExpr, true, AccordoServizioParteSpecifica.model().TIPO, AccordoServizioParteSpecifica.model().NOME);

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Map<String,Object>>();
	}

	@Override
	public int countElencoServizi(String tipoProtocollo) {
		try {
			IExpression expr = this.aspsDAO.newExpression();
			NonNegativeNumber nnn = this.aspsDAO.count(expr);

			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}
			if(nnn != null)
				return (int) nnn.longValue();
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}  

		return 0;
	}

	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo,Soggetto soggetto) {
		return findElencoServizi(tipoProtocollo, soggetto, null);
	}
	
	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo, Soggetto soggetto, String val) {
		return findElencoServizi(tipoProtocollo,soggetto,val,false);
	}
	
	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo, Soggetto soggetto, String val, boolean searchTipo) {
		log.debug("Get Lista Servizi [Soggetto: " + (soggetto != null ? soggetto.getNomeSoggetto() : "Null")+ "], VAL: ["+val+"]");

		try {
			IExpression expr = this.aspsDAO.newExpression();

			if (soggetto != null) {
				expr.equals(
						AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
						soggetto.getTipoSoggetto());
				expr.and()
				.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,
						soggetto.getNomeSoggetto());
			}

			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}
			
			if(StringUtils.isNotEmpty(val)){
				if(searchTipo){
					IExpression exprOrTipo = this.aspsDAO.newExpression();
					exprOrTipo.ilike(AccordoServizioParteSpecifica.model().TIPO, val , LikeMode.ANYWHERE);
					IExpression exprOrNome = this.aspsDAO.newExpression();
					exprOrNome.ilike(AccordoServizioParteSpecifica.model().NOME, val , LikeMode.ANYWHERE);
					expr.or(exprOrTipo,exprOrNome);
				}
				else{
					expr.and().ilike(AccordoServizioParteSpecifica.model().NOME,val,LikeMode.ANYWHERE);		
				}
			}
			
			expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

			IPaginatedExpression pagExpr = this.aspsDAO
					.toPaginatedExpression(expr);

			CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");

			List<Map<String, Object>> list = this.aspsDAO.select(pagExpr,true,AccordoServizioParteSpecifica.model().TIPO, AccordoServizioParteSpecifica.model().NOME,cf);

			for (Map<String, Object> map : list) {
				Long idAccordoLong = (Long) map.remove("idAccordo");

				AccordoServizioParteComune aspc = ((JDBCAccordoServizioParteComuneServiceSearch)this.aspcDAO).get(idAccordoLong);

				map.put("idAccordo", aspc);
			}

			return list;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Map<String, Object>>();
	}
	
	@Override
	public AccordoServizioParteComune getAccordoServizio(String tipoProtocollo, IDSoggetto idSoggetto, String tipoServizio, String nomeServizio) {
		log.debug("Get Lista Servizi [Soggetto: " + (idSoggetto != null ? idSoggetto.getNome() : "Null")
				+ "], Servizio: ["+tipoServizio + "/"+ nomeServizio +"]");
		AccordoServizioParteComune aspc = null;
		try {
			IExpression expr = this.aspsDAO.newExpression();

			if (idSoggetto != null) {
				expr.equals(
						AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
						idSoggetto.getTipo());
				expr.and()
				.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,
						idSoggetto.getNome());
			}

			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}
			
			if(StringUtils.isNotEmpty(tipoServizio) && StringUtils.isNotEmpty(nomeServizio)){
					expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio);
					expr.and();
					expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio);
			}
			
			expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

			IPaginatedExpression pagExpr = this.aspsDAO
					.toPaginatedExpression(expr);

			CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");

			List<Object> list = this.aspsDAO.select(pagExpr,true,cf);

			for (Object idAccordoObj : list) {
				Long idAccordoLong = (Long) idAccordoObj;
				  aspc = ((JDBCAccordoServizioParteComuneServiceSearch)this.aspcDAO).get(idAccordoLong);
			}

			return aspc;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public int countElencoServizi(String tipoProtocollo,Soggetto soggetto) {
		try {
			IExpression expr = this.aspsDAO.newExpression();
			if (soggetto != null) {
				expr.equals(
						AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
						soggetto.getTipoSoggetto());
				expr.and()
				.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,
						soggetto.getNomeSoggetto());
			}
			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}
			NonNegativeNumber nnn = this.aspsDAO.count(expr);

			if(nnn != null)
				return (int) nnn.longValue();
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return 0;
	}

	//	@Override
	public List<Map<String, Object>> findElencoServizi(List<Soggetto> soggetti) {

		log.debug("Get Lista Servizi dei soggetti");

		try {
			IExpression expr = this.aspsDAO.newExpression();

			if (soggetti != null && soggetti.size() > 0) {
				IExpression exprSoggetti = this.aspsDAO.newExpression();
				for (Soggetto soggetto : soggetti) {
					IExpression expr2 = this.aspsDAO.newExpression();
					expr2.equals(
							AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
							soggetto.getTipoSoggetto());
					expr2.and()
					.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,
							soggetto.getNomeSoggetto());
					exprSoggetti.or(expr2);
				}

				expr.and(exprSoggetti);
			}

			expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

			IPaginatedExpression pagExpr = this.aspsDAO
					.toPaginatedExpression(expr);

			CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");

			List<Map<String, Object>> list = this.aspsDAO.select(pagExpr,true,AccordoServizioParteSpecifica.model().TIPO, AccordoServizioParteSpecifica.model().NOME,cf);

			for (Map<String, Object> map : list) {
				Long idAccordoLong = (Long) map.remove("idAccordo");

				AccordoServizioParteComune aspc = ((JDBCAccordoServizioParteComuneServiceSearch)this.aspcDAO).get(idAccordoLong);

				map.put("idAccordo", aspc);
			}

			return list;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Map<String, Object>>();
	}


	@Override
	public Map<String, String> findAzioniFromServizio(String tipoProtocollo ,String tipoServizio ,	String nomeServizio,String tipoErogatore ,	String nomeErogatore ,Integer versioneServizio) {
		log.debug("Get Lista Azioni from Accordo Servizio [nome Servizio: " + nomeServizio + "]");

		try {
			Map<String, String> azioniConLabel = RegistroCore.getAzioniConLabel((JDBCServiceManager) this.utilsServiceManager, tipoProtocollo, tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio);
			
			return azioniConLabel;
 
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return new HashMap<String, String>();

	}

	@Override
	public int countAzioniFromServizio(String tipoProtocollo,String tipoServizio ,
			String nomeServizio,String tipoErogatore ,	String nomeErogatore,Integer versioneServizio) {
		// Implementazione inefficiente
		return this.findAzioniFromServizio(tipoProtocollo,tipoServizio, nomeServizio,tipoErogatore,nomeErogatore,versioneServizio).size();
	}

	@Override
	public List<Object> findElencoServiziApplicativi(String tipoProtocollo,Soggetto soggetto) {

		log.debug("Get Lista Servizi Applicativi [Soggetto: " + (soggetto != null ? soggetto.getNomeSoggetto() : "Null")	+ "]");
		// if (erogatore != null) {
		// list = this.service
		// .getEntityManager()
		// .createQuery(
		// "select s.nome from ServizioApplicativo s where s.soggetto.id = :id_soggetto order by s.nome asc")
		// .setParameter("id_soggetto", erogatore.getId())
		// .getResultList();
		// }
		try {
			IExpression expr = this.serviziApplicativiDAO.newExpression();

			if (soggetto != null) {
				expr.equals(ServizioApplicativo.model().ID_SOGGETTO.TIPO,
						soggetto.getTipoSoggetto());
				expr.and().equals(ServizioApplicativo.model().ID_SOGGETTO.NOME,
						soggetto.getNomeSoggetto());
			}

			if(tipoProtocollo != null)
				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.serviziApplicativiDAO, ServizioApplicativo.model().ID_SOGGETTO.TIPO, tipoProtocollo));

			expr.sortOrder(SortOrder.ASC).addOrder(ServizioApplicativo.model().NOME);

			IPaginatedExpression pagExpr = this.serviziApplicativiDAO
					.toPaginatedExpression(expr);

			return this.serviziApplicativiDAO.select(pagExpr,true,ServizioApplicativo.model().NOME);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return new ArrayList<Object>();
	}

	@Override
	public int countElencoServiziApplicativi(String tipoProtocollo,Soggetto soggetto) {
		log.debug("countElencoServiziApplicativi [Soggetto: " + (soggetto != null ? soggetto.getNomeSoggetto() : "Null")	+ "]");
		try {
			IExpression expr = this.serviziApplicativiDAO.newExpression();

			if (soggetto != null) {
				expr.equals(ServizioApplicativo.model().ID_SOGGETTO.TIPO,
						soggetto.getTipoSoggetto());
				expr.and().equals(ServizioApplicativo.model().ID_SOGGETTO.NOME,
						soggetto.getNomeSoggetto());
			}

			if(tipoProtocollo != null)
				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.serviziApplicativiDAO, ServizioApplicativo.model().ID_SOGGETTO.TIPO, tipoProtocollo));


			NonNegativeNumber nnn = this.serviziApplicativiDAO.count(expr);

			if(nnn != null){
				return (int) nnn.longValue();
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return 0;
	}


	@Override
	public PortType getPortTypeFromAccordoServizio(String tipoProtocollo,IDAccordo idAccordo ,String nomeServizio) {

		log
		.debug("Get Port Type from Accordo Servizio [Accordo: "+ idAccordo.getNome() + "], [nome Servizio: " + nomeServizio + "]");

		IExpression expr = null; 
		try {
			expr = this.aspsDAO.newExpression();
			expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();

			if(idAccordo.getNome() != null)
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());

			if(idAccordo.getVersione() != null)
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());

			if(idAccordo.getSoggettoReferente() != null){
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome());
			}

			AccordoServizioParteSpecifica asps = this.aspsDAO.find(expr);

			if(asps != null && asps.getPortType()!= null){

				expr = this.portTypeDAO.newExpression();

				expr.equals(PortType.model().NOME, asps.getPortType());
				if(idAccordo.getNome() != null)
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());

				if(idAccordo.getVersione() != null)
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());

				if(idAccordo.getSoggettoReferente() != null){
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome());
				}

				return this.portTypeDAO.find(expr);

			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public int countPortTypeFromAccordoServizio(String tipoProtocollo,IDAccordo idAccordo,
			String nomeServizio) {
		log
		.debug("count Port Type from Accordo Servizio [Accordo: "+ idAccordo.getNome() + "], [nome Servizio: " + nomeServizio + "]");

		IExpression expr = null; 
		try {
			expr = this.aspsDAO.newExpression();
			expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();

			if(idAccordo.getNome() != null)
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());

			if(idAccordo.getVersione() != null)
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());

			if(idAccordo.getSoggettoReferente() != null){
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome());
			}

			AccordoServizioParteSpecifica asps = this.aspsDAO.find(expr);

			if(asps != null && asps.getPortType()!= null){

				expr = this.portTypeDAO.newExpression();

				expr.equals(PortType.model().NOME, asps.getPortType());
				if(idAccordo.getNome() != null)
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());

				if(idAccordo.getVersione() != null)
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());

				if(idAccordo.getSoggettoReferente() != null){
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome());
				}

				NonNegativeNumber nnn =  this.portTypeDAO.count(expr);

				if(nnn != null){
					return (int) nnn.longValue();
				}
			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public AccordoServizioParteSpecifica getAspsFromValues(String tipoServizio, String nomeServizio, String tipoErogatore, String nomeErogatore,Integer versioneServizio){
		log.debug("Get AccordoServizioParteSpecifica from Tipo/Nome Servizio [Tipo: "+ tipoServizio + "], [nome: " + nomeServizio + "]");

		IExpression expr = null; 
		try {

			expr = this.aspsDAO.newExpression();
			expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoErogatore).and();
			expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeErogatore);
			expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();
			expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio).and();
			expr.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio);

			return this.aspsDAO.find(expr);

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
	
	@Override
	public AccordoServizioParteSpecifica getAspsFromId(long idServizio) {
		log.debug("Get AccordoServizioParteSpecifica from Id ["+ idServizio + "]");

		try {
			return ((JDBCAccordoServizioParteSpecificaServiceSearch)this.aspsDAO).get(idServizio);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
	

	/*****
	 * 
	 * Restituisce gli accordi di servizio relativi al soggetto passato come parametro.
	 * Se isReferente == true il soggetto e' il referente degli accordi.
	 * Se isErogatore == true il soggetto e' erogatore di un servizio definito nell'accordo.
	 * 
	 */
	@Override
	public List<AccordoServizioParteComune> getAccordiServizio(String tipoProtocollo,String tipoSoggetto , String nomeSoggetto,
			boolean isReferente, boolean isErogatore) {
		List<AccordoServizioParteComune> toRet = new ArrayList<AccordoServizioParteComune>();
		log.debug("Get AccordiServizio from Tipo/Nome Soggetto [Tipo: "+ tipoSoggetto + "], [nome: " + nomeSoggetto + "] Referente["+isReferente+"] Erogatore ["+isErogatore+"]");
		try {
			// se il soggetto e' stato selezionato
			if(StringUtils.isNotEmpty(nomeSoggetto) && StringUtils.isNotEmpty(tipoSoggetto)){
				// utilizzo il soggetto passato come parametro come referente
				if(isReferente){
					// restituisco tutti gli accordi parte comune presenti con referente uguale al parametro
					IPaginatedExpression pagExpr = this.aspcDAO.newPaginatedExpression();

					pagExpr.equals(AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoSoggetto).and().equals(AccordoServizioParteComune.model().ID_REFERENTE.NOME, nomeSoggetto);
					if(tipoProtocollo != null)
						pagExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspcDAO, AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoProtocollo));
					pagExpr.sortOrder(SortOrder.ASC);
					pagExpr.addOrder(AccordoServizioParteComune.model().ID_REFERENTE.TIPO);
					pagExpr.addOrder(AccordoServizioParteComune.model().ID_REFERENTE.NOME);
					pagExpr.addOrder(AccordoServizioParteComune.model().NOME);
					pagExpr.addOrder(AccordoServizioParteComune.model().VERSIONE);

					pagExpr.offset(0).limit(LIMIT_SEARCH);
					toRet = this.aspcDAO.findAll(pagExpr);
					return toRet;
				}

				//utilizzo il soggetto come erogatore
				if(isErogatore){
					IExpression expr = this.aspsDAO.newExpression();
					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggetto).and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggetto);

					if(tipoProtocollo != null)
						expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));

					expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)
					.addOrder(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)
					.addOrder(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)
					.addOrder(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE);

					IPaginatedExpression pagExpr = this.aspsDAO.toPaginatedExpression(expr);

					CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");
					List<Map<String, Object>> list = null;
					try{
						list = this.aspsDAO.select(pagExpr,true,AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO,
								AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME,
								AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME,
								AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE	,cf);
					}
					catch (NotFoundException e) {
						log.debug("Nessun risultato trovato");
					} 

					if(list != null && list.size() > 0){
						for (Map<String, Object> map : list) {
							Long idAccordoLong = (Long) map.remove("idAccordo");
							try{
								AccordoServizioParteComune aspc = ((JDBCAccordoServizioParteComuneServiceSearch)this.aspcDAO).get(idAccordoLong);
								toRet.add(aspc);
							}
							catch (NotFoundException e) {
								log.debug("Accordo si servizio non trovato");
							} 
						}
					}
				}
			} else {
				// restituisco tutti gli accordi parte comune presenti
				IPaginatedExpression pagExpr = this.aspcDAO.newPaginatedExpression();
				if(tipoProtocollo != null)
					pagExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspcDAO, AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoProtocollo));
				pagExpr.sortOrder(SortOrder.ASC);
				pagExpr.addOrder(AccordoServizioParteComune.model().ID_REFERENTE.TIPO);
				pagExpr.addOrder(AccordoServizioParteComune.model().ID_REFERENTE.NOME);
				pagExpr.addOrder(AccordoServizioParteComune.model().NOME);
				pagExpr.addOrder(AccordoServizioParteComune.model().VERSIONE);

				pagExpr.offset(0).limit(LIMIT_SEARCH);
				toRet = this.aspcDAO.findAll(pagExpr);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return toRet;
	}

	@Override
	public int countAccordiServizio(String tipoProtocollo,String tipoSoggetto, String nomeSoggetto,
			boolean isReferente, boolean isErogatore) {
		NonNegativeNumber nnn = null;
		log.debug("countAccordiServizio from Tipo/Nome Soggetto [Tipo: "+ tipoSoggetto + "], [nome: " + nomeSoggetto + "] Referente["+isReferente+"] Erogatore ["+isErogatore+"]");
		try {
			if(StringUtils.isNotEmpty(nomeSoggetto) && StringUtils.isNotEmpty(tipoSoggetto)){
				// utilizzo il soggetto passato come parametro come referente
				if(isReferente){
					// restituisco tutti gli accordi parte comune presenti con referente uguale al parametro
					IExpression expr = this.aspcDAO.newExpression();
					if(tipoProtocollo != null)
						expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspcDAO, AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoProtocollo));

					expr.equals(AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoSoggetto) .and() .equals(AccordoServizioParteComune.model().ID_REFERENTE.NOME, nomeSoggetto);

					nnn = this.aspcDAO.count(expr);
				}

				//utilizzo il soggetto come erogatore
				if(isErogatore){
					CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");
					IExpression expr = this.aspsDAO.newExpression();
					if(tipoProtocollo != null)
						expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));

					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggetto).and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggetto);

					FunctionField countF = new FunctionField(cf, Function.COUNT_DISTINCT, "numeroAccordi");

					Object aggregate = this.aspsDAO.aggregate(expr,countF);

					if(aggregate != null){
						if(aggregate instanceof Long){
							Long count = (Long) aggregate;
							return count.intValue();
						}
					}

				}

			}else {
				IExpression expr = this.aspcDAO.newExpression();
				if(tipoProtocollo != null)
					expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspcDAO, AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoProtocollo));

				nnn = this.aspcDAO.count(expr);
			}
			if(nnn != null){
				return (int) nnn.longValue();
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}    

		return 0;
	}

	@Override
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto) {
		return this.getServizi(tipoProtocollo, uriAccordoServizio, tipoSoggetto, nomeSoggetto, null);
	}
	
	@Override
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo, String uriAccordoServizio,
			String tipoSoggetto, String nomeSoggetto, String val) {
		return getServizi(tipoProtocollo, uriAccordoServizio, tipoSoggetto, nomeSoggetto, val, false);
	}
	
	@Override
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo, String uriAccordoServizio,
			String tipoSoggetto, String nomeSoggetto, String val, boolean searchTipo) {
			log.debug("getServizi: UriAccordoServizio: [" + uriAccordoServizio + "] SoggettoErogatore: [" + tipoSoggetto +"/"+nomeSoggetto + "] Val ["+val+"]" );
			IExpression expr;
			try {
				expr = this.aspsDAO.newExpression();
				if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty()){
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);



					if(idAccordo.getNome() != null){
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
					}

					if( idAccordo.getVersione() != null){
						expr.and().equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
					} else {
						expr.and().isNull(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE);
					}

					if(idAccordo.getSoggettoReferente() != null){
						if(idAccordo.getSoggettoReferente().getTipo() != null)
							expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

						if(idAccordo.getSoggettoReferente().getNome() != null)
							expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
					} else {
						CustomField cf2 = new CustomField("idReferente", Integer.class, "id_referente", "accordi");
						expr.lessEquals(cf2, 0);
					}
				}
				if(StringUtils.isNotEmpty(nomeSoggetto) && StringUtils.isNotEmpty(tipoSoggetto)){
					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggetto)
					.and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggetto);
				}
				if(tipoProtocollo!= null){
					expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
				}
				
				if(StringUtils.isNotEmpty(val)){
					if(searchTipo){
						IExpression exprOrTipo = this.aspsDAO.newExpression();
						exprOrTipo.ilike(AccordoServizioParteSpecifica.model().TIPO, val , LikeMode.ANYWHERE);
						IExpression exprOrNome = this.aspsDAO.newExpression();
						exprOrNome.ilike(AccordoServizioParteSpecifica.model().NOME, val , LikeMode.ANYWHERE);
						expr.or(exprOrTipo,exprOrNome);
					}
					else{
						expr.and().ilike(AccordoServizioParteSpecifica.model().NOME,val,LikeMode.ANYWHERE);
					}
				}
					
				
				expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME)
				.addOrder(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO).addOrder(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME)
				;

				IPaginatedExpression pagExpr = this.aspsDAO.toPaginatedExpression(expr);

				pagExpr.offset(0).limit(LIMIT_SEARCH);


				return this.aspsDAO.findAll(pagExpr);


		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<AccordoServizioParteSpecifica>();
	}

	@Override
	public int countServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto,
			String nomeSoggetto) {
		log.debug("countServizi: UriAccordoServizio: [" + uriAccordoServizio + "] SoggettoErogatore: [" + tipoSoggetto +"/"+nomeSoggetto + "]" );
		IExpression expr;
		NonNegativeNumber nnn =null;
		try {
			expr = this.aspsDAO.newExpression();
			if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty()){
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);

				if(idAccordo.getNome() != null){
					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
				}

				if( idAccordo.getVersione() != null){
					expr.and().equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
				} else {
					expr.and().isNull(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE);
				}

				if(idAccordo.getSoggettoReferente() != null){
					if(idAccordo.getSoggettoReferente().getTipo() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

					if(idAccordo.getSoggettoReferente().getNome() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
				} else {
					CustomField cf2 = new CustomField("idReferente", Integer.class, "id_referente", "accordi");
					expr.lessEquals(cf2, 0);
				}
			}
			if(StringUtils.isNotEmpty(nomeSoggetto) && StringUtils.isNotEmpty(tipoSoggetto)){
				expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggetto)
				.and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggetto);
			}
			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}
			nnn = this.aspsDAO.count(expr);
			if(nnn != null){
				return (int) nnn.longValue();
			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return 0;
	}


	public AccordoServizioParteSpecifica convertPortTypeName (String portTypeName){
		// TODO controllare la versione 
		try{

			int idx = portTypeName.indexOf("(");

			if(idx == -1)
				return null;

			String tipoNomeServizio = portTypeName.substring(0, idx).trim();
			String tipoNomeSoggetto = portTypeName.substring(idx+1).replace(")","").trim();

			if(tipoNomeServizio.indexOf("/") == -1)
				return null;

			if(tipoNomeSoggetto.indexOf("/") == -1)
				return null;

			String tipoServizio = tipoNomeServizio.split("/")[0];
			String nomeServizio = tipoNomeServizio.split("/")[1];

			String tipoSoggetto = tipoNomeSoggetto.split("/")[0];
			String nomeSoggetto = tipoNomeSoggetto.split("/")[1];

			IExpression expr = this.aspsDAO.newExpression();


			expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and()
			.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio).and()
			.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggetto).and()
			.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggetto);

			return this.aspsDAO.find(expr);
		}catch(NotFoundException e){
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}


		return null;
	}

	@Override
	public List<Soggetto> getSoggettiErogatoreAutoComplete(String tipoProtocollo,
			String uriAccordoServizio , String input) {
		IExpression expr;
		try {

			int idx= input.indexOf("/");
			if(idx != -1){
				input = input.substring(idx + 1, input.length());
			}

			// non ho scelto un accordo di servizio
			if(uriAccordoServizio != null && !uriAccordoServizio.isEmpty()) 
			{
				expr = this.aspsDAO.newExpression();

				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);


				boolean addAnd = false;

				if(idAccordo.getNome() != null){
					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
					addAnd = true;
				}

				if( idAccordo.getVersione() != null){
					if(addAnd)
						expr.and();

					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
					addAnd = true;
				}

				if(idAccordo.getSoggettoReferente() != null){
					if(addAnd)
						expr.and();

					if(idAccordo.getSoggettoReferente().getTipo() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

					if(idAccordo.getSoggettoReferente().getNome() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
				}

				expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

				IPaginatedExpression pagExpr =this.aspsDAO.toPaginatedExpression(expr);

				pagExpr.offset(0).limit(LIMIT_SEARCH);

				List<AccordoServizioParteSpecifica> lstAsps = this.aspsDAO.findAll(pagExpr);

				if(lstAsps != null){
					List<Soggetto> lstSog = new ArrayList<Soggetto>();
					Set<Soggetto> setSog = new HashSet<Soggetto>();
					List<String> soggettiGiaInseriti = new ArrayList<String>();

					for(AccordoServizioParteSpecifica asps : lstAsps){

						expr = this.soggettoDAO.newExpression();
						if(asps.getIdErogatore()!= null){
							if(asps.getIdErogatore().getTipo() != null)
								expr.equals(Soggetto.model().TIPO_SOGGETTO, asps.getIdErogatore().getTipo()).and();

							if(asps.getIdErogatore().getNome() != null)
								expr.equals(Soggetto.model().NOME_SOGGETTO, asps.getIdErogatore().getNome());
						}

						expr 
						.ilike(Soggetto.model().NOME_SOGGETTO, input.toLowerCase(), LikeMode.ANYWHERE);

						expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

						Soggetto s = this.soggettoDAO.find(expr);

						if(s != null){
							String key = s.getTipoSoggetto() + "/" + s.getNomeSoggetto();
							if(soggettiGiaInseriti.contains(key)==false){
								setSog.add(s);
								soggettiGiaInseriti.add(key);
							}
						}


					}

					if(setSog.size() > 0)
						lstSog.addAll(setSog);

					return lstSog;
				}
			}
			else 	{
				expr = this.soggettoDAO.newExpression();


				expr.ilike(Soggetto.model().NOME_SOGGETTO,	input.toLowerCase(), LikeMode.ANYWHERE);
				if(tipoProtocollo!= null){
					expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo( this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
				}

				expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

				IPaginatedExpression pagExpr = this.soggettoDAO
						.toPaginatedExpression(expr);
				pagExpr.offset(0).limit(LIMIT_SEARCH);

				return this.soggettoDAO.findAll(pagExpr);

			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Soggetto>();
	}

	@Override
	public List<Soggetto> getSoggettiFruitoreAutoComplete(String tipoProtocollo,
			String uriAccordoServizio,   String input) {

		IExpression expr;
		try {

			int idx= input.indexOf("/");
			if(idx != -1){
				input = input.substring(idx + 1, input.length());
			}

			// non ho scelto un accordo di servizio
			if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty() ) {
				expr = this.aspsDAO.newExpression();

				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);

				boolean addAnd = false;

				if(idAccordo.getNome() != null){
					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
					addAnd = true;
				}

				if( idAccordo.getVersione() != null){
					if(addAnd)
						expr.and();

					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
					addAnd = true;
				}

				if(idAccordo.getSoggettoReferente() != null){
					if(addAnd)
						expr.and();

					if(idAccordo.getSoggettoReferente().getTipo() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

					if(idAccordo.getSoggettoReferente().getNome() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
				}
				expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

				IPaginatedExpression pagExpr =this.aspsDAO.toPaginatedExpression(expr);

				pagExpr.offset(0).limit(LIMIT_SEARCH);

				List<AccordoServizioParteSpecifica> lstAsps = this.aspsDAO.findAll(pagExpr);

				if(lstAsps != null){

					List<Soggetto> lstSog = new ArrayList<Soggetto>();
					Set<Soggetto> setSog = new HashSet<Soggetto>();
					List<String> soggettiGiaInseriti = new ArrayList<String>();
					for(AccordoServizioParteSpecifica asps : lstAsps){

						expr = this.fruitoreSearchDAO.newExpression();


						addAnd = false;

						if(asps.getNome() != null){
							expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME,asps.getNome());
							addAnd = true;
						}

						if( asps.getTipo()!= null){
							if(addAnd)
								expr.and();

							expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO,  asps.getTipo());
							addAnd = true;
						}

						if(asps.getIdErogatore()!= null){
							if(addAnd)
								expr.and();

							if(asps.getIdErogatore().getTipo() != null)
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO,asps.getIdErogatore().getTipo()).and();

							if(asps.getIdErogatore().getNome() != null)
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME,asps.getIdErogatore().getNome()).and();
						}

						expr.ilike(Fruitore.model().ID_FRUITORE.NOME, input.toLowerCase(), LikeMode.ANYWHERE);

						expr.sortOrder(SortOrder.ASC).addOrder(Fruitore.model().ID_FRUITORE.TIPO).addOrder(Fruitore.model().ID_FRUITORE.NOME);

						pagExpr = this.fruitoreSearchDAO.toPaginatedExpression(expr);

						pagExpr.offset(0).limit(LIMIT_SEARCH);

						List<Fruitore> lstFruitori = this.fruitoreSearchDAO.findAll(pagExpr); 

						if(lstFruitori != null){
							for (Fruitore fruitore : lstFruitori) {
								Soggetto s = this.soggettoDAO.get(fruitore.getIdFruitore());

								if(s != null){
									String key = s.getTipoSoggetto() + "/" + s.getNomeSoggetto();
									if(soggettiGiaInseriti.contains(key)==false){
										setSog.add(s);
										soggettiGiaInseriti.add(key);
									}
								}
							}

							if(setSog.size() > 0)
								lstSog.addAll(setSog);


						}				

					}
					return lstSog;			

				}

			} else {
				expr = this.soggettoDAO.newExpression();

				expr.ilike(Soggetto.model().NOME_SOGGETTO,	input.toLowerCase(), LikeMode.ANYWHERE);

				expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

				IPaginatedExpression pagExpr = this.soggettoDAO
						.toPaginatedExpression(expr);
				pagExpr.offset(0).limit(LIMIT_SEARCH);

				return this.soggettoDAO.findAll(pagExpr);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Soggetto>();
	}

	@Override
	public List<Soggetto> getSoggettiFruitoreFromAccordoServizioAndErogatoreAutoComplete(String tipoProtocollo,String tipoServizio ,String nomeServizio,
			String uriAccordoServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String input) {

		IExpression expr;
		try {

			int idx= input.indexOf("/");
			if(idx != -1){
				input = input.substring(idx + 1, input.length());
			}

			// ho scelto un accordo di servizio
			if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty() ) {
				expr = this.aspsDAO.newExpression();

				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);

				boolean addAnd = false;

				if(idAccordo.getNome() != null){
					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
					addAnd = true;
				}

				if( idAccordo.getVersione() != null){
					if(addAnd)
						expr.and();

					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
					addAnd = true;
				}

				if(idAccordo.getSoggettoReferente() != null){
					if(addAnd)
						expr.and();

					if(idAccordo.getSoggettoReferente().getTipo() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

					if(idAccordo.getSoggettoReferente().getNome() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
				}

				// Se ho specificato il soggetto erogatore
				if(StringUtils.isNotEmpty(tipoErogatore) && StringUtils.isNotEmpty(nomeErogatore)){
					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeErogatore).and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,tipoErogatore);

				}
				if(StringUtils.isNotEmpty(nomeServizio ))
					expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();
				if(StringUtils.isNotEmpty(tipoServizio ))
					expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio).and();
				if(versioneServizio != null)
					expr.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio).and();

				expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

				IPaginatedExpression pagExpr =this.aspsDAO.toPaginatedExpression(expr);

				pagExpr.offset(0).limit(LIMIT_SEARCH);

				List<AccordoServizioParteSpecifica> lstAsps = this.aspsDAO.findAll(pagExpr);

				if(lstAsps != null){

					List<Soggetto> lstSog = new ArrayList<Soggetto>();
					Set<Soggetto> setSog = new HashSet<Soggetto>();
					List<String> soggettiGiaInseriti = new ArrayList<String>();
					for(AccordoServizioParteSpecifica asps : lstAsps){

						expr = this.fruitoreSearchDAO.newExpression();


						addAnd = false;

						if(asps.getNome() != null){
							expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME,asps.getNome());
							addAnd = true;
						}

						if( asps.getTipo()!= null){
							if(addAnd)
								expr.and();

							expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO,  asps.getTipo());
							addAnd = true;
						}

						if(asps.getIdErogatore()!= null){
							if(addAnd)
								expr.and();

							if(asps.getIdErogatore().getTipo() != null)
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO,asps.getIdErogatore().getTipo()).and();

							if(asps.getIdErogatore().getNome() != null)
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME,asps.getIdErogatore().getNome()).and();
						}
						expr.ilike(Fruitore.model().ID_FRUITORE.NOME, input.toLowerCase(), LikeMode.ANYWHERE);
						expr.sortOrder(SortOrder.ASC).addOrder(Fruitore.model().ID_FRUITORE.TIPO).addOrder(Fruitore.model().ID_FRUITORE.NOME);

						pagExpr = this.fruitoreSearchDAO.toPaginatedExpression(expr);

						pagExpr.offset(0).limit(LIMIT_SEARCH);

						List<Fruitore> lstFruitori = this.fruitoreSearchDAO.findAll(pagExpr); 

						if(lstFruitori != null){
							for (Fruitore fruitore : lstFruitori) {
								Soggetto s = this.soggettoDAO.get(fruitore.getIdFruitore());

								if(s != null){
									String key = s.getTipoSoggetto() + "/" + s.getNomeSoggetto();
									if(soggettiGiaInseriti.contains(key)==false){
										setSog.add(s);
										soggettiGiaInseriti.add(key);
									}
								}
							}

							if(setSog.size() > 0)
								lstSog.addAll(setSog);


						}				

					}
					return lstSog;
				}

			} else {
				// accordo non specificato
				// Se ho specificato il soggetto erogatore
				if(StringUtils.isNotEmpty(tipoErogatore) && StringUtils.isNotEmpty(nomeErogatore)){
					expr = this.aspsDAO.newExpression();
					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeErogatore).and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,tipoErogatore);

					if(StringUtils.isNotEmpty(nomeServizio ))
						expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();
					if(StringUtils.isNotEmpty(tipoServizio ))
						expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio).and();
					if(versioneServizio != null)
						expr.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio).and();

					expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

					IPaginatedExpression pagExpr =this.aspsDAO.toPaginatedExpression(expr);

					pagExpr.offset(0).limit(LIMIT_SEARCH);

					List<AccordoServizioParteSpecifica> lstAsps = this.aspsDAO.findAll(pagExpr);

					if(lstAsps != null){

						List<Soggetto> lstSog = new ArrayList<Soggetto>();
						Set<Soggetto> setSog = new HashSet<Soggetto>();
						List<String> soggettiGiaInseriti = new ArrayList<String>();
						for(AccordoServizioParteSpecifica asps : lstAsps){

							expr = this.fruitoreSearchDAO.newExpression();


							boolean addAnd = false;

							if(asps.getNome() != null){
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME,asps.getNome());
								addAnd = true;
							}

							if( asps.getTipo()!= null){
								if(addAnd)
									expr.and();

								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO,  asps.getTipo());
								addAnd = true;
							}

							if(asps.getIdErogatore()!= null){
								if(addAnd)
									expr.and();

								if(asps.getIdErogatore().getTipo() != null)
									expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO,asps.getIdErogatore().getTipo()).and();

								if(asps.getIdErogatore().getNome() != null)
									expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME,asps.getIdErogatore().getNome()).and();
							}

							expr.ilike(Fruitore.model().ID_FRUITORE.NOME, input.toLowerCase(), LikeMode.ANYWHERE);
							expr.sortOrder(SortOrder.ASC).addOrder(Fruitore.model().ID_FRUITORE.TIPO).addOrder(Fruitore.model().ID_FRUITORE.NOME);

							pagExpr = this.fruitoreSearchDAO.toPaginatedExpression(expr);

							pagExpr.offset(0).limit(LIMIT_SEARCH);

							List<Fruitore> lstFruitori = this.fruitoreSearchDAO.findAll(pagExpr); 

							if(lstFruitori != null){
								for (Fruitore fruitore : lstFruitori) {
									Soggetto s = this.soggettoDAO.get(fruitore.getIdFruitore());

									if(s != null){
										String key = s.getTipoSoggetto() + "/" + s.getNomeSoggetto();
										if(soggettiGiaInseriti.contains(key)==false){
											setSog.add(s);
											soggettiGiaInseriti.add(key);
										}
									}
								}

								if(setSog.size() > 0)
									lstSog.addAll(setSog);


							}				

						}
						return lstSog;
					}


				} else {

					// ne accordo ne erogatore specificati, faccio la find all
					
					expr = this.soggettoDAO.newExpression();

					expr.ilike(Soggetto.model().NOME_SOGGETTO,	input.toLowerCase(), LikeMode.ANYWHERE);

					expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

					IPaginatedExpression pagExpr = this.soggettoDAO
							.toPaginatedExpression(expr);
					pagExpr.offset(0).limit(LIMIT_SEARCH);

					return this.soggettoDAO.findAll(pagExpr);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Soggetto>();
	}

	@Override
	public List<Soggetto> getSoggettiFruitoreFromAccordoServizioAndErogatore(String tipoProtocollo,String tipoServizio ,String nomeServizio, String uriAccordoServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio) {

		IExpression expr;
		try {
			// ho scelto un accordo di servizio
			if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty() ) {
				expr = this.aspsDAO.newExpression();

				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);

				boolean addAnd = false;

				if(idAccordo.getNome() != null){
					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
					addAnd = true;
				}

				if( idAccordo.getVersione() != null){
					if(addAnd)
						expr.and();

					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
					addAnd = true;
				}

				if(idAccordo.getSoggettoReferente() != null){
					if(addAnd)
						expr.and();

					if(idAccordo.getSoggettoReferente().getTipo() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

					if(idAccordo.getSoggettoReferente().getNome() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
				}

				// Se ho specificato il soggetto erogatore
				if(StringUtils.isNotEmpty(tipoErogatore) && StringUtils.isNotEmpty(nomeErogatore)){
					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeErogatore).and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,tipoErogatore);

				}
				if(StringUtils.isNotEmpty(nomeServizio ))
					expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();
				if(StringUtils.isNotEmpty(tipoServizio ))
					expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio).and();
				if(versioneServizio != null)
					expr.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio).and();

				expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

				IPaginatedExpression pagExpr =this.aspsDAO.toPaginatedExpression(expr);

				pagExpr.offset(0).limit(LIMIT_SEARCH);

				List<AccordoServizioParteSpecifica> lstAsps = this.aspsDAO.findAll(pagExpr);

				if(lstAsps != null){

					List<Soggetto> lstSog = new ArrayList<Soggetto>();
					Set<Soggetto> setSog = new HashSet<Soggetto>();
					List<String> soggettiGiaInseriti = new ArrayList<String>();
					for(AccordoServizioParteSpecifica asps : lstAsps){

						expr = this.fruitoreSearchDAO.newExpression();


						addAnd = false;

						if(asps.getNome() != null){
							expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME,asps.getNome());
							addAnd = true;
						}

						if( asps.getTipo()!= null){
							if(addAnd)
								expr.and();

							expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO,  asps.getTipo());
							addAnd = true;
						}

						if(asps.getIdErogatore()!= null){
							if(addAnd)
								expr.and();

							if(asps.getIdErogatore().getTipo() != null)
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO,asps.getIdErogatore().getTipo()).and();

							if(asps.getIdErogatore().getNome() != null)
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME,asps.getIdErogatore().getNome()).and();
						}

						expr.sortOrder(SortOrder.ASC).addOrder(Fruitore.model().ID_FRUITORE.TIPO).addOrder(Fruitore.model().ID_FRUITORE.NOME);

						pagExpr = this.fruitoreSearchDAO.toPaginatedExpression(expr);

						pagExpr.offset(0).limit(LIMIT_SEARCH);

						List<Fruitore> lstFruitori = this.fruitoreSearchDAO.findAll(pagExpr); 

						if(lstFruitori != null){
							for (Fruitore fruitore : lstFruitori) {
								Soggetto s = this.soggettoDAO.get(fruitore.getIdFruitore());

								if(s != null){
									String key = s.getTipoSoggetto() + "/" + s.getNomeSoggetto();
									if(soggettiGiaInseriti.contains(key)==false){
										setSog.add(s);
										soggettiGiaInseriti.add(key);
									}
								}
							}

							if(setSog.size() > 0)
								lstSog.addAll(setSog);


						}				

					}
					return lstSog;
				}

			} else {
				// accordo non specificato
				// Se ho specificato il soggetto erogatore
				if(StringUtils.isNotEmpty(tipoErogatore) && StringUtils.isNotEmpty(nomeErogatore)){
					expr = this.aspsDAO.newExpression();
					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeErogatore).and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,tipoErogatore);

					if(StringUtils.isNotEmpty(nomeServizio ))
						expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();
					if(StringUtils.isNotEmpty(tipoServizio ))
						expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio).and();
					if(versioneServizio != null)
						expr.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio).and();

					expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

					IPaginatedExpression pagExpr =this.aspsDAO.toPaginatedExpression(expr);

					pagExpr.offset(0).limit(LIMIT_SEARCH);

					List<AccordoServizioParteSpecifica> lstAsps = this.aspsDAO.findAll(pagExpr);

					if(lstAsps != null){

						List<Soggetto> lstSog = new ArrayList<Soggetto>();
						Set<Soggetto> setSog = new HashSet<Soggetto>();
						List<String> soggettiGiaInseriti = new ArrayList<String>();
						for(AccordoServizioParteSpecifica asps : lstAsps){

							expr = this.fruitoreSearchDAO.newExpression();


							boolean addAnd = false;

							if(asps.getNome() != null){
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME,asps.getNome());
								addAnd = true;
							}

							if( asps.getTipo()!= null){
								if(addAnd)
									expr.and();

								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO,  asps.getTipo());
								addAnd = true;
							}

							if(asps.getIdErogatore()!= null){
								if(addAnd)
									expr.and();

								if(asps.getIdErogatore().getTipo() != null)
									expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO,asps.getIdErogatore().getTipo()).and();

								if(asps.getIdErogatore().getNome() != null)
									expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME,asps.getIdErogatore().getNome()).and();
							}

							expr.sortOrder(SortOrder.ASC).addOrder(Fruitore.model().ID_FRUITORE.TIPO).addOrder(Fruitore.model().ID_FRUITORE.NOME);

							pagExpr = this.fruitoreSearchDAO.toPaginatedExpression(expr);

							pagExpr.offset(0).limit(LIMIT_SEARCH);

							List<Fruitore> lstFruitori = this.fruitoreSearchDAO.findAll(pagExpr); 

							if(lstFruitori != null){
								for (Fruitore fruitore : lstFruitori) {
									Soggetto s = this.soggettoDAO.get(fruitore.getIdFruitore());

									if(s != null){
										String key = s.getTipoSoggetto() + "/" + s.getNomeSoggetto();
										if(soggettiGiaInseriti.contains(key)==false){
											setSog.add(s);
											soggettiGiaInseriti.add(key);
										}
									}
								}

								if(setSog.size() > 0)
									lstSog.addAll(setSog);


							}				

						}
						return lstSog;
					}


				} else {

					// ne accordo ne erogatore specificati, faccio la find all
					expr = this.soggettoDAO.newExpression();

					expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

					IPaginatedExpression pagExpr = this.soggettoDAO
							.toPaginatedExpression(expr);
					pagExpr.offset(0).limit(LIMIT_SEARCH);

					return this.soggettoDAO.findAll(pagExpr);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Soggetto>();
	}

	@Override
	public int countSoggettiFruitoreFromAccordoServizioErogatoreAndFruitore(String tipoProtocollo,String uriAccordoServizio, String tipoServizio ,String nomeServizio, 
			String tipoErogatore, String nomeErogatore, Integer versioneServizio, String tipoFruitore, String nomeFruitore) {

		IExpression expr;
		try {
			// ho scelto un accordo di servizio
			if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty() ) {
				expr = this.aspsDAO.newExpression();

				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);

				boolean addAnd = false;

				if(idAccordo.getNome() != null){
					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
					addAnd = true;
				}

				if( idAccordo.getVersione() != null){
					if(addAnd)
						expr.and();

					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
					addAnd = true;
				}

				if(idAccordo.getSoggettoReferente() != null){
					if(addAnd)
						expr.and();

					if(idAccordo.getSoggettoReferente().getTipo() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

					if(idAccordo.getSoggettoReferente().getNome() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
				}

				// Se ho specificato il soggetto erogatore
				if(StringUtils.isNotEmpty(tipoErogatore) && StringUtils.isNotEmpty(nomeErogatore)){
					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeErogatore).and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,tipoErogatore);

				}

				if(StringUtils.isNotEmpty(nomeServizio ))
					expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();
				if(StringUtils.isNotEmpty(tipoServizio ))
					expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio).and();
				if(versioneServizio != null)
					expr.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio).and();

				expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

				IPaginatedExpression pagExpr =this.aspsDAO.toPaginatedExpression(expr);

				pagExpr.offset(0).limit(LIMIT_SEARCH);

				List<AccordoServizioParteSpecifica> lstAsps = this.aspsDAO.findAll(pagExpr);

				if(lstAsps != null){
					List<String> soggettiGiaInseriti = new ArrayList<String>();
					for(AccordoServizioParteSpecifica asps : lstAsps){

						expr = this.fruitoreSearchDAO.newExpression();


						addAnd = false;

						if(asps.getNome() != null){
							expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME,asps.getNome());
							addAnd = true;
						}

						if( asps.getTipo()!= null){
							if(addAnd)
								expr.and();

							expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO,  asps.getTipo());
							addAnd = true;
						}

						if(asps.getIdErogatore()!= null){
							if(addAnd)
								expr.and();

							if(asps.getIdErogatore().getTipo() != null)
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO,asps.getIdErogatore().getTipo()).and();

							if(asps.getIdErogatore().getNome() != null)
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME,asps.getIdErogatore().getNome()).and();
						}

						expr.sortOrder(SortOrder.ASC).addOrder(Fruitore.model().ID_FRUITORE.TIPO).addOrder(Fruitore.model().ID_FRUITORE.NOME);
						if(StringUtils.isNotEmpty(tipoFruitore) && StringUtils.isNotEmpty(nomeFruitore)){
							expr.equals(Fruitore.model().ID_FRUITORE.NOME, nomeFruitore).and().equals(Fruitore.model().ID_FRUITORE.TIPO,tipoFruitore);
						}

						pagExpr = this.fruitoreSearchDAO.toPaginatedExpression(expr);

						pagExpr.offset(0).limit(LIMIT_SEARCH);

						List<Fruitore> lstFruitori = this.fruitoreSearchDAO.findAll(pagExpr); 

						if(lstFruitori != null){
							for (Fruitore fruitore : lstFruitori) {
								IdSoggetto idFruitore = fruitore.getIdFruitore();
								String key = idFruitore.getTipo() + "/" + idFruitore.getNome();
								//								if(soggettiGiaInseriti.contains(key)==false){
								soggettiGiaInseriti.add(key);
								//								}
							}
						}				
					}
					return soggettiGiaInseriti.size();
				}

			} else {
				// accordo non specificato
				// Se ho specificato il soggetto erogatore
				if(StringUtils.isNotEmpty(tipoErogatore) && StringUtils.isNotEmpty(nomeErogatore)){
					expr = this.aspsDAO.newExpression();
					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeErogatore).and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,tipoErogatore);
					if(StringUtils.isNotEmpty(nomeServizio ))
						expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();
					if(StringUtils.isNotEmpty(tipoServizio ))
						expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio).and();
					if(versioneServizio != null)
						expr.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio).and();

					expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

					IPaginatedExpression pagExpr =this.aspsDAO.toPaginatedExpression(expr);

					pagExpr.offset(0).limit(LIMIT_SEARCH);

					List<AccordoServizioParteSpecifica> lstAsps = this.aspsDAO.findAll(pagExpr);

					if(lstAsps != null){
						List<String> soggettiGiaInseriti = new ArrayList<String>();
						for(AccordoServizioParteSpecifica asps : lstAsps){

							expr = this.fruitoreSearchDAO.newExpression();


							boolean addAnd = false;

							if(asps.getNome() != null){
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME,asps.getNome());
								addAnd = true;
							}

							if( asps.getTipo()!= null){
								if(addAnd)
									expr.and();

								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO,  asps.getTipo());
								addAnd = true;
							}

							if(asps.getIdErogatore()!= null){
								if(addAnd)
									expr.and();

								if(asps.getIdErogatore().getTipo() != null)
									expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO,asps.getIdErogatore().getTipo()).and();

								if(asps.getIdErogatore().getNome() != null)
									expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME,asps.getIdErogatore().getNome()).and();
							}

							expr.sortOrder(SortOrder.ASC).addOrder(Fruitore.model().ID_FRUITORE.TIPO).addOrder(Fruitore.model().ID_FRUITORE.NOME);
							if(StringUtils.isNotEmpty(tipoFruitore) && StringUtils.isNotEmpty(nomeFruitore)){
								expr.equals(Fruitore.model().ID_FRUITORE.NOME, nomeFruitore).and().equals(Fruitore.model().ID_FRUITORE.TIPO,tipoFruitore);
							}

							pagExpr = this.fruitoreSearchDAO.toPaginatedExpression(expr);

							pagExpr.offset(0).limit(LIMIT_SEARCH);

							List<Fruitore> lstFruitori = this.fruitoreSearchDAO.findAll(pagExpr); 

							if(lstFruitori != null){
								for (Fruitore fruitore : lstFruitori) {
									IdSoggetto idFruitore = fruitore.getIdFruitore();

									String key = idFruitore.getTipo() + "/" + idFruitore.getNome();
									//									if(soggettiGiaInseriti.contains(key)==false){
									soggettiGiaInseriti.add(key);
									//									}

								}
							}				

						}
						return soggettiGiaInseriti.size();
					}


				} else {

					// ne accordo ne erogatore specificati, faccio la find all

					expr = this.fruitoreSearchDAO.newExpression();
					expr.sortOrder(SortOrder.ASC).addOrder(Fruitore.model().ID_FRUITORE.TIPO).addOrder(Fruitore.model().ID_FRUITORE.NOME);
					if(StringUtils.isNotEmpty(tipoFruitore) && StringUtils.isNotEmpty(nomeFruitore)){
						expr.equals(Fruitore.model().ID_FRUITORE.NOME, nomeFruitore).and().equals(Fruitore.model().ID_FRUITORE.TIPO,tipoFruitore);
					}

					if(tipoProtocollo!= null){
						expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.fruitoreSearchDAO, Fruitore.model().ID_FRUITORE.TIPO, tipoProtocollo));
					}

					IPaginatedExpression pagExpr = this.fruitoreSearchDAO.toPaginatedExpression(expr);

					pagExpr.offset(0).limit(LIMIT_SEARCH);

					List<Fruitore> lstFruitori = this.fruitoreSearchDAO.findAll(pagExpr); 

					if(lstFruitori != null){
						List<String> soggettiGiaInseriti = new ArrayList<String>();

						for (Fruitore fruitore : lstFruitori) {

							IdSoggetto idFruitore = fruitore.getIdFruitore();

							String key = idFruitore.getTipo() + "/" + idFruitore.getNome();
							//if(soggettiGiaInseriti.contains(key)==false){
							soggettiGiaInseriti.add(key);
							//							}
						}

						return soggettiGiaInseriti.size();
					}

				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return 0;
	}

	@Override
	public int countFruizioniSoggetto(String tipoProtocollo,String tipoSoggetto, String nomeSoggetto){

		IExpression expr;
		try {
			expr = this.fruitoreSearchDAO.newExpression();

			if(StringUtils.isNotEmpty(tipoSoggetto) && StringUtils.isNotEmpty(nomeSoggetto)){
				expr.equals(Fruitore.model().ID_FRUITORE.NOME, nomeSoggetto).and().equals(Fruitore.model().ID_FRUITORE.TIPO,tipoSoggetto);
			}

			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.fruitoreSearchDAO, Fruitore.model().ID_FRUITORE.TIPO, tipoProtocollo));
			}
			expr.sortOrder(SortOrder.ASC).addOrder(Fruitore.model().ID_FRUITORE.TIPO).addOrder(Fruitore.model().ID_FRUITORE.NOME);

			IPaginatedExpression pagExpr = this.fruitoreSearchDAO.toPaginatedExpression(expr);

			pagExpr.offset(0).limit(LIMIT_SEARCH);

			List<Fruitore> lstFruitori = this.fruitoreSearchDAO.findAll(pagExpr); 

			if(lstFruitori != null){
				List<String> soggettiGiaInseriti = new ArrayList<String>();

				for (Fruitore fruitore : lstFruitori) {

					IdSoggetto idFruitore = fruitore.getIdFruitore();

					String key = idFruitore.getTipo() + "/" + idFruitore.getNome();
					//if(soggettiGiaInseriti.contains(key)==false){
					soggettiGiaInseriti.add(key);
					//							}
				}

				return soggettiGiaInseriti.size();
			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}  
		return 0;
	}

	@Override
	public List<ServizioApplicativo> findElencoServiziApplicativiFruitore(String tipoProtocollo,String uriAccordoServizio,String tipoSoggetto,
			String nomeSoggetto,String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, PermessiUtenteOperatore permessiUtenteOperatore) {

		log.debug("Get Lista Servizi Applicativi [Soggetto Erogatore : " + nomeErogatore	+ "]");
		List<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		try {
			List<PortaDelegata> listaPorte = this.findPorteDelegate(tipoProtocollo, uriAccordoServizio, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio, nomeAzione,permessiUtenteOperatore);

			if(listaPorte != null && listaPorte.size() > 0){
				for (PortaDelegata porta : listaPorte) {
					List<PortaDelegataServizioApplicativo> portaDelegataServizioApplicativoList = porta.getPortaDelegataServizioApplicativoList();
					for (PortaDelegataServizioApplicativo portaDelegataServizioApplicativo : portaDelegataServizioApplicativoList) {
						IdServizioApplicativo idServizioApplicativo = portaDelegataServizioApplicativo.getIdServizioApplicativo();

						ServizioApplicativo servizioApplicativo = this.serviziApplicativiDAO.get(idServizioApplicativo);
						lista.add(servizioApplicativo);
					}

				}
			}
			return lista;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return lista;
	}

	@Override
	public int countElencoServiziApplicativiFruitore(String tipoProtocollo,String uriAccordoServizio,String tipoSoggetto ,
			String nomeSoggetto,String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, PermessiUtenteOperatore permessiUtenteOperatore) {
		log.debug("countElencoServiziApplicativi [Soggetto Erogatore : " + nomeErogatore	+ "]");
		try {
			List<PortaDelegata> listaPorte = this.findPorteDelegate(tipoProtocollo, uriAccordoServizio, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore,versioneServizio, nomeAzione,permessiUtenteOperatore);

			int somma = 0;
			if(listaPorte != null && listaPorte.size() > 0){
				for (PortaDelegata porta : listaPorte) {
					List<PortaDelegataServizioApplicativo> portaDelegataServizioApplicativoList = porta.getPortaDelegataServizioApplicativoList();
					somma += portaDelegataServizioApplicativoList.size();
				}
			}

			return somma;
		}   catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public List<ServizioApplicativo> findElencoServiziApplicativiErogatore(String tipoProtocollo,String uriAccordoServizio,
			String tipoSoggetto ,String nomeSoggetto,String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, PermessiUtenteOperatore permessiUtenteOperatore) {

		log.debug("Get Lista Servizi Applicativi [Soggetto Erogatore : " + nomeErogatore	+ "]");
		List<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		try {
			List<PortaApplicativa> listaPorte = this.findPorteApplicative(tipoProtocollo, uriAccordoServizio, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore,versioneServizio, nomeAzione,permessiUtenteOperatore);

			if(listaPorte != null && listaPorte.size() > 0){
				for (PortaApplicativa porta : listaPorte) {
					List<PortaApplicativaServizioApplicativo> portaApplicativaServizioApplicativoList = porta.getPortaApplicativaServizioApplicativoList();
					for (PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo : portaApplicativaServizioApplicativoList) {
						IdServizioApplicativo idServizioApplicativo = portaApplicativaServizioApplicativo.getIdServizioApplicativo();

						ServizioApplicativo servizioApplicativo = this.serviziApplicativiDAO.get(idServizioApplicativo);
						lista.add(servizioApplicativo);
					}

				}
			}
			return lista;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return lista;
	}

	@Override
	public int countElencoServiziApplicativiErogatore(String tipoProtocollo,String uriAccordoServizio,String tipoSoggetto ,
			String nomeSoggetto,String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, PermessiUtenteOperatore permessiUtenteOperatore) {
		log.debug("countElencoServiziApplicativi [Soggetto Erogatore : " + nomeErogatore	+ "]");
		try {
			List<PortaApplicativa> listaPorte = this.findPorteApplicative(tipoProtocollo, uriAccordoServizio, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio, nomeAzione,permessiUtenteOperatore);

			int somma = 0;
			if(listaPorte != null && listaPorte.size() > 0){
				for (PortaApplicativa porta : listaPorte) {
					List<PortaApplicativaServizioApplicativo> portaApplicativaServizioApplicativoList = porta.getPortaApplicativaServizioApplicativoList();
					somma += portaApplicativaServizioApplicativoList.size();
				}
			}

			return somma;
		}   catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public List<PortaApplicativa> findPorteApplicative(String tipoProtocollo,String uriAccordoServizio,
			String tipoSoggetto ,String nomeSoggetto,String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, PermessiUtenteOperatore permessiUtenteOperatore) {

		log.debug("Get Lista Porta Applicativa [Soggetto Erogatore : " + nomeErogatore	+ "]");
		List<PortaApplicativa> listaPorte = new ArrayList<PortaApplicativa>();

		try {
			IExpression paExpr = this.portaApplicativaDAO.newExpression();
			
			if(permessiUtenteOperatore!=null){
				IExpression permessi = 	permessiUtenteOperatore.toExpressionConfigurazioneServizi(this.portaApplicativaDAO, 
						PortaApplicativa.model().ID_SOGGETTO.TIPO, PortaApplicativa.model().ID_SOGGETTO.NOME, 
						PortaApplicativa.model().ID_SOGGETTO.TIPO, PortaApplicativa.model().ID_SOGGETTO.NOME, 
						PortaApplicativa.model().TIPO_SERVIZIO, PortaApplicativa.model().NOME_SERVIZIO, PortaApplicativa.model().VERSIONE_SERVIZIO,
						false);
				paExpr.and(permessi);
			}

			// Se non ho selezionato il servizio
			if(StringUtils.isEmpty(nomeServizio )) {
				// Accordo servizio selezionato
				if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty()){
					List<AccordoServizioParteSpecifica> servizi = this.getServizi(tipoProtocollo, uriAccordoServizio, tipoErogatore, nomeErogatore);

					if(servizi != null && servizi.size() > 0){
						List<IExpression> lstExpr = new ArrayList<IExpression>();
						for (AccordoServizioParteSpecifica servizio : servizi) {
							IExpression servizioExpr = this.portaApplicativaDAO.newExpression();
							IdSoggetto idErogatore = servizio.getIdErogatore();
							String nome = servizio.getNome();
							String tipo = servizio.getTipo();
							Integer versione = servizio.getVersione();
							servizioExpr.equals(PortaApplicativa.model().NOME_SERVIZIO, nome).and()
							.equals(PortaApplicativa.model().TIPO_SERVIZIO, tipo).and()
							.equals(PortaApplicativa.model().VERSIONE_SERVIZIO, versione).and()
							.equals(PortaApplicativa.model().ID_SOGGETTO.NOME, idErogatore.getNome())
							.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, idErogatore.getTipo());
							if(StringUtils.isNotEmpty(nomeErogatore ))
								servizioExpr.equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeErogatore).and();
							if(StringUtils.isNotEmpty(tipoErogatore ))
								servizioExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoErogatore).and();
							if(StringUtils.isNotEmpty(tipoSoggetto ))
								servizioExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO,		tipoSoggetto);
							if(StringUtils.isNotEmpty(nomeSoggetto ))
								servizioExpr.and().equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeSoggetto);
							if(tipoProtocollo!= null){
								servizioExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.portaApplicativaDAO, PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoProtocollo));
							}
							lstExpr.add(servizioExpr);
						}

						if(lstExpr.size() > 0)
							paExpr.or(lstExpr.toArray(new IExpression[lstExpr.size()])); 
					}
					IPaginatedExpression pagPdExpr = this.portaApplicativaDAO.toPaginatedExpression(paExpr );
					listaPorte = this.portaApplicativaDAO.findAll(pagPdExpr);
					return listaPorte;
				}
			} 
			// Se ho selezionato un servizio, oppure se non ho selezionato niente 

			if(StringUtils.isNotEmpty(nomeServizio ))
				paExpr.equals(PortaApplicativa.model().NOME_SERVIZIO, nomeServizio).and();
			if(StringUtils.isNotEmpty(tipoServizio ))
				paExpr.equals(PortaApplicativa.model().TIPO_SERVIZIO, tipoServizio).and();
			if(versioneServizio != null)
				paExpr.equals(PortaApplicativa.model().VERSIONE_SERVIZIO, versioneServizio).and();
			if(StringUtils.isNotEmpty(nomeErogatore ))
				paExpr.equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeErogatore).and();
			if(StringUtils.isNotEmpty(tipoErogatore ))
				paExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoErogatore).and();
			if(StringUtils.isNotEmpty(tipoSoggetto ))
				paExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO,		tipoSoggetto);
			if(StringUtils.isNotEmpty(nomeSoggetto ))
				paExpr.and().equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeSoggetto);
			if(StringUtils.isNotEmpty(nomeAzione )){
				IExpression azioneExpr =  this.portaApplicativaDAO.newExpression();
				azioneExpr.equals(PortaApplicativa.model().NOME_AZIONE, nomeAzione).or().isNull(PortaApplicativa.model().NOME_AZIONE); 
				paExpr.and(azioneExpr);
			}

			if(tipoProtocollo!= null){
				paExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.portaApplicativaDAO, PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoProtocollo));
			}
			IPaginatedExpression pagPdExpr = this.portaApplicativaDAO.toPaginatedExpression(paExpr );
			listaPorte = this.portaApplicativaDAO.findAll(pagPdExpr);


			return listaPorte;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return listaPorte;
	}

	@Override
	public int countPorteApplicative(String tipoProtocollo,String uriAccordoServizio,String tipoSoggetto ,
			String nomeSoggetto,String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, PermessiUtenteOperatore permessiUtenteOperatore) {
		log.debug("countPorteApplicative [Soggetto Erogatore : " + nomeErogatore	+ "]");
		try {
			IExpression paExpr = this.portaApplicativaDAO.newExpression();
			
			if(permessiUtenteOperatore!=null){
				IExpression permessi = permessiUtenteOperatore.toExpressionConfigurazioneServizi(this.portaApplicativaDAO, 
						PortaApplicativa.model().ID_SOGGETTO.TIPO, PortaApplicativa.model().ID_SOGGETTO.NOME, 
						PortaApplicativa.model().ID_SOGGETTO.TIPO, PortaApplicativa.model().ID_SOGGETTO.NOME, 
						PortaApplicativa.model().TIPO_SERVIZIO, PortaApplicativa.model().NOME_SERVIZIO, PortaApplicativa.model().VERSIONE_SERVIZIO,
						false);
				paExpr.and(permessi);
			}

			// Se non ho selezionato il servizio
			if(StringUtils.isEmpty(nomeServizio )) {
				// Accordo servizio selezionato
				if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty()){
					int count = 0;
					List<AccordoServizioParteSpecifica> servizi = this.getServizi(tipoProtocollo, uriAccordoServizio, tipoErogatore, nomeErogatore);

					if(servizi != null && servizi.size() > 0){

						for (AccordoServizioParteSpecifica servizio : servizi) {
							IExpression servizioExpr = this.portaApplicativaDAO.newExpression();
							IdSoggetto idErogatore = servizio.getIdErogatore();
							String nome = servizio.getNome();
							String tipo = servizio.getTipo();
							Integer versione = servizio.getVersione();
							servizioExpr.equals(PortaApplicativa.model().NOME_SERVIZIO, nome).and()
							.equals(PortaApplicativa.model().TIPO_SERVIZIO, tipo).and()
							.equals(PortaApplicativa.model().VERSIONE_SERVIZIO, versione).and()
							.equals(PortaApplicativa.model().ID_SOGGETTO.NOME, idErogatore.getNome())
							.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, idErogatore.getTipo());
							if(StringUtils.isNotEmpty(nomeErogatore ))
								servizioExpr.equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeErogatore).and();
							if(StringUtils.isNotEmpty(tipoErogatore ))
								servizioExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoErogatore).and();
							if(StringUtils.isNotEmpty(tipoSoggetto ))
								servizioExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO,		 tipoSoggetto);
							if(StringUtils.isNotEmpty(nomeSoggetto ))
								servizioExpr.and().equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeSoggetto);
							if(tipoProtocollo!= null){
								servizioExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.portaApplicativaDAO, PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoProtocollo));
							}
							NonNegativeNumber nn = this.portaApplicativaDAO.count(servizioExpr);
							if(nn!= null)
								count+= nn.longValue();
						}
					}
					return count;
				}
			} 
			// Se ho selezionato un servizio, oppure se non ho selezionato niente 

			if(StringUtils.isNotEmpty(nomeServizio ))
				paExpr.equals(PortaApplicativa.model().NOME_SERVIZIO, nomeServizio).and();
			if(StringUtils.isNotEmpty(tipoServizio ))
				paExpr.equals(PortaApplicativa.model().TIPO_SERVIZIO, tipoServizio).and();
			if(versioneServizio != null)
				paExpr.equals(PortaApplicativa.model().VERSIONE_SERVIZIO, versioneServizio).and();
			if(StringUtils.isNotEmpty(nomeErogatore ))
				paExpr.equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeErogatore).and();
			if(StringUtils.isNotEmpty(tipoErogatore ))
				paExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoErogatore).and();
			if(StringUtils.isNotEmpty(tipoSoggetto ))
				paExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO,		tipoSoggetto);
			if(StringUtils.isNotEmpty(nomeSoggetto ))
				paExpr.and().equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeSoggetto);
			if(StringUtils.isNotEmpty(nomeAzione )){
				IExpression azioneExpr =  this.portaApplicativaDAO.newExpression();
				azioneExpr.equals(PortaApplicativa.model().NOME_AZIONE, nomeAzione).or().isNull(PortaApplicativa.model().NOME_AZIONE); 
				paExpr.and(azioneExpr);
			}

			if(tipoProtocollo!= null){
				paExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.portaApplicativaDAO, PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoProtocollo));
			}

			NonNegativeNumber nnn= this.portaApplicativaDAO.count(paExpr);

			if(nnn!= null)
				return (int) nnn.longValue();
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return 0;
	}

	@Override
	public List<PortaDelegata> findPorteDelegate(String tipoProtocollo,String uriAccordoServizio,String tipoSoggetto ,
			String nomeSoggetto,String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, PermessiUtenteOperatore permessiUtenteOperatore) {

		log.debug("Get Lista Porta Delegate [Soggetto Erogatore : " + nomeErogatore	+ "]");
		List<PortaDelegata> listaPorte = new ArrayList<PortaDelegata>();

		try {
			IExpression pdExpr = this.portaDelegataDAO.newExpression();
			
			if(permessiUtenteOperatore!=null){
				IExpression permessi = permessiUtenteOperatore.toExpressionConfigurazioneServizi(this.portaDelegataDAO, 
						PortaDelegata.model().ID_SOGGETTO.TIPO, PortaDelegata.model().ID_SOGGETTO.NOME, 
						PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, PortaDelegata.model().NOME_SOGGETTO_EROGATORE, 
						PortaDelegata.model().TIPO_SERVIZIO, PortaDelegata.model().NOME_SERVIZIO, PortaDelegata.model().VERSIONE_SERVIZIO,
						false);
				pdExpr.and(permessi);
			}

			// Se non ho selezionato il servizio
			if(StringUtils.isEmpty(nomeServizio )) {
				// Accordo servizio selezionato
				if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty()){
					List<AccordoServizioParteSpecifica> servizi = this.getServizi(tipoProtocollo, uriAccordoServizio, tipoErogatore, nomeErogatore);

					if(servizi != null && servizi.size() > 0){
						List<IExpression> lstExpr = new ArrayList<IExpression>();
						for (AccordoServizioParteSpecifica servizio : servizi) {
							IExpression servizioExpr = this.portaDelegataDAO.newExpression();
							IdSoggetto idErogatore = servizio.getIdErogatore();
							String nome = servizio.getNome();
							String tipo = servizio.getTipo();
							Integer versione = servizio.getVersione();
							servizioExpr.equals(PortaDelegata.model().NOME_SERVIZIO, nome).and()
							.equals(PortaDelegata.model().TIPO_SERVIZIO, tipo).and()
							.equals(PortaDelegata.model().VERSIONE_SERVIZIO, versione).and()
							.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, idErogatore.getNome())
							.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, idErogatore.getTipo());
							if(StringUtils.isNotEmpty(nomeErogatore ))
								servizioExpr.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, nomeErogatore).and();
							if(StringUtils.isNotEmpty(tipoErogatore ))
								servizioExpr.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, tipoErogatore).and();
							if(StringUtils.isNotEmpty(tipoSoggetto ))
								servizioExpr.equals(PortaDelegata.model().ID_SOGGETTO.TIPO,		tipoSoggetto);
							if(StringUtils.isNotEmpty(nomeSoggetto ))
								servizioExpr.and().equals(PortaDelegata.model().ID_SOGGETTO.NOME, nomeSoggetto);
							if(tipoProtocollo!= null){
								servizioExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.portaDelegataDAO, PortaDelegata.model().ID_SOGGETTO.TIPO, tipoProtocollo));
							}

							lstExpr.add(servizioExpr);
						}

						if(lstExpr.size() > 0)
							pdExpr.or(lstExpr.toArray(new IExpression[lstExpr.size()])); 
					}
					IPaginatedExpression pagPdExpr = this.portaDelegataDAO.toPaginatedExpression(pdExpr );
					listaPorte = this.portaDelegataDAO.findAll(pagPdExpr);
					return listaPorte;
				}
			} 
			// Se ho selezionato un servizio, oppure se non ho selezionato niente 

			if(StringUtils.isNotEmpty(nomeServizio ))
				pdExpr.equals(PortaDelegata.model().NOME_SERVIZIO, nomeServizio).and();
			if(StringUtils.isNotEmpty(tipoServizio ))
				pdExpr.equals(PortaDelegata.model().TIPO_SERVIZIO, tipoServizio).and();
			if(versioneServizio != null)
				pdExpr.equals(PortaDelegata.model().VERSIONE_SERVIZIO, versioneServizio).and();
			if(StringUtils.isNotEmpty(nomeErogatore ))
				pdExpr.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, nomeErogatore).and();
			if(StringUtils.isNotEmpty(tipoErogatore ))
				pdExpr.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, tipoErogatore).and();
			if(StringUtils.isNotEmpty(tipoSoggetto ))
				pdExpr.equals(PortaDelegata.model().ID_SOGGETTO.TIPO,		tipoSoggetto);
			if(StringUtils.isNotEmpty(nomeSoggetto ))
				pdExpr.and().equals(PortaDelegata.model().ID_SOGGETTO.NOME, nomeSoggetto);
			if(StringUtils.isNotEmpty(nomeAzione )){
				IExpression azioneExpr =  this.portaDelegataDAO.newExpression();
				azioneExpr.equals(PortaDelegata.model().NOME_AZIONE, nomeAzione).or().isNull(PortaDelegata.model().NOME_AZIONE); 
				pdExpr.and(azioneExpr);
			}

			if(tipoProtocollo!= null){
				pdExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.portaDelegataDAO, PortaDelegata.model().ID_SOGGETTO.TIPO, tipoProtocollo));
			}

			IPaginatedExpression pagPdExpr = this.portaDelegataDAO.toPaginatedExpression(pdExpr );
			listaPorte = this.portaDelegataDAO.findAll(pagPdExpr);

			return listaPorte;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return listaPorte;
	}

	@Override
	public int countPorteDelegate(String tipoProtocollo,String uriAccordoServizio,String tipoSoggetto ,String nomeSoggetto,String tipoServizio ,String nomeServizio,
			String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, PermessiUtenteOperatore permessiUtenteOperatore) {
		log.debug("countPorteDelegate [Soggetto Erogatore : " + nomeErogatore	+ "]");
		try {
			IExpression pdExpr = this.portaDelegataDAO.newExpression();
			
			if(permessiUtenteOperatore != null){
				IExpression permessi = permessiUtenteOperatore.toExpressionConfigurazioneServizi(this.portaDelegataDAO, 
					PortaDelegata.model().ID_SOGGETTO.TIPO, PortaDelegata.model().ID_SOGGETTO.NOME, 
					PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, PortaDelegata.model().NOME_SOGGETTO_EROGATORE, 
					PortaDelegata.model().TIPO_SERVIZIO, PortaDelegata.model().NOME_SERVIZIO, PortaDelegata.model().VERSIONE_SERVIZIO,
					false);
				pdExpr.and(permessi);
			}

			// Se non ho selezionato il servizio
			if(StringUtils.isEmpty(nomeServizio )) {
				// Accordo servizio selezionato
				if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty()){
					int count = 0;
					List<AccordoServizioParteSpecifica> servizi = this.getServizi(tipoProtocollo, uriAccordoServizio, tipoErogatore, nomeErogatore);

					if(servizi != null && servizi.size() > 0){

						for (AccordoServizioParteSpecifica servizio : servizi) {
							IExpression servizioExpr = this.portaDelegataDAO.newExpression();
							IdSoggetto idErogatore = servizio.getIdErogatore();
							String nome = servizio.getNome();
							String tipo = servizio.getTipo();
							Integer versione = servizio.getVersione();
							servizioExpr.equals(PortaDelegata.model().NOME_SERVIZIO, nome).and()
							.equals(PortaDelegata.model().TIPO_SERVIZIO, tipo).and()
							.equals(PortaDelegata.model().VERSIONE_SERVIZIO, versione).and()
							.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, idErogatore.getNome())
							.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, idErogatore.getTipo());
							if(StringUtils.isNotEmpty(nomeErogatore ))
								servizioExpr.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, nomeErogatore).and();
							if(StringUtils.isNotEmpty(tipoErogatore ))
								servizioExpr.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, tipoErogatore).and();
							if(StringUtils.isNotEmpty(tipoSoggetto ))
								servizioExpr.equals(PortaDelegata.model().ID_SOGGETTO.TIPO,		tipoSoggetto);
							if(StringUtils.isNotEmpty(nomeSoggetto ))
								servizioExpr.and().equals(PortaDelegata.model().ID_SOGGETTO.NOME, nomeSoggetto);
							if(tipoProtocollo!= null){
								servizioExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.portaDelegataDAO, PortaDelegata.model().ID_SOGGETTO.TIPO, tipoProtocollo));
							}
							NonNegativeNumber nn = this.portaDelegataDAO.count(servizioExpr);
							if(nn!= null)
								count+= nn.longValue();
						}
					}
					return count;
				}
			} 
			// Se ho selezionato un servizio, oppure se non ho selezionato niente 

			if(StringUtils.isNotEmpty(nomeServizio ))
				pdExpr.equals(PortaDelegata.model().NOME_SERVIZIO, nomeServizio).and();
			if(StringUtils.isNotEmpty(tipoServizio ))
				pdExpr.equals(PortaDelegata.model().TIPO_SERVIZIO, tipoServizio).and();
			if(versioneServizio != null)
				pdExpr.equals(PortaDelegata.model().VERSIONE_SERVIZIO, versioneServizio).and();
			if(StringUtils.isNotEmpty(nomeErogatore ))
				pdExpr.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, nomeErogatore).and();
			if(StringUtils.isNotEmpty(tipoErogatore ))
				pdExpr.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, tipoErogatore).and();
			if(StringUtils.isNotEmpty(tipoSoggetto ))
				pdExpr.equals(PortaDelegata.model().ID_SOGGETTO.TIPO,		tipoSoggetto);
			if(StringUtils.isNotEmpty(nomeSoggetto ))
				pdExpr.and().equals(PortaDelegata.model().ID_SOGGETTO.NOME, nomeSoggetto);
			if(StringUtils.isNotEmpty(nomeAzione )){
				IExpression azioneExpr =  this.portaDelegataDAO.newExpression();
				azioneExpr.equals(PortaDelegata.model().NOME_AZIONE, nomeAzione).or().isNull(PortaDelegata.model().NOME_AZIONE); 
				pdExpr.and(azioneExpr);
			}


			if(tipoProtocollo!= null){
				pdExpr.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(this.portaDelegataDAO, PortaDelegata.model().ID_SOGGETTO.TIPO, tipoProtocollo));
			}

			NonNegativeNumber nnn= this.portaDelegataDAO.count(pdExpr);

			if(nnn!= null)
				return (int) nnn.longValue();
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return 0;
	}

	
}
