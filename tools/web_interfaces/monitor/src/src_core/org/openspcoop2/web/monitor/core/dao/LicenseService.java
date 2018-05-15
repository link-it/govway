package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.search.GeneralInfo;
import org.openspcoop2.core.commons.search.dao.IGeneralInfoService;
import org.openspcoop2.core.commons.search.dao.IGeneralInfoServiceSearch;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.exception.ServiceException;

public class LicenseService implements ILicenseService {

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();
	
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;
	
	private IGeneralInfoServiceSearch generalInfoSearchDAO;
	private IGeneralInfoService generalInfoDAO;
	
	public LicenseService(){
		
		try {
			// init Service Manager utils
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory.getInstance(LicenseService.log).getServiceManager(DAO.UTILS,LicenseService.log);
			
			this.generalInfoSearchDAO = this.utilsServiceManager.getGeneralInfoServiceSearch();
			this.generalInfoDAO = this.utilsServiceManager.getGeneralInfoService();

		} catch (Exception e) {
			LicenseService.log.error(e.getMessage(), e);
		}
		
	}

	@Override
	public List<GeneralInfo> findAll(int start, int limit) {
		return null;
	}

	@Override
	public int totalCount() {
		return 0;
	}

	@Override
	public void store(GeneralInfo obj) throws Exception {
		this.generalInfoDAO.updateOrCreate(obj);
	}
	
	@Override
	public void deleteById(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(GeneralInfo obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GeneralInfo findById(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GeneralInfo> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public GeneralInfo leggiLicenza() throws ServiceException {
		try{
			return this.generalInfoSearchDAO.get();
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
	}

}
