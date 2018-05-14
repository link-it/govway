package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.core.commons.search.GeneralInfo;
import org.openspcoop2.web.monitor.core.dao.IService;

import org.openspcoop2.generic_project.exception.ServiceException;

public interface ILicenseService extends IService<GeneralInfo, String>{

	public GeneralInfo leggiLicenza() throws ServiceException;
	
}
