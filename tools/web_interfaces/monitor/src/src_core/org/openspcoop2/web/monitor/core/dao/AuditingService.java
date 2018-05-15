package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.core.commons.dao.DAO;
import org.openspcoop2.core.commons.dao.DAOFactory;
import it.link.pdd.core.auditing.Audit;
import it.link.pdd.core.auditing.Configuration;
import it.link.pdd.core.auditing.dao.IAuditService;
import it.link.pdd.core.auditing.dao.IAuditServiceSearch;
import it.link.pdd.core.auditing.dao.IConfigurationService;
import it.link.pdd.core.auditing.dao.IConfigurationServiceSearch;
import it.link.pdd.core.auditing.dao.IDBAuditService;
import it.link.pdd.core.auditing.dao.IDBAuditServiceSearch;
import it.link.pdd.core.auditing.dao.IServiceManager;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.springframework.transaction.annotation.Transactional;

public class AuditingService implements IAuditingService{

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();

	private IServiceManager auditServiceManager = null;
	private IAuditServiceSearch auditServiceSearch = null;
	private IAuditService auditService = null;
	private IConfigurationServiceSearch configServiceSearch = null;
	private IConfigurationService configService = null;
	
	public AuditingService(){
		try {
			this.auditServiceManager = (it.link.pdd.core.auditing.dao.IServiceManager) DAOFactory
					.getInstance(AuditingService.log).getServiceManager(DAO.AUDITING,AuditingService.log);
			this.auditServiceSearch = this.auditServiceManager.getAuditServiceSearch();
			this.auditService = this.auditServiceManager.getAuditService();
			this.configServiceSearch = this.auditServiceManager.getConfigurationServiceSearch();
			this.configService = this.auditServiceManager.getConfigurationService();
		} catch (Exception e) {
			AuditingService.log.error(e.getMessage(), e);
		}
	}
	

	@Override
	public List<Audit> findAll(int start, int limit) {
		
		try{
		
			IPaginatedExpression pagExpr = this.auditServiceSearch.newPaginatedExpression();
			pagExpr.offset(start).limit(limit);
			return this.auditServiceSearch.findAll(pagExpr);
			
		} catch (Exception e) {
			AuditingService.log.error(e.getMessage(), e);
			return null;
		}
		
	}

	@Override
	public int totalCount() {
		try{
			return (int) this.auditServiceSearch.count(this.auditServiceSearch.newExpression()).longValue();
		}catch (Exception e){
			AuditingService.log.error(e.getMessage(), e);
		}
		
		return 0;
	}

	@Override
	public void delete(Audit obj) throws Exception {
		try{
			this.auditService.delete(obj);
		}catch(Exception e){
			AuditingService.log.error(e.getMessage(), e);
		}
	}

	@Override
	public void deleteById(Integer key) {
		try{
			((IDBAuditService)this.auditService).deleteById(key);
		}catch(Exception e){
			AuditingService.log.error(e.getMessage(), e);
		}
	}

	@Override
	public List<Audit> findAll() {
		try{
			IPaginatedExpression pagExpr = this.auditServiceSearch.newPaginatedExpression();
			return this.auditServiceSearch.findAll(pagExpr);
		}catch(Exception e){
			AuditingService.log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public Audit findById(Integer key) {
		try{
			return ((IDBAuditServiceSearch)this.auditServiceSearch).get(key);
		}catch(Exception e){
			AuditingService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public void store(Audit obj) throws Exception {
		try{
			obj.setExecutionTime(Calendar.getInstance().getTime());
			this.auditService.create(obj);
		}catch(Exception e){
			AuditingService.log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public boolean isAuditEnabled() {
		try{
			Configuration c = this.configServiceSearch.get();		
			return c.getDefaultEnabled();
		}catch(Exception e){
			AuditingService.log.error(e.getMessage(), e);
		}
		return false;
		
	}

	@Override
	public boolean isSerializeObject() {
		try{
			Configuration c = this.configServiceSearch.get();					
			return c.getDefaultSerializedObject();
		}catch(Exception e){
			AuditingService.log.error(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public Configuration readConfiguration() {

		try{
			return this.configServiceSearch.get();
		}catch(Exception e){
			AuditingService.log.error(e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	@Transactional
	public void saveConfiguration(Configuration config) {
		try{
			if(this.configServiceSearch.exists()){
				this.configService.update(config);
			}else{
				this.configService.create(config);
			}
		}catch(Exception e){
			AuditingService.log.error(e.getMessage(), e);
		}
	}

	@Override
	public void deleteAll() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
