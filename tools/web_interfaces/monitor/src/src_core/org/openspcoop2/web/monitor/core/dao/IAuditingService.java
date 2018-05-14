package org.openspcoop2.web.monitor.core.dao;

import it.link.pdd.core.auditing.Audit;
import it.link.pdd.core.auditing.Configuration;
import org.openspcoop2.web.monitor.core.dao.IService;

public interface IAuditingService extends IService<Audit, Integer> {

	public boolean isAuditEnabled();
	public boolean isSerializeObject();
	
	public Configuration readConfiguration();
	public void saveConfiguration(Configuration config);
	
}
